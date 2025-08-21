package br.com.rafaelblomer.business.dtos;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Usuario;

import java.util.List;

public record EstoqueResponseDTO(Long id,
                                 Usuario usuario,
                                 List<LoteProduto> lotes) {
}
