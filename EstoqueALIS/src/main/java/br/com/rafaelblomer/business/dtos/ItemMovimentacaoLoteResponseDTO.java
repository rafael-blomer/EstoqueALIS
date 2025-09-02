package br.com.rafaelblomer.business.dtos;

public record ItemMovimentacaoLoteResponseDTO(Long idItensMovimentacao,
                                              Long loteProdutoId,
                                              Integer quantidadeRetiradaLote,
                                              String loteFabricante,
                                              Long produtoId,
                                              String nomeProduto,
                                              String marcaProduto,
                                              String descricaoProduto) {
}
