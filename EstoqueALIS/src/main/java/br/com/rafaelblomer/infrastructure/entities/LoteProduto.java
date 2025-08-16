package br.com.rafaelblomer.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class LoteProduto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "estoque_id")
    private Estoque estoque;
    @NotNull
    private Integer quantidadeLote;
    @NotNull
    private LocalDate dataValidade;
    @NotBlank
    private String loteFabricante;

    public LoteProduto() {
    }

    public LoteProduto(LocalDate dataValidade, Estoque estoque, String loteFabricante, Produto produto, Integer quantidadeLote) {
        this.dataValidade = dataValidade;
        this.estoque = estoque;
        this.loteFabricante = loteFabricante;
        this.produto = produto;
        this.quantidadeLote = quantidadeLote;
    }

    public @NotNull LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(@NotNull LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public @NotNull Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(@NotNull Estoque estoque) {
        this.estoque = estoque;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank String getLoteFabricante() {
        return loteFabricante;
    }

    public void setLoteFabricante(@NotBlank String loteFabricante) {
        this.loteFabricante = loteFabricante;
    }

    public @NotNull Produto getProduto() {
        return produto;
    }

    public void setProduto(@NotNull Produto produto) {
        this.produto = produto;
    }

    public @NotNull Integer getQuantidadeLote() {
        return quantidadeLote;
    }

    public void setQuantidadeLote(@NotNull Integer quantidadeLote) {
        this.quantidadeLote = quantidadeLote;
    }
}
