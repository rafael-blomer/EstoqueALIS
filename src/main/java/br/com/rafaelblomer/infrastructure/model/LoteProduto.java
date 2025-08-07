package br.com.rafaelblomer.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class LoteProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Produto produto;

    private Integer quantidade;
    private LocalDate dataValidade;
    private String loteFabricante;

    public LoteProduto(LocalDate dataValidade, String loteFabricante, Produto produto, Integer quantidade) {
        this.dataValidade = dataValidade;
        this.loteFabricante = loteFabricante;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public LoteProduto() {
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoteFabricante() {
        return loteFabricante;
    }

    public void setLoteFabricante(String loteFabricante) {
        this.loteFabricante = loteFabricante;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Boolean estaVencido() {
        return LocalDate.now().isAfter(this.dataValidade);
    }
}
