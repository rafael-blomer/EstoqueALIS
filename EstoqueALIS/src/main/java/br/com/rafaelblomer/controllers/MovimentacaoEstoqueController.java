package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.MovimentacaoEstoqueService;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/movimentacoes")
public class MovimentacaoEstoqueController {

    @Autowired
    private MovimentacaoEstoqueService service;

    @PostMapping("/saida")
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> realizarRetirada(@RequestBody MovimentacaoSaidaDTO dto) {
        return ResponseEntity.ok().body(service.registrarSaida(dto));
    }

    @GetMapping("/estoque")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> buscarMovimentacoesPorEstoque(@RequestHeader("Authorization") String token, @RequestParam Long estoqueId) {
        return ResponseEntity.ok().body(service.listarHistoricoMovimentacoesEstoque(token, estoqueId));
    }

    @GetMapping("/produto")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> buscarMovimentacoesPorProduto(@RequestHeader("Authorization") String token, @RequestParam Long estoqueId, @RequestParam Long produtoId) {
        return ResponseEntity.ok().body(service.listarHistoricoMovimentacoesProduto(token, estoqueId, produtoId));
    }

    @GetMapping("/data")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> buscarMovimentacoesPorData(@RequestHeader("Authorization") String token, @RequestParam Long estoqueId, @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFinal) {
        return ResponseEntity.ok().body(service.listarHistoricoMovimentacoesData(token, estoqueId, dataInicio, dataFinal));
    }

    @GetMapping("/produtodata")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> buscarMovimentacoesPorProdutoEData(@RequestHeader("Authorization") String token, @RequestParam Long estoqueId, @RequestParam Long produtoId, @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFinal) {
        return ResponseEntity.ok().body(service.listarHistoricoMovimentacoesProdutoEData(token, estoqueId, produtoId, dataInicio, dataFinal));
    }
}
