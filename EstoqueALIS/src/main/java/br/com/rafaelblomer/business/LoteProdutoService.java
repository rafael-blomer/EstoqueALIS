package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoteProdutoService {

    @Autowired
    private LoteProdutoRepository repository;

    //CRIAR DTOS
    public LoteProdutoResponseDTO cadastrarLote(Long produtoId, Long estoqueId, LoteProdutoCadastroDTO dto) {
        return null;
    }

    //CRIAR NO REPOSITORY METODO PARA TRAZER LOTES POR PRODUTO
    public List<LoteProdutoResponseDTO> buscarLotesPorProduto(Long produtoId) {
        return null;
    }
}
