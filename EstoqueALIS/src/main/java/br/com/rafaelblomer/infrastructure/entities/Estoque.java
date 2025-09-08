package br.com.rafaelblomer.infrastructure.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nomeEstoque;
    private Boolean ativo;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @NotNull
    @JsonIgnore
    private Usuario usuario;
    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentacaoEstoque> movimentacoes;

    public Estoque(String nome, Usuario usuario) {
        this.nomeEstoque = nome;
        this.ativo = true;
        this.usuario = usuario;
        this.movimentacoes = new ArrayList<>();
    }

    public Estoque() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public @NotNull Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(@NotNull Usuario usuario) {
        this.usuario = usuario;
    }

    public List<MovimentacaoEstoque> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoEstoque> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public @NotBlank String getNomeEstoque() {
        return nomeEstoque;
    }

    public void setNomeEstoque(@NotBlank String nomeEstoque) {
        this.nomeEstoque = nomeEstoque;
    }
}
