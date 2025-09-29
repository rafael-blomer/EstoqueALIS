package br.com.rafaelblomer.services;

import br.com.rafaelblomer.business.EstoqueService;
import br.com.rafaelblomer.business.ProdutoService;
import br.com.rafaelblomer.business.UsuarioService;
import br.com.rafaelblomer.business.converters.ProdutoConverter;
import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @InjectMocks
    private ProdutoService produtoService;

    @Mock
    private ProdutoRepository repository;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private ProdutoConverter converter;
    @Mock
    private EstoqueService estoqueService;

    private Usuario usuario;
    private Estoque estoque;
    private Produto produto;
    private final String TOKEN = "Bearer fake-token";
    private final Long USUARIO_ID = 1L;
    private final Long ESTOQUE_ID = 1L;
    private final Long PRODUTO_ID = 1L;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(USUARIO_ID);

        estoque = new Estoque("Estoque Principal", usuario);
        estoque.setId(ESTOQUE_ID);
        estoque.setAtivo(true);

        usuario.setEstoques(new ArrayList<>(List.of(estoque)));

        produto = new Produto();
        produto.setId(PRODUTO_ID);
        produto.setNome("Produto Teste");
        produto.setMarca("Marca Teste");
        produto.setDescricao("Descrição Teste");
        produto.setAtivo(true);
        produto.setEstoque(estoque);
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso")
    void criarProduto_ComDadosValidos_RetornaProdutoResponseDTO() {
        ProdutoCadastroDTO cadastroDTO = new ProdutoCadastroDTO("Novo Prod", "Nova Marca", "Desc", ESTOQUE_ID);
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(converter.cadastroParaProdutoEntity(cadastroDTO)).thenReturn(produto);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(repository.save(any(Produto.class))).thenReturn(produto);
        when(converter.entityParaResponseDTO(any(Produto.class)))
                .thenReturn(new ProdutoResponseDTO(PRODUTO_ID, "Novo Prod", "Nova Marca", "Desc", 0, null));

        ProdutoResponseDTO resultado = produtoService.criarProduto(cadastroDTO, TOKEN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo("Novo Prod");

        verify(estoqueService, times(1)).verificarEstoqueAtivo(estoque);
        verify(estoqueService, times(1)).verificarEstoqueUsuario(estoque, usuario);
        verify(repository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar produto em estoque de outro usuário")
    void criarProduto_EmEstoqueDeOutroUsuario_LancaAcaoNaoPermitidaException() {
        ProdutoCadastroDTO cadastroDTO = new ProdutoCadastroDTO("Prod", "Marca", "Desc", ESTOQUE_ID);
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        doThrow(new AcaoNaoPermitidaException("")).when(estoqueService).verificarEstoqueUsuario(estoque, usuario);

        assertThatThrownBy(() -> produtoService.criarProduto(cadastroDTO, TOKEN))
                .isInstanceOf(AcaoNaoPermitidaException.class);

        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void atualizarProduto_ComDadosValidos_SalvaElusciousProdutoResponseDTO() {
        ProdutoAtualizacaoDTO atualizacaoDTO = new ProdutoAtualizacaoDTO("Nome Atualizado", "Marca Atualizada", "Desc Atualizada");
        when(repository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(repository.save(any(Produto.class))).thenReturn(produto);
        when(converter.entityParaResponseDTO(any(Produto.class)))
                .thenReturn(new ProdutoResponseDTO(PRODUTO_ID, "Nome Atualizado", null, null, 0, null));

        produtoService.atualizarProduto(PRODUTO_ID, atualizacaoDTO, TOKEN);

        ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);
        verify(repository).save(produtoCaptor.capture());
        Produto produtoSalvo = produtoCaptor.getValue();

        assertThat(produtoSalvo.getNome()).isEqualTo("Nome Atualizado");
        assertThat(produtoSalvo.getMarca()).isEqualTo("Marca Atualizada");
        assertThat(produtoSalvo.getDescricao()).isEqualTo("Desc Atualizada");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar produto sem permissão")
    void atualizarProduto_SemPermissaoDoUsuario_LancaAcaoNaoPermitidaException() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setEstoques(new ArrayList<>());

        ProdutoAtualizacaoDTO dto = new ProdutoAtualizacaoDTO("Nome", null, null);
        when(repository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));
        when(usuarioService.findByToken(TOKEN)).thenReturn(outroUsuario);

        assertThatThrownBy(() -> produtoService.atualizarProduto(PRODUTO_ID, dto, TOKEN))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("Você não tem permissão para realizar essa ação.");
    }

    @Test
    @DisplayName("Deve buscar todos os produtos de um usuário, filtrando inativos")
    void buscarTodosProdutosUsuario_QuandoChamado_RetornaApenasProdutosAtivosDeEstoquesAtivos() {
        Produto produtoAtivoEstoqueAtivo = produto; // Do setup
        Produto produtoInativo = new Produto();
        produtoInativo.setAtivo(false);
        produtoInativo.setEstoque(estoque);

        Estoque estoqueInativo = new Estoque("Inativo", usuario);
        estoqueInativo.setAtivo(false);
        Produto produtoAtivoEstoqueInativo = new Produto();
        produtoAtivoEstoqueInativo.setAtivo(true);
        produtoAtivoEstoqueInativo.setEstoque(estoqueInativo);

        List<Produto> todosOsProdutos = List.of(produtoAtivoEstoqueAtivo, produtoInativo, produtoAtivoEstoqueInativo);

        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(repository.findByEstoqueUsuarioId(USUARIO_ID)).thenReturn(todosOsProdutos);
        when(converter.entityParaResponseDTO(produtoAtivoEstoqueAtivo))
                .thenReturn(new ProdutoResponseDTO(PRODUTO_ID, null, null, null, 0, null));

        List<ProdutoResponseDTO> resultado = produtoService.buscarTodosProdutosUsuario(TOKEN);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().id()).isEqualTo(PRODUTO_ID);
    }

    @Test
    @DisplayName("Deve desativar um produto com sucesso")
    void desativarProduto_ComIdValidoEPermissao_AlteraStatusParaInativo() {
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(repository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));

        produtoService.desativarProduto(PRODUTO_ID, TOKEN);

        ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);
        verify(repository).save(produtoCaptor.capture());

        assertThat(produtoCaptor.getValue().getAtivo()).isFalse();
    }

    @Test
    @DisplayName("buscarProdutoId deve lançar exceção quando produto não é encontrado")
    void buscarProdutoId_ComIdInexistente_LancaObjetoNaoEncontradoException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.buscarProdutoId(99L))
                .isInstanceOf(ObjetoNaoEncontradoException.class)
                .hasMessage("Produto não encontrado");
    }

    @Test
    @DisplayName("verificarProdutoAtivo deve lançar exceção para produto inativo")
    void verificarProdutoAtivo_QuandoProdutoInativo_LancaDadoIrregularException() {
        produto.setAtivo(false);

        assertThatThrownBy(() -> produtoService.verificarProdutoAtivo(produto))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("O produto foi desativado.");
    }
}
