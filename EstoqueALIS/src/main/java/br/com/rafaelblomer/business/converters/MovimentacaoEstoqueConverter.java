package br.com.rafaelblomer.business.converters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.rafaelblomer.business.LoteProdutoService;
import br.com.rafaelblomer.business.dtos.ItemMovimentacaoLoteResponseDTO;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import br.com.rafaelblomer.infrastructure.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;
	
@Component
public class MovimentacaoEstoqueConverter {

    @Autowired
    private LoteProdutoService loteProdutoService;

    @Autowired
    private EstoqueConverter estoqueConverter;

    @Autowired
    private LoteProdutoConverter loteProdutoConverter;

    public MovimentacaoEstoque loteProdParaMovEstoque(LoteProduto loteProduto) {
        MovimentacaoEstoque movimentacaoEstoque = new MovimentacaoEstoque();
        movimentacaoEstoque.setEstoque(loteProduto.getProduto().getEstoque());
        movimentacaoEstoque.setDataHora(LocalDateTime.now());
        movimentacaoEstoque.setTipoMov(TipoMovimentacao.ENTRADA);
        ItemMovimentacaoLote itemMovimentacaoLote = gerarItemMovimentacaoLote(loteProduto, movimentacaoEstoque);
        movimentacaoEstoque.setItensMovimentacao(List.of(itemMovimentacaoLote));
        return movimentacaoEstoque;
    }

    public MovimentacaoEstoque saidaDtoParaEntity(MovimentacaoSaidaDTO dto, Estoque estoque) {
        MovimentacaoEstoque movEstoque = new MovimentacaoEstoque();
        movEstoque.setDataHora(LocalDateTime.now());
        movEstoque.setTipoMov(TipoMovimentacao.SAIDA);
        movEstoque.setEstoque(estoque);
        List<ItemMovimentacaoLote> list = new ArrayList<>();
        movEstoque.setItensMovimentacao(list);
        return movEstoque;
    }

    public MovimentacaoEstoqueResponseDTO movEstoqueEntityParaDto(MovimentacaoEstoque movEstoque) {
        return new MovimentacaoEstoqueResponseDTO(
                movEstoque.getId(),
                estoqueConverter.entityParaResponseDTO(movEstoque.getEstoque()),
                movEstoque.getDataHora(),
                movEstoque.getTipoMov(),
                movEstoque.getItensMovimentacao().stream().map(this::itemMovLoteEntityParaDto).toList());
    }

    private ItemMovimentacaoLote gerarItemMovimentacaoLote(LoteProduto loteProduto, MovimentacaoEstoque movimentacaoEstoque) {
        ItemMovimentacaoLote itemMovimentacaoLote = new ItemMovimentacaoLote();
        itemMovimentacaoLote.setLoteProduto(loteProduto);
        itemMovimentacaoLote.setMovimentacaoEstoque(movimentacaoEstoque);
        itemMovimentacaoLote.setQuantidade(loteProduto.getQuantidadeLote());
        return itemMovimentacaoLote;
    }

    private ItemMovimentacaoLote itemMovLoteDtoParaEntity(ItemMovimentacaoLoteDTO dto, MovimentacaoEstoque movimentacaoEstoque) {
        LoteProduto loteProduto = loteProdutoService.buscarLoteProdutoEntity(dto.loteProdutoId());
        return new ItemMovimentacaoLote(loteProduto, movimentacaoEstoque, dto.quantidade());
    }

    private ItemMovimentacaoLoteResponseDTO itemMovLoteEntityParaDto(ItemMovimentacaoLote entity) {
        return new ItemMovimentacaoLoteResponseDTO(
                entity.getId(),
                loteProdutoConverter.paraLoteProdutoDTO(entity.getLoteProduto()),
                entity.getQuantidade());
    }
}
