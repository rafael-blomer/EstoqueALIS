package br.com.rafaelblomer.business;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {

    @Autowired
    LoteProdutoRepository loteProdutoRepository;

    public void calcularQuantidadeTotalProduto(Produto produto) {
        produto.setQuantidadeTotal(loteProdutoRepository.findByProdutoId(produto.getId()).stream()
                .filter(l -> l.getQuantidadeLote() > 0)
                .mapToInt(LoteProduto::getQuantidadeLote)
                .sum());
    }
}
