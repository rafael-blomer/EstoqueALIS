package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProdutoCadastroDTO(@NotBlank @Size(max = 40) String nome,
                                 @NotBlank @Size(max = 40) String marca,
                                 @NotNull @Size(max = 100) String descricao,
                                 @NotNull Long idEstoque) {
}
