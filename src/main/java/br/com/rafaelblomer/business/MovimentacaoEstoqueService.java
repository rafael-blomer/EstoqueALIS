package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.exceptions.EntidadeNaoEncontrada;
import br.com.rafaelblomer.infrastructure.model.MovimentacaoEstoque;
import br.com.rafaelblomer.infrastructure.model.Produto;
import br.com.rafaelblomer.infrastructure.repositories.MovimentacaoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private MovimentacaoEstoqueRepository repository;

    @Transactional
    public MovimentacaoEstoque criarNovaMovimentacaoEstoque(MovimentacaoEstoque movimentacaoEstoque) {
        return repository.save(movimentacaoEstoque);
    }

    public MovimentacaoEstoque buscarUmaMovimentacaoEstoque(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntidadeNaoEncontrada("Movimentação no etoque não encontrada na base de dados."));
    }

    public List<MovimentacaoEstoque> buscartTodasMovimentacoesEstoque() {
        return repository.findAll();
    }

    @Transactional
    public void excluirMovimentacaoEstoque (Long id) {
        repository.delete(buscarUmaMovimentacaoEstoque(id));
    }
}
