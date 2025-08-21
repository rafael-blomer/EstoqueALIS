package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.UsuarioService;
import br.com.rafaelblomer.business.dtos.UsuarioCadastroDTO;
import br.com.rafaelblomer.business.dtos.UsuarioLoginDTO;
import br.com.rafaelblomer.business.dtos.UsuarioResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastro(@RequestBody UsuarioCadastroDTO cadastro) {
        return ResponseEntity.ok().body(service.criarUsuario(cadastro));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsuarioLoginDTO login) {
        return ResponseEntity.ok().body(service.realizarLogin(login));
    }

    @GetMapping("/meuuser")
    public ResponseEntity<UsuarioResponseDTO> buscarPorToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.buscarUsuarioToken(token));
    }
}
