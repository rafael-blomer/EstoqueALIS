package br.com.rafaelblomer.infrastructure.model;

import jakarta.persistence.*;

@Entity
public class MovimentacaoLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private MovimentacaoEstoque movimentacao;

    @ManyToOne
    private LoteProduto lote;

    private Integer quantidade;

    public MovimentacaoLote() {
    }

    public MovimentacaoLote(Integer quantidade, LoteProduto lote, MovimentacaoEstoque movimentacao) {
        this.quantidade = quantidade;
        this.lote = lote;
        this.movimentacao = movimentacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MovimentacaoEstoque getMovimentacao() {
        return movimentacao;
    }

    public void setMovimentacao(MovimentacaoEstoque movimentacao) {
        this.movimentacao = movimentacao;
    }

    public LoteProduto getLote() {
        return lote;
    }

    public void setLote(LoteProduto lote) {
        this.lote = lote;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}

