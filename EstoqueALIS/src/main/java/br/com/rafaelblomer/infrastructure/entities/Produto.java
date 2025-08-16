package br.com.rafaelblomer.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(length = 40)
    private String nome;
    @NotNull
    private String descricao;
    private Boolean ativo;
    @Transient
    private Integer quantidadeTotal;
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteProduto> lotes;

    public Produto(String nome, String descricao, Integer quantidadeTotal, List<LoteProduto> lotes) {
        this.nome = nome;
        this.descricao = descricao;
        this.ativo = true;
        this.quantidadeTotal = quantidadeTotal;
        this.lotes = lotes;
    }

    public Produto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getNome() {
        return nome;
    }

    public void setNome(@NotBlank String nome) {
        this.nome = nome;
    }

    public @NotNull String getDescricao() {
        return descricao;
    }

    public void setDescricao(@NotNull String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Integer getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(Integer quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }

    public List<LoteProduto> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteProduto> lotes) {
        this.lotes = lotes;
    }
}
