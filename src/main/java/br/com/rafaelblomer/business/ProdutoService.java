package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.exceptions.EntidadeNaoEncontrada;
import br.com.rafaelblomer.infrastructure.model.LoteProduto;
import br.com.rafaelblomer.infrastructure.model.Produto;
import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Transactional
    public Produto criarNovoProduto(Produto produto) {
        return repository.save(produto);
    }

    public Produto buscarUmProduto(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntidadeNaoEncontrada("Produto não encontrado na base de dados."));
    }

    public List<Produto> buscarTodosProdutosAtivos() {
        return repository.findAll()
                .stream()
                .filter(Produto::getAtivo)
                .collect(Collectors.toList());
    }

    public void desativarProduto (Long id) {
        Produto produto = buscarUmProduto(id);
        produto.setAtivo(false);
        repository.save(produto);
    }

    public Integer quantidadeDeItensDoProduto(Long id) {
        Produto entity = buscarUmProduto(id);
        int quantidadeTotal = 0;
        List<LoteProduto> list = entity.getLoteProdutos().stream().filter(loteProduto -> loteProduto.getQuantidade() > 0 && !loteProduto.estaVencido()).toList();
        for (LoteProduto i: list) {
            quantidadeTotal += i.getQuantidade();
        }
        return quantidadeTotal;
    }
}
