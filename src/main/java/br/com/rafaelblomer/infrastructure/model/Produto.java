package br.com.rafaelblomer.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<LoteProduto> loteProdutos = new ArrayList<>();

    private String nome;
    private String descricao;
    private String categoria;
    private Boolean ativo;

    public Produto(String categoria, String descricao, String nome) {
        this.categoria = categoria;
        this.descricao = descricao;
        this.nome = nome;
        this.ativo = true;
    }

    public Produto() {
        this.ativo = true;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public List<LoteProduto> getLoteProdutos() {
        return loteProdutos;
    }

    public void adicionarLoteProduto(LoteProduto loteProdutos) {
        this.loteProdutos.add(loteProdutos);
    }
}
