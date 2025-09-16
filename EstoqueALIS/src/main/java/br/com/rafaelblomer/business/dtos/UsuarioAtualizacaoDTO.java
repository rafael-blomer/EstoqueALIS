package br.com.rafaelblomer.business.dtos;

import jakarta.validation.constraints.Size;

public record UsuarioAtualizacaoDTO (String nome,
                                     @Size(min = 11, max = 13) String telefone){
}
