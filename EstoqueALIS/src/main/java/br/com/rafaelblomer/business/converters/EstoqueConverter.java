package br.com.rafaelblomer.business.converters;

import org.springframework.stereotype.Component;

import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Estoque;

@Component
public class EstoqueConverter {

    public EstoqueResponseDTO entityParaResponseDTO (Estoque estoque) {
        return new EstoqueResponseDTO(
                estoque.getId(),
                estoque.getUsuario().getId());
    }

}
