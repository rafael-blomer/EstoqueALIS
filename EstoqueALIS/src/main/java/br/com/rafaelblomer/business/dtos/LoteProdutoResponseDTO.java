package br.com.rafaelblomer.business.dtos;

import java.time.LocalDate;

public record LoteProdutoResponseDTO (Long id,
                                      ProdutoResponseDTO produto,
                                      Integer quantidadeLote,
                                      LocalDate dataValidade,
                                      String loteFabricante){
}
