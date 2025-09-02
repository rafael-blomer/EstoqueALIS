package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.MovimentacaoEstoqueService;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoEstoqueController {

    @Autowired
    private MovimentacaoEstoqueService service;

    @PostMapping("/saida")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> realizarRetirada(@RequestBody MovimentacaoSaidaDTO dto) {
        return ResponseEntity.ok().body(service.registrarSaida(dto));
    }
}
