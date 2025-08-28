package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LoteProdutoCadastroDTO(@NotNull Long produtoId,
                                     @NotNull Integer quantidadeLote,
                                     @NotNull LocalDate dataValidade,
                                     @NotNull @Size(max = 65) String loteFabricante) {
}
