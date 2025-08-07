package br.com.rafaelblomer.business.converter;

import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueDTO;
import br.com.rafaelblomer.infrastructure.model.MovimentacaoEstoque;
import br.com.rafaelblomer.infrastructure.model.enums.TipoMovimentacao;
import org.springframework.stereotype.Component;

@Component
public class ConverterMovEstoque {

    public MovimentacaoEstoque paraMovEstoqueEntity(MovimentacaoEstoqueDTO dto, TipoMovimentacao movimentacao) {
        return new MovimentacaoEstoque(dto.loteProduto(), dto.quantidade(), movimentacao);
    }
}
