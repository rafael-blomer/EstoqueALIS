package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.dtos.UsuarioAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioLoginDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.business.exceptions.UsuarioInativoException;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioConverter converter;

    public UsuarioResponseDTO criarUsuario(UsuarioCadastroDTO entityCadastro) {
        Usuario entity = converter.dtoCadastroParaEntity(entityCadastro);
        return converter.entityParaResponseDTO(repository.save(entity));
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        return converter.entityParaResponseDTO(buscarUsuarioEntity(id));
    }

    public List<UsuarioResponseDTO> buscarTodosUsuarios() {
        return repository.findAll()
                .stream()
                .filter(e -> e.getAtivo().equals(true))
                .map(u -> converter.entityParaResponseDTO(u))
                .toList();
    }

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioAtualizacaoDTO novo) {
        Usuario antigo = buscarUsuarioEntity(id);
        verificarUsuarioAtivo(antigo);
        atualizarDadosUsuario(antigo, novo);
        return converter.entityParaResponseDTO(repository.save(antigo));
    }

    public void alterarStatusAtivoUsuario(Long id) {
        Usuario entity = buscarUsuarioEntity(id);
        entity.setAtivo(!entity.getAtivo());
        repository.save(entity);
    }

    //TODO: implementar Security e verificar se usuario está ativo
    public void realizarLogin(UsuarioLoginDTO dto) {

    }

    private void atualizarDadosUsuario(Usuario antigo, UsuarioAtualizacaoDTO novo) {
        if(novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.telefone() != null)
            antigo.setTelefone(novo.telefone());
        if (novo.senha() != null)
            antigo.setSenha(novo.senha());
    }

    private Usuario buscarUsuarioEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("O usuário não foi encontrado"));
    }

    private void verificarUsuarioAtivo(Usuario entity) {
        if(entity.getAtivo())
            throw new UsuarioInativoException("O usuário não está ativo.");
    }
}
