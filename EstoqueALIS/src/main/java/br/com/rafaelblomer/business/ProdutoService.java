package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.ProdutoConverter;
import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProdutoConverter converter;

    @Autowired
    private EstoqueService estoqueService;

    public ProdutoResponseDTO criarProduto(ProdutoCadastroDTO dto, String token) {
        Usuario usuario = usuarioService.findByToken(token);
        Produto produto = converter.cadastroParaProdutoEntity(dto);
        Estoque estoque = estoqueService.buscarEstoqueEntityId(dto.idEstoque());
        estoqueService.verificarEstoqueAtivo(estoque);
        estoqueService.verificarEstoqueUsuario(estoque, usuario);
        produto.setEstoque(estoque);
        repository.save(produto);
        return converter.entityParaResponseDTO(produto, estoque);
    }

    public ProdutoResponseDTO atualizarProduto(Long idProduto, ProdutoAtualizacaoDTO dto, String token) {
        Produto antigo = buscarProdutoId(idProduto);
        verificarProdutoAtivo(antigo);
        estoqueService.verificarEstoqueAtivo(antigo.getEstoque());
        Usuario usuario = usuarioService.findByToken(token);
        verificarPermissaoProdutoUsuario(usuario, antigo);
        atualizarDadosProduto(antigo, dto);
        return converter.entityParaResponseDTO(repository.save(antigo), antigo.getEstoque());
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long id, String token) {
        Produto produto = buscarProdutoId(id);
        Usuario usuario = usuarioService.findByToken(token);
        verificarPermissaoProdutoUsuario(usuario, produto);
        return converter.entityParaResponseDTO(produto, produto.getEstoque());
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosUsuario(String token) {
        Usuario usuario = usuarioService.findByToken(token);
        return repository.findByEstoqueUsuarioId(usuario.getId())
                .stream()
                .filter(Produto::getAtivo)
                .filter(p -> p.getEstoque().getAtivo())
                .map(p -> converter.entityParaResponseDTO(p, p.getEstoque()))
                .toList();
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosEstoque(Long estoqueId, String token) {
        Usuario usuario = usuarioService.findByToken(token);
        verificarPermissaoEstoqueUsuario(estoqueId, usuario);
        return repository.findByEstoqueId(estoqueId)
                .stream()
                .filter(Produto::getAtivo)
                .map(p -> converter.entityParaResponseDTO(p, p.getEstoque()))
                .toList();
    }

    public void desativarProduto(Long id, String token) {
        Usuario usuario = usuarioService.findByToken(token);
        Produto produto = buscarProdutoId(id);
        verificarProdutoAtivo(produto);
        verificarPermissaoProdutoUsuario(usuario, produto);
        produto.setAtivo(false);
        repository.save(produto);
    }

    //ÚTEIS

    public Produto buscarProdutoId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Produto não encontrado"));
    }

    public void verificarProdutoAtivo(Produto produto) {
        if (!produto.getAtivo())
            throw new DadoIrregularException("O produto foi desativado.");
    }

    public void verificarPermissaoProdutoUsuario(Usuario usuario, Produto produto) {
        boolean permitido = usuario.getEstoques().stream()
                .anyMatch(estoque -> estoque.equals(produto.getEstoque()));

        if (!permitido) {
            throw new AcaoNaoPermitidaException("Você não tem permissão para realizar essa ação.");
        }
    }

    private void verificarPermissaoEstoqueUsuario(Long estoqueId, Usuario usuario) {
        for (Estoque i : usuario.getEstoques()){
            if (estoqueId.equals(i.getId()))
                return;
        }
        throw new AcaoNaoPermitidaException("Você não tem permissão para realizar essa ação.");
    }

    private void atualizarDadosProduto(Produto antigo, ProdutoAtualizacaoDTO novo) {
        if(novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.marca() != null)
            antigo.setMarca(novo.marca());
        if (novo.descricao() != null)
            antigo.setDescricao((novo.descricao()));
    }
}
