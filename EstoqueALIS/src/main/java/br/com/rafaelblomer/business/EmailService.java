package br.com.rafaelblomer.business;

import br.com.rafaelblomer.infrastructure.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(Usuario user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Verificação de Cadastro";
        String confirmationUrl = "http://localhost:8080/api/auth/verify?token=" + token;
        String message = "Olá " + user.getNome() + ",\n\n"
                + "Obrigado por se cadastrar. Por favor, clique no link abaixo para ativar sua conta:\n\n"
                + confirmationUrl + "\n\n"
                + "Este link irá expirar em 24 horas.\n\n"
                + "Atenciosamente,\nEquipe do Sistema.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
