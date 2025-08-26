package br.com.rafaelblomer.business;

import br.com.rafaelblomer.business.dtos.MovimentacaoEntradaDTO;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import br.com.rafaelblomer.infrastructure.repositories.MovimentacaoEstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private MovimentacaoEstoqueRepository repository;

    //fazer dtos
    public MovimentacaoEstoqueResponseDTO registrarEntrada(Long estoqueId, MovimentacaoEntradaDTO dto) {
        return null;
    }

    public MovimentacaoEstoqueResponseDTO registrarSaida(Long estoqueId, MovimentacaoSaidaDTO dto) {
        return null;
    }

    //fazer por usuario
    public List<MovimentacaoEstoqueResponseDTO> historicoMovimentacoes(Long estoqueId) {
        return null;
    }
}
