package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoteProdutoService {

    @Autowired
    private LoteProdutoRepository repository;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioService usuarioService;

    public LoteProduto cadastrarLote(LoteProdutoCadastroDTO dto) {
        validarDto(dto);
        LoteProduto loteProduto = fazerLoteProduto(dto);
        return repository.save(loteProduto);
    }

    public List<LoteProduto> buscarLotesPorProduto(Long produtoId, String token) {
        Usuario usuario = usuarioService.findByToken(token);
        Produto produto = produtoService.buscarProdutoId(produtoId);
        produtoService.verificarPermissaoProdutoUsuario(usuario, produto);
        return repository.findByProdutoId(produtoId);
    }

    private LoteProduto fazerLoteProduto(LoteProdutoCadastroDTO dto) {
        return new LoteProduto(dto.dataValidade(),
                buscarEstoque(dto.estoqueId()),
                dto.loteFabricante(),
                buscarProduto(dto.produtoId()),
                dto.quantidadeLote());
    }

    private Produto buscarProduto(Long id) {
        return produtoService.buscarProdutoId(id);
    }

    private Estoque buscarEstoque(Long id) {
        return estoqueService.buscarEstoqueEntityId(id);
    }

    private void validarDto(LoteProdutoCadastroDTO dto) {
        if (dto.quantidadeLote() <= 0)
            throw new DadoIrregularException("A quantidade total do lote tem que ser maior que 0.");
        if (dto.dataValidade().isBefore(LocalDate.now()))
            throw new DadoIrregularException("A data de validade tem que ser após a data atual");
    }
}
