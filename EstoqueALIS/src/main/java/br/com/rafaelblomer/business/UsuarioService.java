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
import br.com.rafaelblomer.infrastructure.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioConverter converter;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;


    public UsuarioResponseDTO criarUsuario(UsuarioCadastroDTO entityCadastro) {
        Usuario entity = converter.dtoCadastroParaEntity(entityCadastro);
        entity.setSenha(encoder.encode(entity.getSenha()));
        return converter.entityParaResponseDTO(repository.save(entity));
    }

    //fazer exceção personalizada caso de erro ao buscar token
    public UsuarioResponseDTO buscarUsuarioToken(String token) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        return converter.entityParaResponseDTO(findByEmail(email));
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

    public String realizarLogin(UsuarioLoginDTO dto) {
        Usuario usuario = findByEmail(dto.email());
        if (!usuario.getAtivo())
            throw new UsuarioInativoException("Usuario foi desativado");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateToken(authentication.getName());
        return "Bearer " + jwtToken;
    }

    private void atualizarDadosUsuario(Usuario antigo, UsuarioAtualizacaoDTO novo) {
        if(novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.telefone() != null)
            antigo.setTelefone(novo.telefone());
        if (novo.senha() != null)
            antigo.setSenha(novo.senha());
    }

    public Usuario buscarUsuarioEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ObjetoNaoEncontradoException("O usuário não foi encontrado"));
    }

    private void verificarUsuarioAtivo(Usuario entity) {
        if(entity.getAtivo())
            throw new UsuarioInativoException("O usuário não está ativo.");
    }

    private Usuario findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }


}
