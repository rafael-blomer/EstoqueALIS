package br.com.rafaelblomer.services;

import br.com.rafaelblomer.business.EmailService;
import br.com.rafaelblomer.business.UsuarioService;
import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.dtos.UsuarioAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioLoginDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.VerficacaoEmailException;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.entities.VerificacaoTokenUsuario;
import br.com.rafaelblomer.infrastructure.repositories.UsuarioRepository;
import br.com.rafaelblomer.infrastructure.repositories.VerificacaoTokenUsuarioRepository;
import br.com.rafaelblomer.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository repository;
    @Mock
    private UsuarioConverter converter;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;
    @Mock
    private VerificacaoTokenUsuarioRepository tokenRepository;

    // Variáveis de apoio
    private Usuario usuario;
    private UsuarioCadastroDTO cadastroDTO;
    private final String FAKE_EMAIL = "teste@email.com";
    private final String FAKE_PASSWORD = "password123";
    private final String HASHED_PASSWORD = "hashedPassword123";
    private final String FAKE_JWT = "fake.jwt.token";
    private final String FAKE_BEARER_TOKEN = "Bearer " + FAKE_JWT;

    @BeforeEach
    void setUp() {
        cadastroDTO = new UsuarioCadastroDTO("Teste", FAKE_EMAIL, "12345678901234", "11999999999", FAKE_PASSWORD);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail(FAKE_EMAIL);
        usuario.setSenha(FAKE_PASSWORD);
        usuario.setAtivo(true); // Usuário ativo por padrão nos testes
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso e enviar email de verificação")
    void criarUsuario_ComDadosValidos_SalvaUsuarioEEnviaEmail() {
        // Arrange (Organizar)
        when(converter.dtoCadastroParaEntity(any(UsuarioCadastroDTO.class))).thenReturn(usuario);
        when(encoder.encode(FAKE_PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(repository.save(any(Usuario.class))).thenReturn(usuario);
        when(converter.entityParaResponseDTO(any(Usuario.class)))
                .thenReturn(new UsuarioResponseDTO(1L, "Teste", FAKE_EMAIL, "99999999999", "00623904000173", new ArrayList<>()));

        // Act (Agir)
        UsuarioResponseDTO resultado = usuarioService.criarUsuario(cadastroDTO);

        // Assert (Verificar)
        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo(FAKE_EMAIL);

        // Verifica se a senha foi codificada
        verify(encoder, times(1)).encode(FAKE_PASSWORD);
        // Verifica se o usuário foi salvo
        verify(repository, times(1)).save(usuario);
        // Captura o token de verificação que foi salvo
        ArgumentCaptor<VerificacaoTokenUsuario> tokenCaptor = ArgumentCaptor.forClass(VerificacaoTokenUsuario.class);
        verify(tokenRepository, times(1)).save(tokenCaptor.capture());
        // Verifica se o email de verificação foi enviado com o token correto
        verify(emailService, times(1)).sendVerificationEmail(eq(usuario), eq(tokenCaptor.getValue().getToken()));
    }

    @Test
    @DisplayName("Deve realizar login com sucesso para usuário ativo e credenciais válidas")
    void realizarLogin_ComUsuarioAtivoECredenciaisValidas_RetornaBearerToken() {
        // Arrange
        UsuarioLoginDTO loginDTO = new UsuarioLoginDTO(FAKE_EMAIL, FAKE_PASSWORD);
        Authentication authentication = mock(Authentication.class);

        when(repository.findByEmail(FAKE_EMAIL)).thenReturn(Optional.of(usuario));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getName()).thenReturn(FAKE_EMAIL);
        when(jwtUtil.generateToken(FAKE_EMAIL)).thenReturn(FAKE_JWT);

        // Act
        String resultado = usuarioService.realizarLogin(loginDTO);

        // Assert
        assertThat(resultado).isEqualTo("Bearer " + FAKE_JWT);
        verify(authenticationManager, times(1))
                .authenticate(new UsernamePasswordAuthenticationToken(FAKE_EMAIL, FAKE_PASSWORD));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar logar com usuário inativo")
    void realizarLogin_ComUsuarioInativo_LancaObjetoInativoException() {
        // Arrange
        usuario.setAtivo(false); // Define o usuário como inativo
        UsuarioLoginDTO loginDTO = new UsuarioLoginDTO(FAKE_EMAIL, FAKE_PASSWORD);
        when(repository.findByEmail(FAKE_EMAIL)).thenReturn(Optional.of(usuario));

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.realizarLogin(loginDTO))
                .isInstanceOf(ObjetoInativoException.class)
                .hasMessage("O usuário não está ativo.");

        // Garante que o processo de autenticação nem começou
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Deve verificar (ativar) um usuário com sucesso com um token válido")
    void verificarUsuario_ComTokenValido_AtivaUsuarioEExcluiToken() {
        // Arrange
        usuario.setAtivo(false); // Usuário começa inativo
        String tokenString = "valid-token";
        VerificacaoTokenUsuario verificationToken = new VerificacaoTokenUsuario(tokenString, usuario);

        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);

        // Act
        String resultado = usuarioService.verificarUsuario(tokenString);

        // Assert
        assertThat(resultado).isEqualTo("valido");

        // Captura o usuário salvo para verificar se foi ativado
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository, times(1)).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue().getAtivo()).isTrue();

        // Verifica se o token foi excluído
        verify(tokenRepository, times(1)).delete(verificationToken);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar verificar usuário com token expirado")
    void verificarUsuario_ComTokenExpirado_LancaVerficacaoEmailException() {
        // Arrange
        String tokenString = "expired-token";
        VerificacaoTokenUsuario verificationToken = new VerificacaoTokenUsuario(tokenString, usuario);
        // Define a data de expiração para o passado
        verificationToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.verificarUsuario(tokenString))
                .isInstanceOf(VerficacaoEmailException.class)
                .hasMessage("O token expirou.");

        // Garante que nenhuma alteração foi salva
        verify(repository, never()).save(any());
        verify(tokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar verificar usuário com token inválido")
    void verificarUsuario_ComTokenInvalido_LancaVerficacaoEmailException() {
        // Arrange
        String tokenString = "invalid-token";
        when(tokenRepository.findByToken(tokenString)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.verificarUsuario(tokenString))
                .isInstanceOf(VerficacaoEmailException.class)
                .hasMessage("O token de verificação está inválido");
    }

    @Test
    @DisplayName("Deve atualizar os dados do usuário com sucesso")
    void atualizarUsuario_ComDadosValidos_SalvaAlteracoes() {
        // Arrange
        UsuarioAtualizacaoDTO atualizacaoDTO = new UsuarioAtualizacaoDTO("Nome Atualizado", "11888888888");
        // Simula o comportamento do método findByToken
        when(jwtUtil.extrairEmailToken(FAKE_JWT)).thenReturn(FAKE_EMAIL);
        when(repository.findByEmail(FAKE_EMAIL)).thenReturn(Optional.of(usuario));
        // Simula que o novo telefone não está em uso
        when(repository.existsByTelefoneAndIdNot(atualizacaoDTO.telefone(), usuario.getId())).thenReturn(false);

        // Act
        usuarioService.atualizarUsuario("Bearer " + FAKE_JWT, atualizacaoDTO);

        // Assert
        // Captura o usuário que foi salvo para verificar os dados
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository, times(1)).save(usuarioCaptor.capture());

        Usuario usuarioSalvo = usuarioCaptor.getValue();
        assertThat(usuarioSalvo.getNome()).isEqualTo("Nome Atualizado");
        assertThat(usuarioSalvo.getTelefone()).isEqualTo("11888888888");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar com telefone já existente")
    void atualizarUsuario_ComTelefoneJaExistente_LancaDadoIrregularException() {
        // Arrange
        UsuarioAtualizacaoDTO atualizacaoDTO = new UsuarioAtualizacaoDTO("Nome", "11999999999");
        when(jwtUtil.extrairEmailToken(FAKE_JWT)).thenReturn(FAKE_EMAIL);
        when(repository.findByEmail(FAKE_EMAIL)).thenReturn(Optional.of(usuario));
        // Simula que o telefone já existe em outro cadastro
        when(repository.existsByTelefoneAndIdNot(atualizacaoDTO.telefone(), usuario.getId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> usuarioService.atualizarUsuario("Bearer " + FAKE_JWT, atualizacaoDTO))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("Número de telefone irregular. Tente outro.");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve alterar a senha com sucesso com um token válido")
    void alterarSenha_ComTokenValido_AlteraSenhaEExcluiToken() {
        // Arrange
        String newPassword = "newPassword123";
        String hashedNewPassword = "hashedNewPassword123";
        String tokenString = "valid-reset-token";
        VerificacaoTokenUsuario verificationToken = new VerificacaoTokenUsuario(tokenString, usuario);

        when(tokenRepository.findByToken(tokenString)).thenReturn(verificationToken);
        when(encoder.encode(newPassword)).thenReturn(hashedNewPassword);

        // Act
        String resultado = usuarioService.alterarSenha(tokenString, newPassword);

        // Assert
        assertThat(resultado).isEqualTo("Senha alterada com sucesso.");

        // Captura o usuário salvo para verificar a nova senha
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(repository, times(1)).save(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue().getSenha()).isEqualTo(hashedNewPassword);

        verify(tokenRepository, times(1)).delete(verificationToken);
    }

}