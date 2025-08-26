package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.ProdutoService;
import br.com.rafaelblomer.business.dtos.ProdutoAtualizacaoDTO;
import br.com.rafaelblomer.business.dtos.ProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.ProdutoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criarNovoProduto(@RequestBody @Valid ProdutoCadastroDTO dto) {
        return ResponseEntity.ok().body(service.criarProduto(dto));
    }

    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizarDadosProduto(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody ProdutoAtualizacaoDTO dto) {
        return ResponseEntity.ok().body(service.atualizarProduto(id, dto, token));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarProdutoPorId(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return ResponseEntity.ok().body(service.buscarProdutoPorId(id, token));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorUsuario(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.buscarTodosProdutosUsuario(token));
    }

    @GetMapping("/estoque/{id}")
    public ResponseEntity<List<ProdutoResponseDTO>> buscarProdutosPorEstoque(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return ResponseEntity.ok().body(service.buscarTodosProdutosEstoque(id, token));
    }

    @PatchMapping("/desativar/{id}")
    public ResponseEntity<Void> desativarProduto(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        service.desativarProduto(id, token);
        return ResponseEntity.noContent().build();
    }
}
