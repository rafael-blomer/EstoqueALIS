package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.UsuarioService;
import br.com.rafaelblomer.business.dtos.UsuarioAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioLoginDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastro(@Valid @RequestBody UsuarioCadastroDTO cadastro) {
        return ResponseEntity.ok().body(service.criarUsuario(cadastro));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UsuarioLoginDTO login) {
        return ResponseEntity.ok().body(service.realizarLogin(login));
    }

    @GetMapping
    public ResponseEntity<UsuarioResponseDTO> buscarPorToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.buscarUsuarioDTOToken(token));
    }

    @PatchMapping("/desativar")
    public ResponseEntity<Void> desativarUsuario(@RequestHeader("Authorization") String token) {
        service.alterarStatusAtivoUsuario(token);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/atualizar")
    public ResponseEntity<UsuarioResponseDTO> atualizarDados (@RequestHeader("Authorization") String token, @Valid @RequestBody UsuarioAtualizacaoDTO dto) {
        return ResponseEntity.ok().body(service.atualizarUsuario(token, dto));
    }
}
