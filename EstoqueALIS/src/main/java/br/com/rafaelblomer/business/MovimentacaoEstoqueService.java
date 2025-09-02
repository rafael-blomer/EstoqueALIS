package br.com.rafaelblomer.business;

import java.util.List;

import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.MovimentacaoEstoque;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.event.LoteCriadoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.MovimentacaoEstoqueConverter;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
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
        List<LoteProduto> listLoteProduto = loteProdutoService.buscarLoteProdutoPorDataValidade(produto.getId());
        MovimentacaoEstoque movEstoque = converter.saidaDtoParaEntity(estoque, listLoteProduto);
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
            throw new IllegalArgumentException();
    }
}
