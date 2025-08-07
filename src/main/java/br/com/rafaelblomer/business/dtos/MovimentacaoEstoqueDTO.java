package br.com.rafaelblomer.business.dtos;

import br.com.rafaelblomer.infrastructure.model.LoteProduto;

import java.util.List;

public record MovimentacaoEstoqueDTO(List<LoteProduto> loteProduto, Integer quantidade) {
}
