package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.EstoqueConverter;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository repository;

    @Autowired
    private EstoqueConverter converter;

    @Autowired
    private UsuarioService usuarioService;

    public EstoqueResponseDTO criarNovoEstoque(String token) {
        Estoque estoque = new Estoque(buscarUsuarioPorToken(token));
        repository.save(estoque);
        return converter.entityParaResponseDTO(estoque);
    }

    public List<EstoqueResponseDTO> buscarTodosEstoquesUsuario(String token) {
        Usuario usuario = buscarUsuarioPorToken(token);
        return repository.findByUsuario(usuario)
                .stream()
                .map(e -> converter.entityParaResponseDTO(e))
                .toList();
    }

    public void desativarEstoque(String token, Long id) {
        Usuario usuario = buscarUsuarioPorToken(token);
        Estoque estoque = buscarEstoqueEntityId(id);
        verificarEstoqueUsuario(estoque, usuario);
        estoque.setAtivo(false);
        repository.save(estoque);
    }

    public EstoqueResponseDTO buscarUmEstoque(String token, Long id) {
        Usuario usuario = buscarUsuarioPorToken(token);
        Estoque estoque = buscarEstoqueEntityId(id);
        verificarEstoqueUsuario(estoque, usuario);
        return converter.entityParaResponseDTO(estoque);
    }

    private Usuario buscarUsuarioPorToken(String token) {
        return usuarioService.findByToken(token);
    }

    public Estoque buscarEstoqueEntityId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("Estoque não foi encontrado."));
    }

    private void verificarEstoqueUsuario(Estoque estoque, Usuario usuario) {
        if(!estoque.getUsuario().equals(usuario))
            throw new AcaoNaoPermitidaException("O usuário não tem permissão para fazer essa ação.");
    }
}
