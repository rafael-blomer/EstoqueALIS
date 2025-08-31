package br.com.rafaelblomer.business.converters;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import br.com.rafaelblomer.infrastructure.entities.ItemMovimentacaoLote;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.MovimentacaoEstoque;
import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;
	
@Component
public class MovimentacaoEstoqueConverter {

    public MovimentacaoEstoque loteProdParaMovEstoque(LoteProduto loteProduto) {
        MovimentacaoEstoque movimentacaoEstoque = new MovimentacaoEstoque();
        movimentacaoEstoque.setEstoque(loteProduto.getProduto().getEstoque());
        movimentacaoEstoque.setDataHora(LocalDateTime.now());
        movimentacaoEstoque.setTipoMov(TipoMovimentacao.ENTRADA);
        ItemMovimentacaoLote itemMovimentacaoLote = new ItemMovimentacaoLote();
        itemMovimentacaoLote.setLoteProduto(loteProduto);
        itemMovimentacaoLote.setMovimentacaoEstoque(movimentacaoEstoque);
        itemMovimentacaoLote.setQuantidade(loteProduto.getQuantidadeLote());
        movimentacaoEstoque.setItensMovimentacao(List.of(itemMovimentacaoLote));
        return movimentacaoEstoque;
    }

    /*public MovimentacaoEstoque dtoSaidaParaMovEstoqueEntity(MovimentacaoSaidaDTO dto, Estoque estoque) {
        return new MovimentacaoEstoque(LocalDateTime.now(),
                estoque,
                dto.ItensMovimentacaoLote().stream().map(iml -> dtoParaItemMovEntity(dto, )).toList(),
                TipoMovimentacao.SAIDA);
    }

    private ItemMovimentacaoLote dtoParaItemMovEntity(ItemMovimentacaoLoteDTO dto, LoteProduto loteProduto, MovimentacaoEstoque) {
        return null;
    }*/
}
