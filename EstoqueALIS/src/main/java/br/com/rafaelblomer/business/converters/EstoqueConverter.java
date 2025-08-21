package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import org.springframework.stereotype.Component;

@Component
public class EstoqueConverter {

    public EstoqueResponseDTO entityParaResponseDTO (Estoque estoque) {
        return new EstoqueResponseDTO(
                estoque.getId(),
                estoque.getUsuario(),
                estoque.getLotes());
    }

}
