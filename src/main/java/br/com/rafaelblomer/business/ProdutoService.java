package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.exceptions.EntidadeNaoEncontrada;
import br.com.rafaelblomer.infrastructure.model.Produto;
import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Produto> buscarTodosProdutos() {
        return repository.findAll();
    }

    public void desativarProduto (Long id) {
        Produto produto = buscarUmProduto(id);
        produto.setAtivo(false);
        repository.save(produto);
    }

    @Transactional
    public Produto alterarDadosProduto(Produto novo, Long id) {
        Produto antigo = buscarUmProduto(id);
        atualizarDados(antigo, novo);
        return repository.save(antigo);
    }

    private void atualizarDados(Produto antigo, Produto novo) {
        if (novo.getNome() != null)
            antigo.setNome(novo.getNome());
        if (novo.getDescricao() != null)
            antigo.setDescricao(novo.getDescricao());
        if (novo.getCategoria() != null)
            antigo.setCategoria(novo.getCategoria());
    }
}
