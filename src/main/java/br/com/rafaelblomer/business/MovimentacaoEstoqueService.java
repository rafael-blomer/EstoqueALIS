package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converter.ConverterMovEstoque;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueDTO;
import br.com.rafaelblomer.business.exceptions.EntidadeNaoEncontrada;
import br.com.rafaelblomer.infrastructure.model.MovimentacaoEstoque;
import br.com.rafaelblomer.infrastructure.model.enums.TipoMovimentacao;
import br.com.rafaelblomer.infrastructure.repositories.MovimentacaoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private MovimentacaoEstoqueRepository repository;

    @Autowired
    private ConverterMovEstoque converter;

    @Autowired
    private LoteProdutoService loteProdutoService;

    @Transactional
    public MovimentacaoEstoque registrarEntrada(MovimentacaoEstoqueDTO dto) {
        MovimentacaoEstoque entity = converter.paraMovEstoqueEntity(dto, TipoMovimentacao.ENTRADA);
        return repository.save(entity);
    }

    @Transactional
    public MovimentacaoEstoque registrarSaida(MovimentacaoEstoqueDTO dto) {
        MovimentacaoEstoque entity = converter.paraMovEstoqueEntity(dto, TipoMovimentacao.SAIDA);
        for (var movLote : entity.getLotes()) {
            loteProdutoService.atualizarQuantidadeLote(movLote.getLote().getId(), movLote.getQuantidade());
        }
        return repository.save(entity);
    }


    public MovimentacaoEstoque buscarUmaMovimentacaoEstoque(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntidadeNaoEncontrada("Movimentação no estoque não encontrada na base de dados."));
    }

    public List<MovimentacaoEstoque> buscarTodasMovimentacoesEstoque() {
        return repository.findAll();
    }

    public List<MovimentacaoEstoque> buscarMovimentacoesPorProduto(Long produtoId) {
        return repository.findAll().stream()
                .filter(mov -> mov.getLotes().stream()
                        .anyMatch(l -> l.getLote().getProduto().getId().equals(produtoId)))
                .collect(Collectors.toList());
    }

}
