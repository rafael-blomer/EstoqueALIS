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
import br.com.rafaelblomer.infrastructure.repositories.EstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    // A anotação @InjectMocks cria uma instância da classe que queremos testar
    // e injeta os mocks criados com @Mock nela.
    @InjectMocks
    private EstoqueService estoqueService;

    // A anotação @Mock cria um objeto simulado (mock) para a dependência.
    @Mock
    private EstoqueRepository repository;

    @Mock
    private EstoqueConverter converter;

    @Mock
    private UsuarioService usuarioService;

    // Variáveis de apoio para os testes
    private Usuario usuario;
    private Estoque estoque;
    private final String TOKEN = "fake-token";
    private final Long ESTOQUE_ID = 1L;
    private final Long USUARIO_ID = 1L;

    // O método anotado com @BeforeEach será executado antes de cada teste.
    // É útil para inicializar objetos comuns a vários testes.
    @BeforeEach
    void setUp() {
        // Criando instâncias padrão para serem usadas nos testes
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
        // Arrange (Organizar)
        EstoqueCadastroDTO cadastroDTO = new EstoqueCadastroDTO("Meu Estoque");
        EstoqueResponseDTO responseDTO = new EstoqueResponseDTO(ESTOQUE_ID, "Meu Estoque", USUARIO_ID);

        // Configurando o comportamento dos mocks
        // 1. Quando o usuarioService.findByToken for chamado com qualquer token, retorne nosso usuário mockado.
        when(usuarioService.findByToken(any(String.class))).thenReturn(usuario);
        // 2. Quando o repository.save for chamado com qualquer instância de Estoque, apenas retorne o que foi passado.
        when(repository.save(any(Estoque.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // 3. Quando o converter.entityParaResponseDTO for chamado, retorne nosso DTO de resposta.
        when(converter.entityParaResponseDTO(any(Estoque.class))).thenReturn(responseDTO);

        // Act (Agir)
        EstoqueResponseDTO resultado = estoqueService.criarNovoEstoque(TOKEN, cadastroDTO);

        // Assert (Verificar)
        assertThat(resultado).isNotNull();
        assertThat(resultado.nomeEstoque()).isEqualTo("Meu Estoque");
        assertThat(resultado.usuarioId()).isEqualTo(USUARIO_ID);

        // Verifica se os métodos mockados foram chamados o número esperado de vezes.
        verify(usuarioService, times(1)).findByToken(TOKEN);
        verify(repository, times(1)).save(any(Estoque.class));
        verify(converter, times(1)).entityParaResponseDTO(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve desativar um estoque com sucesso")
    void desativarEstoque_ComIdValidoEUsuarioCorreto_DesativaComSucesso() {
        // Arrange
        // 1. Quando findByToken for chamado, retorne o usuário dono do estoque.
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        // 2. Quando findById for chamado com o ID do estoque, retorne o estoque.
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.of(estoque));

        // Act
        estoqueService.desativarEstoque(TOKEN, ESTOQUE_ID);

        // Assert
        // Verifica se o método save foi chamado uma vez.
        verify(repository, times(1)).save(estoque);
        // Verifica se o estado do objeto estoque foi alterado para inativo.
        assertThat(estoque.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar estoque de outro usuário")
    void desativarEstoque_ComUsuarioDiferente_LancaAcaoNaoPermitidaException() {
        // Arrange
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);

        when(usuarioService.findByToken(TOKEN)).thenReturn(outroUsuario);
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.of(estoque));

        // Act & Assert
        // Verifica se a chamada do método lança a exceção esperada.
        assertThatThrownBy(() -> estoqueService.desativarEstoque(TOKEN, ESTOQUE_ID))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("O usuário não tem permissão para fazer essa ação.");

        // Garante que o método save nunca foi chamado.
        verify(repository, never()).save(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar desativar estoque inexistente")
    void desativarEstoque_ComIdInexistente_LancaObjetoNaoEncontradoException() {
        // Arrange
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.desativarEstoque(TOKEN, ESTOQUE_ID))
                .isInstanceOf(ObjetoNaoEncontradoException.class)
                .hasMessage("Estoque não foi encontrado.");
    }

    @Test
    @DisplayName("buscarEstoqueEntityId deve retornar Estoque quando encontrado")
    void buscarEstoqueEntityId_QuandoEncontrado_RetornaEstoque() {
        // Arrange
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.of(estoque));

        // Act
        Estoque resultado = estoqueService.buscarEstoqueEntityId(ESTOQUE_ID);

        // Assert
        assertThat(resultado).isEqualTo(estoque);
    }

    @Test
    @DisplayName("buscarEstoqueEntityId deve lançar exceção quando não encontrado")
    void buscarEstoqueEntityId_QuandoNaoEncontrado_LancaObjetoNaoEncontradoException() {
        // Arrange
        when(repository.findById(ESTOQUE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.buscarEstoqueEntityId(ESTOQUE_ID))
                .isInstanceOf(ObjetoNaoEncontradoException.class)
                .hasMessage("Estoque não foi encontrado.");
    }

    @Test
    @DisplayName("verificarEstoqueAtivo não deve fazer nada se estoque estiver ativo")
    void verificarEstoqueAtivo_QuandoEstoqueAtivo_NaoLancaExcecao() {
        // Arrange
        estoque.setAtivo(true);

        // Act & Assert
        // Nenhuma exceção deve ser lançada
        estoqueService.verificarEstoqueAtivo(estoque);
    }

    @Test
    @DisplayName("verificarEstoqueAtivo deve lançar exceção se estoque estiver inativo")
    void verificarEstoqueAtivo_QuandoEstoqueInativo_LancaObjetoInativoException() {
        // Arrange
        estoque.setAtivo(false);

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.verificarEstoqueAtivo(estoque))
                .isInstanceOf(ObjetoInativoException.class)
                .hasMessage("Estoque não está ativo");
    }

    @Test
    @DisplayName("verificarEstoqueUsuario não deve fazer nada se usuário for o dono")
    void verificarEstoqueUsuario_QuandoUsuarioEDono_NaoLancaExcecao() {
        // Arrange (nenhum arranjo especial necessário, usa o setup)

        // Act & Assert
        estoqueService.verificarEstoqueUsuario(estoque, usuario);
    }

    @Test
    @DisplayName("verificarEstoqueUsuario deve lançar exceção se usuário não for o dono")
    void verificarEstoqueUsuario_QuandoUsuarioNaoEDono_LancaAcaoNaoPermitidaException() {
        // Arrange
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(99L);

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.verificarEstoqueUsuario(estoque, outroUsuario))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("O usuário não tem permissão para fazer essa ação.");
    }

    @Test
    @DisplayName("verificarEstoqueProduto deve lançar exceção se o produto não pertencer ao estoque")
    void verificarEstoqueProduto_QuandoProdutoNaoPertenceAoEstoque_LancaAcaoNaoPermitidaException() {
        // Arrange
        Estoque outroEstoque = new Estoque("Outro Estoque", usuario);
        outroEstoque.setId(2L);

        Produto produto = new Produto();
        produto.setEstoque(outroEstoque); // Produto pertence a outro estoque

        // Act & Assert
        assertThatThrownBy(() -> estoqueService.verificarEstoqueProduto(estoque, produto))
                .isInstanceOf(AcaoNaoPermitidaException.class)
                .hasMessage("O produto não faz parte desse estoque.");
    }

    @Test
    @DisplayName("verificarEstoqueProduto não deve fazer nada se o produto pertencer ao estoque")
    void verificarEstoqueProduto_QuandoProdutoPertenceAoEstoque_NaoLancaExcecao() {
        // Arrange
        Produto produto = new Produto();
        produto.setEstoque(estoque); // Produto pertence ao estoque correto

        // Act & Assert
        estoqueService.verificarEstoqueProduto(estoque, produto);
    }
}
