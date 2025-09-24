package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;

@Component
public class LoteProdutoConverter {

    @Autowired
    private ProdutoConverter produtoConverter;

    public LoteProdutoResponseDTO paraLoteProdutoDTO(LoteProduto entity) {
        return new LoteProdutoResponseDTO(
                entity.getId(),
                produtoConverter.entityParaResponseDTO(entity.getProduto()),
                entity.getQuantidadeLote(),
                entity.getDataValidade(),
                entity.getLoteFabricante()
        );
    }

    public LoteProduto dtoParaLoteProdutoEntity(LoteProdutoCadastroDTO dto, Produto produto) {
        return new LoteProduto(
                dto.dataValidade(),
                dto.loteFabricante(),
                produto,
                dto.quantidadeLote());
    }
}
