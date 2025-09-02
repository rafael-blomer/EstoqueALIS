package br.com.rafaelblomer.business.dtos;

import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;

import java.time.LocalDateTime;
import java.util.List;

public record MovimentacaoEstoqueResponseDTO(Long id,
                                             LocalDateTime dataHora,
                                             TipoMovimentacao tipoMovimentacao,
                                             List<ItemMovimentacaoLoteResponseDTO> itensMovimentacoesLote) {
}
