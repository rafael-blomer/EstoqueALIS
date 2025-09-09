package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotBlank;

public record EstoqueCadastroDTO(@NotBlank String nomeEstoque) {
}
