package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MovimentacaoSaidaDTO(@NotNull Long estoqueId,
                                   @NotNull List<ItemMovimentacaoLoteDTO> ItensMovimentacaoLote) {
}
