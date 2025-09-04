package br.com.rafaelblomer.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.dtos.UsuarioAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioLoginDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.UsuarioRepository;
import br.com.rafaelblomer.infrastructure.security.JwtUtil;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioCadastroDTO entityCadastro) {
        Usuario entity = converter.dtoCadastroParaEntity(entityCadastro);
        entity.setSenha(encoder.encode(entity.getSenha()));
        return converter.entityParaResponseDTO(repository.save(entity));
    }



    @Transactional
    public UsuarioResponseDTO atualizarUsuario(String token, UsuarioAtualizacaoDTO novo) {
        Usuario antigo = findByToken(token);
        validarTelefone(antigo.getId(), novo.telefone());
        atualizarDadosUsuario(antigo, novo);
        repository.save(antigo);
        return converter.entityParaResponseDTO(antigo);
    }

    @Transactional
    public void alterarStatusAtivoUsuario(String token) {
        Usuario entity = findByToken(token);
        entity.setAtivo(false);
        repository.save(entity);
    }

    @Transactional
    public String realizarLogin(UsuarioLoginDTO dto) {
        Usuario usuario = repository.findByEmail(dto.email()).orElseThrow(() -> new ObjetoNaoEncontradoException("Email não cadastrado. Tente novamente"));
        verificarUsuarioAtivo(usuario);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateToken(authentication.getName());
        return "Bearer " + jwtToken;
    }

    //ÚTEIS

    public UsuarioResponseDTO buscarUsuarioDTOToken(String token) {
        Usuario usuario = findByToken(token);
        return converter.entityParaResponseDTO(usuario);
    }

    public Usuario findByToken(String token) {
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        return repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }

    private void atualizarDadosUsuario(Usuario antigo, UsuarioAtualizacaoDTO novo) {
        if(novo.nome() != null)
            antigo.setNome(novo.nome());
        if (novo.telefone() != null)
            antigo.setTelefone(novo.telefone());
        if (novo.senha() != null)
            antigo.setSenha(encoder.encode(novo.senha()));
    }

    private void verificarUsuarioAtivo(Usuario entity) {
        if(!entity.getAtivo())
            throw new ObjetoInativoException("O usuário não está ativo.");
    }

    private void validarTelefone(Long id, String telefone) {
        if(repository.existsByTelefoneAndIdNot(telefone, id))
            throw new DadoIrregularException("Número de telefone irregular. Tente outro.");
    }
}
