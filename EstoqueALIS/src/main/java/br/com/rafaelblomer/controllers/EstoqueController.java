package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.EstoqueService;
import br.com.rafaelblomer.business.dtos.EstoqueCadastroDTO;
import br.com.rafaelblomer.business.dtos.EstoqueResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoques")
public class EstoqueController {

    @Autowired
    private EstoqueService service;

    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> criarNovoEstoque(@RequestHeader("Authorization") String token, @RequestBody @Valid EstoqueCadastroDTO cadastro) {
        return ResponseEntity.ok().body(service.criarNovoEstoque(token, cadastro));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> desativarEstoque(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        service.desativarEstoque(token, id);
        return ResponseEntity.noContent().build();
    }
}
