package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    //Criar ProdutoResponseDTO e ProdutoCadastroDTO
    public ProdutoResponseDTO criarProduto(ProdutoCadastroDTO dto) {
        return null;
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoAtualizacaoDTO dto) {
        return null;
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        return null;
    }

    public List<ProdutoResponseDTO> buscarTodosProdutos() {
        return null;
    }

    public void desativarProduto(Long id) {

    }
}
