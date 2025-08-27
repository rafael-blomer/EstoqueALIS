package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoteProdutoConverter {

    @Autowired
    private ProdutoConverter produtoConverter;

    @Autowired
    private EstoqueConverter estoqueConverter;

    public LoteProdutoResponseDTO paraLoteProdutoDTO(LoteProduto entity) {
        return new LoteProdutoResponseDTO(
                entity.getId(),
                produtoConverter.entityParaResponseDTO(entity.getProduto(), entity.getEstoque()),
                estoqueConverter.entityParaResponseDTO(entity.getEstoque()),
                entity.getQuantidadeLote(),
                entity.getDataValidade(),
                entity.getLoteFabricante()
        );
    }

    public LoteProduto dtoParaLoteProdutoEntity(LoteProdutoCadastroDTO dto, Estoque estoque, Produto produto) {
        return new LoteProduto(dto.dataValidade(),
                estoque,
                dto.loteFabricante(),
                produto,
                dto.quantidadeLote());
    }
}
