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

    // Variáveis de apoio
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

        // Associa o estoque ao usuário (essencial para testes de permissão)
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
        // Arrange
        ProdutoCadastroDTO cadastroDTO = new ProdutoCadastroDTO("Novo Prod", "Nova Marca", "Desc", ESTOQUE_ID);
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(converter.cadastroParaProdutoEntity(cadastroDTO)).thenReturn(produto);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(repository.save(any(Produto.class))).thenReturn(produto);
        when(converter.entityParaResponseDTO(any(Produto.class), any(Estoque.class)))
                .thenReturn(new ProdutoResponseDTO(PRODUTO_ID, "Novo Prod", "Nova Marca", "Desc", 0, null));

        // Act
        ProdutoResponseDTO resultado = produtoService.criarProduto(cadastroDTO, TOKEN);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo("Novo Prod");

        // Verifica se as validações de estoque foram chamadas
        verify(estoqueService, times(1)).verificarEstoqueAtivo(estoque);
        verify(estoqueService, times(1)).verificarEstoqueUsuario(estoque, usuario);
        verify(repository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar produto em estoque de outro usuário")
    void criarProduto_EmEstoqueDeOutroUsuario_LancaAcaoNaoPermitidaException() {
        // Arrange
        ProdutoCadastroDTO cadastroDTO = new ProdutoCadastroDTO("Prod", "Marca", "Desc", ESTOQUE_ID);
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        // Simula a falha na validação de permissão
        doThrow(new AcaoNaoPermitidaException("")).when(estoqueService).verificarEstoqueUsuario(estoque, usuario);

        // Act & Assert
        assertThatThrownBy(() -> produtoService.criarProduto(cadastroDTO, TOKEN))
                .isInstanceOf(AcaoNaoPermitidaException.class);

        // Garante que o produto nunca foi salvo
        verify(repository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void atualizarProduto_ComDadosValidos_SalvaElusciousProdutoResponseDTO() {
        // Arrange
        ProdutoAtualizacaoDTO atualizacaoDTO = new ProdutoAtualizacaoDTO("Nome Atualizado", "Marca Atualizada", "Desc Atualizada");
        when(repository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(repository.save(any(Produto.class))).thenReturn(produto);
        when(converter.entityParaResponseDTO(any(Produto.class), any(Estoque.class)))
                .thenReturn(new ProdutoResponseDTO(PRODUTO_ID, "Nome Atualizado", null, null, 0, null));

        // Act
        produtoService.atualizarProduto(PRODUTO_ID, atualizacaoDTO, TOKEN);

        // Assert
        // Captura o objeto produto que foi passado para o método save
        ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);
        verify(repository).save(produtoCaptor.capture());
        Produto produtoSalvo = produtoCaptor.getValue();

        // Verifica se os dados foram atualizados antes de salvar
        assertThat(produtoSalvo.getNome()).isEqualTo("Nome Atualizado");
        assertThat(produtoSalvo.getMarca()).isEqualTo("Marca Atualizada");
        assertThat(produtoSalvo.getDescricao()).isEqualTo("Desc Atualizada");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar produto sem permissão")
    void atualizarProduto_SemPermissaoDoUsuario_LancaAcaoNaoPermitidaException() {
        // Arrange
        Usuario outroUsuario = new Usuario(); // Usuário diferente, sem o estoque
        outroUsuario.setId(2L);
        outroUsuario.setEstoques(new ArrayList<>()); // Lista de estoques vazia

        ProdutoAtualizacaoDTO dto = new ProdutoAtualizacaoDTO("Nome", null, null);
        when(repository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));
        when(usuarioService.findByToken(TOKEN)).thenReturn(outroUsuario);

        // Act & Assert
        assertThatThrownBy(() -> produtoService.atualizarProduto(PRODUTO_ID, dto, TOKEN))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("Você não tem permissão para realizar essa ação.");
    }

    @Test
    @DisplayName("Deve buscar todos os produtos de um usuário, filtrando inativos")
    void buscarTodosProdutosUsuario_QuandoChamado_RetornaApenasProdutosAtivosDeEstoquesAtivos() {
        // Arrange
        // Cria produtos para simular o filtro
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
        // Simula o conversor apenas para o produto que deve passar no filtro
        when(converter.entityParaResponseDTO(produtoAtivoEstoqueAtivo, estoque))
                .thenReturn(new ProdutoResponseDTO(PRODUTO_ID, null, null, null, 0, null));

        // Act
        List<ProdutoResponseDTO> resultado = produtoService.buscarTodosProdutosUsuario(TOKEN);

        // Assert
        // A lista deve conter apenas 1 item, pois os outros foram filtrados
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).id()).isEqualTo(PRODUTO_ID);
    }

    @Test
    @DisplayName("Deve desativar um produto com sucesso")
    void desativarProduto_ComIdValidoEPermissao_AlteraStatusParaInativo() {
        // Arrange
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(repository.findById(PRODUTO_ID)).thenReturn(Optional.of(produto));

        // Act
        produtoService.desativarProduto(PRODUTO_ID, TOKEN);

        // Assert
        // Captura o produto para verificar se o status 'ativo' foi alterado para false
        ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);
        verify(repository).save(produtoCaptor.capture());

        assertThat(produtoCaptor.getValue().getAtivo()).isFalse();
    }

    @Test
    @DisplayName("buscarProdutoId deve lançar exceção quando produto não é encontrado")
    void buscarProdutoId_ComIdInexistente_LancaObjetoNaoEncontradoException() {
        // Arrange
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> produtoService.buscarProdutoId(99L))
                .isInstanceOf(ObjetoNaoEncontradoException.class)
                .hasMessage("Produto não encontrado");
    }

    @Test
    @DisplayName("verificarProdutoAtivo deve lançar exceção para produto inativo")
    void verificarProdutoAtivo_QuandoProdutoInativo_LancaDadoIrregularException() {
        // Arrange
        produto.setAtivo(false);

        // Act & Assert
        assertThatThrownBy(() -> produtoService.verificarProdutoAtivo(produto))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("O produto foi desativado.");
    }
}
