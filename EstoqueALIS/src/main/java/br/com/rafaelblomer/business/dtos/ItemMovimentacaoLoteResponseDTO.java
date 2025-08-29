package br.com.rafaelblomer.business.dtos;

public record ItemMovimentacaoLoteResponseDTO(Long id,
                                              LoteProdutoResponseDTO loteProduto,
                                              Integer quantidade) {
}
