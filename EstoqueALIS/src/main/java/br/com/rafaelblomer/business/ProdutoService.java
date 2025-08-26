package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.ProdutoConverter;
import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
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

    public ProdutoResponseDTO criarProduto(ProdutoCadastroDTO dto) {
        Produto produto = converter.cadastroParaProdutoEntity(dto);
        repository.save(produto);
        return converter.entityParaResponseDTO(produto);
    }

    public ProdutoResponseDTO atualizarProduto(Long idProduto, ProdutoAtualizacaoDTO dto, String token) {
        Produto antigo = buscarProdutoId(idProduto);
        Usuario usuario = buscarUsuarioPorToken(token);
        verificarPermissaoProdutoUsuario(usuario, antigo);
        atualizarDadosProduto(antigo, dto);
        return converter.entityParaResponseDTO(repository.save(antigo));
    }

    public ProdutoResponseDTO buscarProdutoPorId(Long id, String token) {
        Produto produto = buscarProdutoId(id);
        Usuario usuario = buscarUsuarioPorToken(token);
        verificarPermissaoProdutoUsuario(usuario, produto);
        return converter.entityParaResponseDTO(produto);
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosUsuario(String token) {
        Usuario usuario = buscarUsuarioPorToken(token);
        return repository.buscarProdutosPorUsuario(usuario.getId())
                .stream()
                .map(p -> converter.entityParaResponseDTO(p))
                .toList();
    }

    public List<ProdutoResponseDTO> buscarTodosProdutosEstoque(Long estoqueId, String token) {
        Usuario usuario = buscarUsuarioPorToken(token);
        verificarPermissaoEstoqueUsuario(estoqueId, usuario);
        return repository.buscarProdutosPorEstoque(estoqueId)
                .stream()
                .map(p -> converter.entityParaResponseDTO(p))
                .toList();
    }

    public void desativarProduto(Long id, String token) {
        Usuario usuario = buscarUsuarioPorToken(token);
        Produto produto = buscarProdutoId(id);
        verificarPermissaoProdutoUsuario(usuario, produto);
        produto.setAtivo(false);
        repository.save(produto);
    }

    private Usuario buscarUsuarioPorToken(String token) {
        return usuarioService.findByToken(token);
    }

    private Produto buscarProdutoId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Produto não encontrado"));
    }

    private void verificarPermissaoProdutoUsuario(Usuario usuario, Produto produto) {
        if (usuario.getEstoques().stream()
                .flatMap(estoque -> estoque.getLotes().stream())
                .noneMatch(lote -> lote.getProduto().equals(produto))) {
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
