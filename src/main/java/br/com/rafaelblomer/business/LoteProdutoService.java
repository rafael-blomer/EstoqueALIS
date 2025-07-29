package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.exceptions.EntidadeNaoEncontrada;
import br.com.rafaelblomer.infrastructure.model.LoteProduto;
import br.com.rafaelblomer.infrastructure.model.Produto;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LoteProdutoService {

    @Autowired
    private LoteProdutoRepository repository;

    @Transactional
    public LoteProduto criarNovoLoteProduto(LoteProduto loteProduto) {
        return repository.save(loteProduto);
    }

    public LoteProduto buscarUmLoteProduto(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntidadeNaoEncontrada("Lote de produto não encontrado na base de dados."));
    }

    public List<LoteProduto> buscarLotesPorProduto(Long produtoId) {
        return repository.findAll().stream().filter(lote -> Objects.equals(lote.getProduto().getId(), produtoId)).collect(Collectors.toList());
    }
}
