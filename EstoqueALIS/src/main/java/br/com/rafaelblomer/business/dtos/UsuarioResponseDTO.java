package br.com.rafaelblomer.business.dtos;

import br.com.rafaelblomer.infrastructure.entities.Estoque;

import java.util.List;

public record UsuarioResponseDTO(Long id,
                                 String nome,
                                 String email,
                                 String telefone,
                                 String cnpj,
                                 List<Estoque> estoques) {
}
