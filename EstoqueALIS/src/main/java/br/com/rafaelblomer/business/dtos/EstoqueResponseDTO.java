package br.com.rafaelblomer.business.dtos;

public record EstoqueResponseDTO(Long estoqueId,
                                 String nomeEstoque,
                                 Long usuarioId) {
}
