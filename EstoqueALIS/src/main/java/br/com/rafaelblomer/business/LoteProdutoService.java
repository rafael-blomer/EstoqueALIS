package br.com.rafaelblomer.business;

import java.time.LocalDate;
import java.util.List;

import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
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

    /**
     * Cadastra um novo lote de produto.
     * Valida os dados recebidos, verifica se o produto existe e está ativo,
     * converte o DTO em entidade, persiste no banco de dados e publica
     * um evento de criação de lote.
     *
     * @param dto DTO contendo as informações do lote (quantidade, validade, produtoId, etc.)
     * @return DTO de resposta representando o lote cadastrado
     * @throws DadoIrregularException se a quantidade ou data de validade forem inválidas
     * @throws ObjetoNaoEncontradoException se o produto não for encontrado
     * @throws ObjetoInativoException se o produto estiver inativo
     */
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

    /**
     * Valida os dados do DTO de cadastro do lote.
     * @param dto DTO a ser validado
     * @throws DadoIrregularException se a quantidade for menor ou igual a zero
     *                                ou se a data de validade for anterior à data atual
     */
    private void validarDto(LoteProdutoCadastroDTO dto) {
        if (dto.quantidadeLote() <= 0)
            throw new DadoIrregularException("A quantidade total do lote tem que ser maior que 0.");
        if (dto.dataValidade().isBefore(LocalDate.now()))
            throw new DadoIrregularException("A data de validade tem que ser após a data atual");
    }


    /**
     * Busca todos os lotes disponíveis de um produto, ordenados pela data de validade.
     * @param produtoId ID do produto
     * @return Lista de lotes disponíveis, ordenada por validade (do mais próximo ao mais distante)
     */
    public List<LoteProduto> buscarLoteProdutoPorDataValidade(Long produtoId) {
        return repository.findLotesDisponiveisOrdenadosPorValidade(produtoId);
    }

    /**
     * Persiste alterações feitas em uma lista de lotes.
     * @param alterados Lista de lotes com alterações a serem salvas
     */
    public void salvarAlteracoes(List<LoteProduto> alterados) {
        repository.saveAll(alterados);
    }
}
