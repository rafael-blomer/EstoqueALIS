package br.com.rafaelblomer.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.infrastructure.entities.*;
import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;
import br.com.rafaelblomer.infrastructure.event.LoteCriadoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.MovimentacaoEstoqueConverter;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.infrastructure.repositories.MovimentacaoEstoqueRepository;

@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private MovimentacaoEstoqueRepository repository;

    @Autowired
    private MovimentacaoEstoqueConverter converter;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private LoteProdutoService loteProdutoService;

    public MovimentacaoEstoqueResponseDTO registrarSaida(MovimentacaoSaidaDTO dto) {
        Estoque estoque = estoqueService.buscarEstoqueEntityId(dto.estoqueId());
        estoqueService.verificarEstoqueAtivo(estoque);
        Produto produto = produtoService.buscarProdutoId(dto.produtoId());
        produtoService.verificarProdutoAtivo(produto);
        verificarQuantidadeTotalProduto(dto, produto);
        MovimentacaoEstoque movEstoque = new MovimentacaoEstoque();
        movEstoque.setDataHora(LocalDateTime.now());
        movEstoque.setTipoMov(TipoMovimentacao.SAIDA);
        movEstoque.setEstoque(estoque);
        List<ItemMovimentacaoLote> itens = listaDeItensMovimentacao(estoque, movEstoque, produto.getId(), dto.quantidade());
        movEstoque.setItensMovimentacao(itens);
        repository.save(movEstoque);
        return converter.movEstoqueEntityParaDto(movEstoque);
    }

    //fazer por usuario
    public List<MovimentacaoEstoqueResponseDTO> historicoMovimentacoes(Long estoqueId) {
        return null;
    }

    //ÚTEIS

    @EventListener
    public void registrarEntrada(LoteCriadoEvent event) {
        repository.save(converter.loteProdParaMovEstoque(event.getLoteProduto()));
    }

    private void verificarQuantidadeTotalProduto(MovimentacaoSaidaDTO dto, Produto produto) {
        relatorioService.calcularQuantidadeTotalProduto(produto);
        if (dto.quantidade() > produto.getQuantidadeTotal())
            throw new DadoIrregularException("Você está tentando retirar mais unidades de produto do que existe no estoque.");
    }

    private List<ItemMovimentacaoLote> listaDeItensMovimentacao(Estoque estoque, MovimentacaoEstoque movEstoque, Long produtoId, Integer quantidadeDesejada) {
        List<LoteProduto> lotes = loteProdutoService.buscarLoteProdutoPorDataValidade(produtoId);
        List<ItemMovimentacaoLote> itens = new ArrayList<>();
        for (LoteProduto lt : lotes) {
            if (quantidadeDesejada <= 0) break;
            int retirada;
            if (lt.getQuantidadeLote() > quantidadeDesejada) {
                retirada = quantidadeDesejada;
                lt.setQuantidadeLote(lt.getQuantidadeLote() - retirada);
                quantidadeDesejada = 0;
            } else {
                retirada = lt.getQuantidadeLote();
                quantidadeDesejada -= retirada;
                lt.setQuantidadeLote(0);
            }
            ItemMovimentacaoLote item = new ItemMovimentacaoLote();
            item.setMovimentacaoEstoque(movEstoque);
            item.setLoteProduto(lt);
            item.setQuantidade(retirada);
            itens.add(item);
        }
        loteProdutoService.salvarAlteracoes(itens.stream().map(ItemMovimentacaoLote::getLoteProduto).toList());
        return itens;
    }
}
