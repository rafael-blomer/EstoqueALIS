package br.com.rafaelblomer.business;

import java.time.LocalDate;
import java.util.List;

import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.LoteProdutoConverter;
import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;

@Service
public class LoteProdutoService {

    @Autowired
    private LoteProdutoRepository repository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Autowired
    private LoteProdutoConverter converter;

    public LoteProdutoResponseDTO cadastrarLote(LoteProdutoCadastroDTO dto) {
        validarDto(dto);
        Produto produto = produtoService.buscarProdutoId(dto.produtoId());
        produtoService.verificarProdutoAtivo(produto);
        LoteProduto loteProduto = converter.dtoParaLoteProdutoEntity(dto, produto);
        repository.save(loteProduto);
        movimentacaoEstoqueService.registrarEntrada(loteProduto);
        return converter.paraLoteProdutoDTO(loteProduto);
    }

    public List<LoteProdutoResponseDTO> buscarLotesPorProduto(Long produtoId, String token) {
        Usuario usuario = usuarioService.findByToken(token);
        Produto produto = produtoService.buscarProdutoId(produtoId);
        produtoService.verificarPermissaoProdutoUsuario(usuario, produto);
        return repository.findByProdutoId(produtoId).stream().map(lp -> converter.paraLoteProdutoDTO(lp)).toList();
    }

    //ÚTEIS

    private void validarDto(LoteProdutoCadastroDTO dto) {
        if (dto.quantidadeLote() <= 0)
            throw new DadoIrregularException("A quantidade total do lote tem que ser maior que 0.");
        if (dto.dataValidade().isBefore(LocalDate.now()))
            throw new DadoIrregularException("A data de validade tem que ser após a data atual");
    }

    public LoteProduto buscarLoteProdutoEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Lote de produot não encontrado."));
    }
}
