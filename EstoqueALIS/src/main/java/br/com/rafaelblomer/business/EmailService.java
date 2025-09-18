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

    /**
     * Envia verificação para o email do usuário quando ele se cadastra
     * @param user = usuário que receberá o email
     * @param token = token de verificação de email, NÃO É O TOKEN DO SECURITY
     */
    public void sendVerificationEmail(Usuario user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Verificação de Cadastro";
        String confirmationUrl = "http://127.0.0.1:5500/VerificaEmail.html?token=" + token;
        String message = "Olá " + user.getNome() + ",\n\n"
                + "Obrigado por se cadastrar no EstoqueALIS. Por favor, clique no link abaixo para ativar sua conta:\n\n"
                + confirmationUrl + "\n\n"
                + "Este link irá expirar em 5 horas.\n\n"
                + "Atenciosamente,\nEquipe EstoqueALIS.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }

    /**
     * Envia email para o usuário quando ele quer trocar de senha
     * @param usuario = usuário que quer trocar a senha
     * @param token = token de verificação de email, NÃO É O TOKEN DO SECURITY
     */
    public void sendChangePasswordEmail(Usuario usuario, String token) {
        String recipientAddress = usuario.getEmail();
        String subject = "Alteração de Senha";
        String confirmationUrl = "http://127.0.0.1:5500/alterarsenha.html?token=" + token;
        String message = "Olá " + usuario.getNome() + ",\n\n"
                + "Houve um pedido para alterar sua senha. Se o pedido não foi feito por você, apenas ignore o email.\n\n"
                + "Para alterar sua senha, clique no link abaixo:\n\n"
                + confirmationUrl + "\n\n"
                + "Este link irá expirar em 5 horas.\n\n"
                + "Atenciosamente,\nEquipe EstoqueALIS.";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
