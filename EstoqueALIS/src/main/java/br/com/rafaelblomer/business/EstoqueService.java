package br.com.rafaelblomer.business;

import java.util.List;

import br.com.rafaelblomer.infrastructure.entities.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.EstoqueConverter;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.EstoqueRepository;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository repository;

    @Autowired
    private EstoqueConverter converter;

    @Autowired
    private UsuarioService usuarioService;

    public EstoqueResponseDTO criarNovoEstoque(String token) {
        Estoque estoque = new Estoque(usuarioService.findByToken(token));
        repository.save(estoque);
        return converter.entityParaResponseDTO(estoque);
    }

    public List<EstoqueResponseDTO> buscarTodosEstoquesUsuario(String token) {
        Usuario usuario = usuarioService.findByToken(token);
        return repository.findByUsuario(usuario)
                .stream()
                .filter(Estoque::getAtivo)
                .map(e -> converter.entityParaResponseDTO(e))
                .toList();
    }

    public void desativarEstoque(String token, Long id) {
        Usuario usuario = usuarioService.findByToken(token);
        Estoque estoque = buscarEstoqueEntityId(id);
        verificarEstoqueUsuario(estoque, usuario);
        estoque.setAtivo(false);
        repository.save(estoque);
    }

    public EstoqueResponseDTO buscarUmEstoque(String token, Long id) {
        Usuario usuario = usuarioService.findByToken(token);
        Estoque estoque = buscarEstoqueEntityId(id);
        verificarEstoqueUsuario(estoque, usuario);
        return converter.entityParaResponseDTO(estoque);
    }

    //ÚTEIS

    public Estoque buscarEstoqueEntityId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Estoque não foi encontrado."));
    }

    public void verificarEstoqueAtivo(Estoque estoque) {
        if (!estoque.getAtivo())
            throw new ObjetoInativoException("Estoque não está ativo");
    }

    public void verificarEstoqueUsuario(Estoque estoque, Usuario usuario) {
        if(!estoque.getUsuario().equals(usuario))
            throw new AcaoNaoPermitidaException("O usuário não tem permissão para fazer essa ação.");
    }

    public void verificarEstoqueProduto(Estoque estoque, Produto produto) {
        if (!produto.getEstoque().equals(estoque))
            throw new AcaoNaoPermitidaException("O produto não faz parte desse estoque.");
    }
}
