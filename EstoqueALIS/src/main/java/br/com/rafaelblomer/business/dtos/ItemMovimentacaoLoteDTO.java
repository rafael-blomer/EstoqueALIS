package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotNull;

public record ItemMovimentacaoLoteDTO(@NotNull Long loteProdutoId,
                                      @NotNull Integer quantidade) {
}
