package br.com.rafaelblomer.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class ItemMovimentacaoLote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "movimentacao_id")
    @NotNull
    private MovimentacaoEstoque movimentacaoEstoque;
    @ManyToOne
    @JoinColumn(name = "lote_produto_id")
    @NotNull
    private LoteProduto loteProduto;
    @NotNull
    private Integer quantidade;

    public ItemMovimentacaoLote() {
    }

    public ItemMovimentacaoLote(LoteProduto loteProduto, MovimentacaoEstoque movimentacaoEstoque, Integer quantidade) {
        this.loteProduto = loteProduto;
        this.movimentacaoEstoque = movimentacaoEstoque;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull LoteProduto getLoteProduto() {
        return loteProduto;
    }

    public void setLoteProduto(@NotNull LoteProduto loteProduto) {
        this.loteProduto = loteProduto;
    }

    public @NotNull MovimentacaoEstoque getMovimentacaoEstoque() {
        return movimentacaoEstoque;
    }

    public void setMovimentacaoEstoque(@NotNull MovimentacaoEstoque movimentacaoEstoque) {
        this.movimentacaoEstoque = movimentacaoEstoque;
    }

    public @NotNull Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(@NotNull Integer quantidade) {
        this.quantidade = quantidade;
    }
}
