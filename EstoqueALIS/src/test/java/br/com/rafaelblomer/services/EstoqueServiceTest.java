package br.com.rafaelblomer.services;

import br.com.rafaelblomer.business.EstoqueService;
import br.com.rafaelblomer.business.UsuarioService;
import br.com.rafaelblomer.business.converters.EstoqueConverter;
import br.com.rafaelblomer.business.dtos.EstoqueCadastroDTO;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.event.EstoqueExcluidoEvent;
import br.com.rafaelblomer.infrastructure.repositories.EstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @InjectMocks
    private EstoqueService estoqueService;

    @Mock
    private EstoqueRepository repository;

    @Mock
    private EstoqueConverter converter;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ApplicationEventPublisher publisher;

    private Usuario usuario;
    private Estoque estoque;
    private final String TOKEN = "fake-token";
    private final Long ESTOQUE_ID = 1L;
    private final Long USUARIO_ID = 1L;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(USUARIO_ID);
        usuario.setNome("Usuário Teste");

        estoque = new Estoque("Estoque Principal", usuario);
        estoque.setId(ESTOQUE_ID);
        estoque.setAtivo(true);
    }

    @Test
    @DisplayName("Deve criar um novo estoque com sucesso")
    void criarNovoEstoque_ComDadosValidos_RetornaEstoqueResponseDTO() {
        EstoqueCadastroDTO cadastroDTO = new EstoqueCadastroDTO("Meu Estoque");
        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO(ESTOQUE_ID, "Meu Estoque", USUARIO_ID);

        when(usuarioService.findByToken(any(String.class))).thenReturn(usuario);
        when(repository.save(any(Estoque.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(converter.entityParaResponseDTO(any(Estoque.class))).thenReturn(responseDTO);

        EstoqueResponseDTO resultado = estoqueService.criarNovoEstoque(TOKEN, cadastroDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.nomeEstoque()).isEqualTo("Meu Estoque");
        assertThat(resultado.usuarioId()).isEqualTo(USUARIO_ID);

        verify(usuarioService, times(1)).findByToken(TOKEN);
        verify(repository, times(1)).save(any(Estoque.class));
        verify(converter, times(1)).entityParaResponseDTO(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve desativar um estoque com sucesso")
    void desativarEstoque_ComIdValidoEUsuarioCorreto_DesativaComSucesso() {
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.of(estoque));

        estoqueService.desativarEstoque(TOKEN, ESTOQUE_ID);

        verify(repository, times(1)).save(estoque);
        assertThat(estoque.getAtivo()).isFalse();

        verify(publisher, times(1)).publishEvent(any(EstoqueExcluidoEvent.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar estoque de outro usuário")
    void desativarEstoque_ComUsuarioDiferente_LancaAcaoNaoPermitidaException() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);

        when(usuarioService.findByToken(TOKEN)).thenReturn(outroUsuario);
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.of(estoque));

        assertThatThrownBy(() -> estoqueService.desativarEstoque(TOKEN, ESTOQUE_ID))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("O usuário não tem permissão para fazer essa ação.");

        verify(repository, never()).save(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar estoque inexistente")
    void desativarEstoque_ComIdInexistente_LancaObjetoNaoEncontradoException() {
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.desativarEstoque(TOKEN, ESTOQUE_ID))
                .isInstanceOf(ObjetoNaoEncontradoException.class)
                .hasMessage("Estoque não foi encontrado.");
    }

    @Test
    @DisplayName("buscarEstoqueEntityId deve retornar Estoque quando encontrado")
    void buscarEstoqueEntityId_QuandoEncontrado_RetornaEstoque() {
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.of(estoque));

        Estoque resultado = estoqueService.buscarEstoqueEntityId(ESTOQUE_ID);

        assertThat(resultado).isEqualTo(estoque);
    }

    @Test
    @DisplayName("buscarEstoqueEntityId deve lançar exceção quando não encontrado")
    void buscarEstoqueEntityId_QuandoNaoEncontrado_LancaObjetoNaoEncontradoException() {
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.buscarEstoqueEntityId(ESTOQUE_ID))
                .isInstanceOf(ObjetoNaoEncontradoException.class)
                .hasMessage("Estoque não foi encontrado.");
    }

    @Test
    @DisplayName("verificarEstoqueAtivo não deve fazer nada se estoque estiver ativo")
    void verificarEstoqueAtivo_QuandoEstoqueAtivo_NaoLancaExcecao() {
        estoque.setAtivo(true);

        estoqueService.verificarEstoqueAtivo(estoque);
    }

    @Test
    @DisplayName("verificarEstoqueAtivo deve lançar exceção se estoque estiver inativo")
    void verificarEstoqueAtivo_QuandoEstoqueInativo_LancaObjetoInativoException() {
        estoque.setAtivo(false);

        assertThatThrownBy(() -> estoqueService.verificarEstoqueAtivo(estoque))
                .isInstanceOf(ObjetoInativoException.class)
                .hasMessage("Estoque não está ativo");
    }

    @Test
    @DisplayName("verificarEstoqueUsuario não deve fazer nada se usuário for o dono")
    void verificarEstoqueUsuario_QuandoUsuarioEDono_NaoLancaExcecao() {
        estoqueService.verificarEstoqueUsuario(estoque, usuario);
    }

    @Test
    @DisplayName("verificarEstoqueUsuario deve lançar exceção se usuário não for o dono")
    void verificarEstoqueUsuario_QuandoUsuarioNaoEDono_LancaAcaoNaoPermitidaException() {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(99L);

        assertThatThrownBy(() -> estoqueService.verificarEstoqueUsuario(estoque, outroUsuario))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("O usuário não tem permissão para fazer essa ação.");
    }

    @Test
    @DisplayName("verificarEstoqueProduto deve lançar exceção se o produto não pertencer ao estoque")
    void verificarEstoqueProduto_QuandoProdutoNaoPertenceAoEstoque_LancaAcaoNaoPermitidaException() {
        Estoque outroEstoque = new Estoque("Outro Estoque", usuario);
        outroEstoque.setId(2L);

        Produto produto = new Produto();
        produto.setEstoque(outroEstoque);

        assertThatThrownBy(() -> estoqueService.verificarEstoqueProduto(estoque, produto))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("O produto não faz parte desse estoque.");
    }

    @Test
    @DisplayName("verificarEstoqueProduto não deve fazer nada se o produto pertencer ao estoque")
    void verificarEstoqueProduto_QuandoProdutoPertenceAoEstoque_NaoLancaExcecao() {
        Produto produto = new Produto();
        produto.setEstoque(estoque);

        estoqueService.verificarEstoqueProduto(estoque, produto);
    }
}
