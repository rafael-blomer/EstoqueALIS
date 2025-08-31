package br.com.rafaelblomer.business.dtos;

import java.util.List;

public record UsuarioResponseDTO(Long id,
                                 String nome,
                                 String email,
                                 String telefone,
                                 String cnpj,
                                 List<EstoqueResponseDTO> estoques) {
}
