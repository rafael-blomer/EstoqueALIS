package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.EstoqueConverter;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
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
        Estoque estoque = new Estoque(usuarioService.findByToken(token));
        repository.save(estoque);
        return converter.entityParaResponseDTO(estoque);
    }

    public List<EstoqueResponseDTO> buscarTodosEstoques() {
        return null;
    }
}
