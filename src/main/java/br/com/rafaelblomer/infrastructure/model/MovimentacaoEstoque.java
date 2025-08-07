package br.com.rafaelblomer.infrastructure.model;

import br.com.rafaelblomer.infrastructure.model.enums.TipoMovimentacao;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipoMovimentacao;

    private LocalDateTime dataHora;


    @OneToMany(mappedBy = "movimentacao", cascade = CascadeType.ALL)
    private List<MovimentacaoLote> lotes;

    public MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(TipoMovimentacao tipoMovimentacao, List<MovimentacaoLote> lotes) {
        this.tipoMovimentacao = tipoMovimentacao;
        this.dataHora = LocalDateTime.now();
        this.lotes = lotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoMovimentacao getTipoMovimentacao() {
        return tipoMovimentacao;
    }

    public void setTipoMovimentacao(TipoMovimentacao tipoMovimentacao) {
        this.tipoMovimentacao = tipoMovimentacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public List<MovimentacaoLote> getLotes() {
        return lotes;
    }

    public void adicionarLotes(MovimentacaoLote lote) {
        this.lotes.add(lote);
        lote.setMovimentacao(this);
    }
}