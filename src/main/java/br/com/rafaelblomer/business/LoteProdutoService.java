package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.exceptions.EntidadeNaoEncontrada;
import br.com.rafaelblomer.infrastructure.model.LoteProduto;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoteProdutoService {

    @Autowired
    private LoteProdutoRepository repository;

    @Autowired
    private ProdutoService produtoService;

    @Transactional
    public LoteProduto criarNovoLoteProduto(LoteProduto loteProduto) {
        var produto = produtoService.buscarUmProduto(loteProduto.getProduto().getId());
        loteProduto.setProduto(produto);
        produto.adicionarLoteProduto(loteProduto);
        return repository.save(loteProduto);
    }

    public LoteProduto buscarUmLoteProduto(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntidadeNaoEncontrada("Lote de produto não encontrado na base de dados."));
    }

    public List<LoteProduto> buscarLotesPorProduto(Long produtoId) {
        List<LoteProduto> list = repository.findAll().stream().filter(lote -> Objects.equals(lote.getProduto().getId(), produtoId)).collect(Collectors.toList());
        if(list.isEmpty())
            throw new EntidadeNaoEncontrada("Produto não encontrado na base de dados.");
        return list;
    }

    public List<LoteProduto> buscarLotesPorProdutoContendoItens(Long produtoId) {
        return repository.findAll().stream()
                .filter(lote -> Objects.equals(lote.getProduto().getId(), produtoId))
                .filter(lote -> lote.getQuantidade() > 0)
                .filter(lote -> !lote.estaVencido())
                .collect(Collectors.toList());
    }

    public void atualizarQuantidadeLote(Long id, Integer quantidade) {
        LoteProduto lote = buscarUmLoteProduto(id);
        lote.setQuantidade(lote.getQuantidade() - quantidade);
        repository.save(lote);
    }

    public List<LoteProduto> buscarLotesAVencer(Long dias) {
        return repository.findAll().stream()
                .filter(lote -> LocalDate.now().plusDays(dias).isAfter(lote.getDataValidade())).toList();
    }

    public Boolean loteVencido(Long idLote) {
        LoteProduto lote = buscarUmLoteProduto(idLote);
        return lote.estaVencido();
    }
}
