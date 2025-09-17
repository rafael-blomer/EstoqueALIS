package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.LoteProdutoService;
import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loteproduto")
public class LoteProdutoController {

    @Autowired
    private LoteProdutoService service;

    @PostMapping
    public ResponseEntity<LoteProdutoResponseDTO> criarLoteProduto(@RequestBody @Valid LoteProdutoCadastroDTO dto) {
        return ResponseEntity.ok().body(service.cadastrarLote(dto));
    }
}
