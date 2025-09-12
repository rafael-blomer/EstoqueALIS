package br.com.rafaelblomer.infrastructure.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificacaoTokenUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Usuario user;
    private LocalDateTime expiryDate;

    public VerificacaoTokenUsuario(String token, Usuario user) {
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(5);
    }

    public VerificacaoTokenUsuario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
