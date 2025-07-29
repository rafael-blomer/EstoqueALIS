package br.com.rafaelblomer.infrastructure.model;

import br.com.rafaelblomer.infrastructure.model.enums.TipoMovimentacao;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private LoteProduto loteProduto;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipoMovimentacao;

    private Integer quantidade;
    private LocalDateTime dataHora;

    public MovimentacaoEstoque(LoteProduto loteProduto, Integer quantidade, TipoMovimentacao tipoMovimentacao) {
        this.dataHora = LocalDateTime.now();
        this.loteProduto = loteProduto;
        this.quantidade = quantidade;
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public MovimentacaoEstoque() {
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteProduto getLoteProduto() {
        return loteProduto;
    }

    public void setLoteProduto(LoteProduto loteProduto) {
        this.loteProduto = loteProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }
}
