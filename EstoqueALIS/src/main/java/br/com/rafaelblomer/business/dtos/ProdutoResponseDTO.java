package br.com.rafaelblomer.business.dtos;

public record ProdutoResponseDTO(Long id,
                                 String nome,
                                 String marca,
                                 String descricao,
                                 Integer quantidadeTotal,
                                 EstoqueResponseDTO estoque) {
}
