package br.com.rafaelblomer.services;

import br.com.rafaelblomer.business.EmailService;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Test
    void deveEnviarEmailDeVerificacao() {
        Usuario usuario = new Usuario();
        usuario.setNome("Rafael");
        usuario.setEmail("rafa@mail.com");

        emailService.sendVerificationEmail(usuario, "token123");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void deveEnviarEmailDeAlteracaoDeSenha() {
        Usuario usuario = new Usuario();
        usuario.setNome("Rafael");
        usuario.setEmail("rafa@mail.com");

        emailService.sendChangePasswordEmail(usuario, "token456");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
