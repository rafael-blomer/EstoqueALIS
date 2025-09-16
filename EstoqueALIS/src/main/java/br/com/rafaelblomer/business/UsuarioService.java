package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.dtos.*;
import br.com.rafaelblomer.business.exceptions.VerficacaoEmailException;
import br.com.rafaelblomer.infrastructure.entities.VerificacaoTokenUsuario;
import br.com.rafaelblomer.infrastructure.repositories.VerificacaoTokenUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.UsuarioConverter;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.UsuarioRepository;
import br.com.rafaelblomer.infrastructure.security.JwtUtil;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificacaoTokenUsuarioRepository tokenRepository;

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioCadastroDTO entityCadastro) {
        Usuario entity = converter.dtoCadastroParaEntity(entityCadastro);
        entity.setSenha(encoder.encode(entity.getSenha()));
        entity = repository.save(entity);
        String tokenString = UUID.randomUUID().toString();
        VerificacaoTokenUsuario verificacaoTokenUsuario = new VerificacaoTokenUsuario(tokenString, entity);
        tokenRepository.save(verificacaoTokenUsuario);
        emailService.sendVerificationEmail(entity, tokenString);
        return converter.entityParaResponseDTO(entity);
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
        Usuario usuario = buscarPorEmail(dto.email());
        verificarUsuarioAtivo(usuario);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.email(), dto.senha()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateToken(authentication.getName());
        return "Bearer " + jwtToken;
    }

    public UsuarioResponseDTO buscarUsuarioDTOToken(String token) {
        Usuario usuario = findByToken(token);
        return converter.entityParaResponseDTO(usuario);
    }

    public String verificarUsuario(String token) {
        VerificacaoTokenUsuario verificationToken = tokenRepository.findByToken(token);
        verificarTokenEmail(verificationToken);
        Usuario usuario = verificationToken.getUser();
        usuario.setAtivo(true);
        repository.save(usuario);
        tokenRepository.delete(verificationToken);
        return "valido";
    }

    public String esqueciSenha(String email) {
        Usuario usuario = buscarPorEmail(email);
        String token = UUID.randomUUID().toString();
        VerificacaoTokenUsuario verificacaoTokenUsuario = new VerificacaoTokenUsuario(token, usuario);
        tokenRepository.save(verificacaoTokenUsuario);
        emailService.sendChangePasswordEmail(usuario, token);
        return "Email para alteração de senha enviado.";
    }

    public String alterarSenha (String token, String senhaNova) {
        VerificacaoTokenUsuario verificationToken = tokenRepository.findByToken(token);
        verificarTokenEmail(verificationToken);
        Usuario usuario = verificationToken.getUser();
        usuario.setSenha(encoder.encode(senhaNova));
        repository.save(usuario);
        tokenRepository.delete(verificationToken);
        return "Senha alterada com sucesso.";
    }

    //ÚTEIS

    private Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email).orElseThrow(
                () -> new ObjetoNaoEncontradoException("Email não cadastrado. Tente novamente"));
    }

    private void verificarTokenEmail(VerificacaoTokenUsuario verificationToken) {
        if (verificationToken == null)
            throw new VerficacaoEmailException("O token de verificação está inválido");
        else if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
            throw new VerficacaoEmailException("O token expirou.");
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
