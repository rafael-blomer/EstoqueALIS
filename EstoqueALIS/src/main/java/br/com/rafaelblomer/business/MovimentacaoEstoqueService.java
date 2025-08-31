package br.com.rafaelblomer.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.MovimentacaoEstoqueConverter;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.repositories.MovimentacaoEstoqueRepository;

@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private MovimentacaoEstoqueRepository repository;

    @Autowired
    private MovimentacaoEstoqueConverter converter;

    @Autowired
    private EstoqueService estoqueService;

    /*public MovimentacaoEstoqueResponseDTO registrarSaida(MovimentacaoSaidaDTO dto) {
        Estoque estoque = estoqueService.buscarEstoqueEntityId(dto.estoqueId());

        MovimentacaoEstoque movEstoque = converter.dtoSaidaParaMovEstoqueEntity(dto, estoque);
        return null;
    }*/

    //fazer por usuario
    public List<MovimentacaoEstoqueResponseDTO> historicoMovimentacoes(Long estoqueId) {
        return null;
    }

    //ÚTEIS

    public void registrarEntrada(LoteProduto loteProduto) {
        repository.save(converter.loteProdParaMovEstoque(loteProduto));
    }
}
