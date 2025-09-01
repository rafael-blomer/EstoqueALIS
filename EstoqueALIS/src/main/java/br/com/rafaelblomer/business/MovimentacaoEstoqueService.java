package br.com.rafaelblomer.business;

import java.util.List;

import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.MovimentacaoEstoque;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.beans.factory.annotation.Autowired;
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

    //List<LoteProduto> findByProdutoIdAndQuantidadeLoteGreaterThanOrderByDataValidadeAsc(Long produtoId, int quantidade);
    @Autowired
    private

    public MovimentacaoEstoqueResponseDTO registrarSaida(MovimentacaoSaidaDTO dto) {
        Estoque estoque = estoqueService.buscarEstoqueEntityId(dto.estoqueId());
        estoqueService.verificarEstoqueAtivo(estoque);
        Produto produto = produtoService.buscarProdutoId(dto.produtoId());
        produtoService.verificarProdutoAtivo(produto);
        relatorioService.calcularQuantidadeTotalProduto(produto);
        if (dto.quantidade() > produto.getQuantidadeTotal())
            throw new IllegalArgumentException();
        MovimentacaoEstoque movEstoque = converter.saidaDtoParaEntity(dto, estoque);

        repository.save(movEstoque);
        return converter.movEstoqueEntityParaDto(movEstoque);
    }

    //fazer por usuario
    public List<MovimentacaoEstoqueResponseDTO> historicoMovimentacoes(Long estoqueId) {
        return null;
    }

    //ÚTEIS

    public void registrarEntrada(LoteProduto loteProduto) {
        repository.save(converter.loteProdParaMovEstoque(loteProduto));
    }
}
