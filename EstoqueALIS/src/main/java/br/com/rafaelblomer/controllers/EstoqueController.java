package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.EstoqueService;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoques")
public class EstoqueController {

    @Autowired
    private EstoqueService service;

    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> criarNovoEstoque(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.criarNovoEstoque(token));
    }

    @GetMapping("/todos")
    public ResponseEntity<List<EstoqueResponseDTO>> buscarTodosEstoques(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.buscarTodosEstoquesUsuario(token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> buscarUmEstoque(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return ResponseEntity.ok().body(service.buscarUmEstoque(token, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> desativarEstoque(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        service.desativarEstoque(token, id);
        return ResponseEntity.noContent().build();
    }
}
