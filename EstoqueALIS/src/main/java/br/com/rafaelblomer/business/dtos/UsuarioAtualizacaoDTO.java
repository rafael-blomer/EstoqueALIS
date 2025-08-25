package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioAtualizacaoDTO (String nome,
                                     @NotBlank @Size(min = 11, max = 13) String telefone,
                                     @NotBlank @Size(min = 10) String senha){
}
