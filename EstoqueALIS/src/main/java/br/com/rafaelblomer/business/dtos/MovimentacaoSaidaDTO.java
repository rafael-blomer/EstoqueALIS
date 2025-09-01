package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotNull;

public record MovimentacaoSaidaDTO(@NotNull Long estoqueId,
                                   @NotNull Long produtoId,
                                   @NotNull Integer quantidade) {
}
