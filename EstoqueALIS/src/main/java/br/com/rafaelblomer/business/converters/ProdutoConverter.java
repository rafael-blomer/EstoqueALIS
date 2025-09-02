package br.com.rafaelblomer.business.converters;

import br.com.rafaelblomer.business.RelatorioService;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProdutoConverter {

    @Autowired
    private EstoqueConverter estoqueConverter;

    @Autowired
    private RelatorioService relatorioService;

    public Produto cadastroParaProdutoEntity(ProdutoCadastroDTO dto) {
        return new Produto(dto.nome(), dto.marca(), dto.descricao(), 0, new ArrayList<>(), new Estoque());
    }

    public ProdutoResponseDTO entityParaResponseDTO(Produto produto, Estoque estoque) {
        relatorioService.calcularQuantidadeTotalProduto(produto);
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getMarca(),
                produto.getDescricao(),
                produto.getQuantidadeTotal(),
                estoqueConverter.entityParaResponseDTO(estoque));
    }
}
