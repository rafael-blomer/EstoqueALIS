package br.com.rafaelblomer.infrastructure.entities;

import java.time.LocalDateTime;
import java.util.List;

import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

@Entity
public class MovimentacaoEstoque {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "estoque_id")
    @NotNull
    private Estoque estoque;
    @NotNull
    private LocalDateTime dataHora;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipoMov;
    @OneToMany(mappedBy = "movimentacaoEstoque", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    private List<ItemMovimentacaoLote> itensMovimentacao;

    public MovimentacaoEstoque() {
    }

    public MovimentacaoEstoque(LocalDateTime dataHora, Estoque estoque, List<ItemMovimentacaoLote> itensMovimentacao, TipoMovimentacao tipoMov) {
        this.dataHora = dataHora;
        this.estoque = estoque;
        this.itensMovimentacao = itensMovimentacao;
        this.tipoMov = tipoMov;
    }

    public @NotNull LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(@NotNull LocalDateTime dataHora) {
        this.dataHora = dataHora;
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

    public @NotNull List<ItemMovimentacaoLote> getItensMovimentacao() {
        return itensMovimentacao;
    }

    public void setItensMovimentacao(@NotNull List<ItemMovimentacaoLote> itensMovimentacao) {
        this.itensMovimentacao = itensMovimentacao;
    }

    public @NotNull TipoMovimentacao getTipoMov() {
        return tipoMov;
    }

    public void setTipoMov(@NotNull TipoMovimentacao tipoMov) {
        this.tipoMov = tipoMov;
    }
}
