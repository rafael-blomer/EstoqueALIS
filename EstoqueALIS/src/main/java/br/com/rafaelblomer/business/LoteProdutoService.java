package br.com.rafaelblomer.business;

import java.time.LocalDate;
import java.util.List;

import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.event.LoteCriadoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.LoteProdutoConverter;
import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.entities.Usuario;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoteProdutoService {

    @Autowired
    private LoteProdutoRepository repository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private LoteProdutoConverter converter;

    @Transactional
    public LoteProdutoResponseDTO cadastrarLote(LoteProdutoCadastroDTO dto) {
        validarDto(dto);
        Produto produto = produtoService.buscarProdutoId(dto.produtoId());
        produtoService.verificarProdutoAtivo(produto);
        LoteProduto loteProduto = converter.dtoParaLoteProdutoEntity(dto, produto);
        repository.save(loteProduto);
        publisher.publishEvent(new LoteCriadoEvent(loteProduto));
        return converter.paraLoteProdutoDTO(loteProduto);
    }

    //ÚTEIS

    private void validarDto(LoteProdutoCadastroDTO dto) {
        if (dto.quantidadeLote() <= 0)
            throw new DadoIrregularException("A quantidade total do lote tem que ser maior que 0.");
        if (dto.dataValidade().isBefore(LocalDate.now()))
            throw new DadoIrregularException("A data de validade tem que ser após a data atual");
    }

    public List<LoteProduto> buscarLoteProdutoPorDataValidade(Long produtoId) {
        return repository.findLotesDisponiveisOrdenadosPorValidade(produtoId);
    }

    public void salvarAlteracoes(List<LoteProduto> alterados) {
        repository.saveAll(alterados);
    }
}
