package br.com.rafaelblomer.business;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import br.com.rafaelblomer.business.exceptions.AcaoNaoPermitidaException;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.business.exceptions.ObjetoInativoException;
import br.com.rafaelblomer.business.exceptions.ObjetoNaoEncontradoException;
import br.com.rafaelblomer.infrastructure.entities.*;
import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;
import br.com.rafaelblomer.infrastructure.event.LoteCriadoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import br.com.rafaelblomer.business.converters.MovimentacaoEstoqueConverter;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.infrastructure.repositories.MovimentacaoEstoqueRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private MovimentacaoEstoqueRepository repository;

    @Autowired
    private MovimentacaoEstoqueConverter converter;

    @Autowired
    private EstoqueService estoqueService;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private LoteProdutoService loteProdutoService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Registra a saída de um produto do estoque.
     * Verifica se o estoque e o produto existem, estão ativos e relacionados,
     * garante que a quantidade desejada é válida e gera uma movimentação de saída
     * com base nos lotes disponíveis, atualizando suas quantidades.
     *
     * @param dto DTO contendo as informações da saída (estoqueId, produtoId, quantidade)
     * @return DTO de resposta representando a movimentação registrada
     * @throws ObjetoNaoEncontradoException se o estoque ou produto não forem encontrados
     * @throws ObjetoInativoException se o estoque ou produto estiverem inativos
     * @throws AcaoNaoPermitidaException se o produto não pertencer ao estoque
     * @throws DadoIrregularException se a quantidade desejada for maior que a disponível
     */
    @Transactional
    public MovimentacaoEstoqueResponseDTO registrarSaida(MovimentacaoSaidaDTO dto) {
        Estoque estoque = estoqueService.buscarEstoqueEntityId(dto.estoqueId());
        estoqueService.verificarEstoqueAtivo(estoque);
        Produto produto = produtoService.buscarProdutoId(dto.produtoId());
        produtoService.verificarProdutoAtivo(produto);
        estoqueService.verificarEstoqueProduto(estoque, produto);
        verificarQuantidadeTotalProduto(dto, produto);
        MovimentacaoEstoque movEstoque = new MovimentacaoEstoque();
        movEstoque.setDataHora(LocalDateTime.now());
        movEstoque.setTipoMov(TipoMovimentacao.SAIDA);
        movEstoque.setEstoque(estoque);
        List<ItemMovimentacaoLote> itens = listaDeItensMovimentacao(movEstoque, produto.getId(), dto.quantidade());
        movEstoque.setItensMovimentacao(itens);
        repository.save(movEstoque);
        return converter.movEstoqueEntityParaDto(movEstoque);
    }

    /**
     * Lista o histórico de movimentações de um estoque específico.
     * @param token Token JWT do usuário logado
     * @param estoqueId ID do estoque
     * @return Lista de movimentações do estoque em formato DTO
     * @throws ObjetoNaoEncontradoException se o estoque não existir
     * @throws ObjetoInativoException se o estoque estiver inativo
     * @throws AcaoNaoPermitidaException se o estoque não pertencer ao usuário
     */
    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponseDTO> listarHistoricoMovimentacoesEstoque(String token, Long estoqueId) {
        Usuario usuario = usuarioService.findByToken(token);
        Estoque estoque = estoqueService.buscarEstoqueEntityId(estoqueId);
        estoqueService.verificarEstoqueAtivo(estoque);
        estoqueService.verificarEstoqueUsuario(estoque, usuario);
        return repository.findByEstoqueId(estoque.getId()).stream().map(converter::movEstoqueEntityParaDto).toList();
    }

    /**
     * Lista o histórico de movimentações de um produto em um estoque específico.
     * @param token Token JWT do usuário logado
     * @param estoqueId ID do estoque
     * @param produtoId ID do produto
     * @return Lista de movimentações do produto no estoque em formato DTO
     * @throws ObjetoNaoEncontradoException se o estoque ou produto não existirem
     * @throws ObjetoInativoException se o estoque estiver inativo
     * @throws AcaoNaoPermitidaException se o usuário não tiver permissão sobre o produto/estoque
     */
    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponseDTO> listarHistoricoMovimentacoesProduto(String token, Long estoqueId, Long produtoId) {
        Usuario usuario = usuarioService.findByToken(token);
        Estoque estoque = estoqueService.buscarEstoqueEntityId(estoqueId);
        Produto produto = produtoService.buscarProdutoId(produtoId);
        produtoService.verificarPermissaoProdutoUsuario(usuario, produto);
        estoqueService.verificarEstoqueAtivo(estoque);
        estoqueService.verificarEstoqueUsuario(estoque, usuario);
        estoqueService.verificarEstoqueProduto(estoque, produto);
        return repository.listarHistoricoMovimentacoesProduto(produtoId, estoqueId).stream()
                .map(converter::movEstoqueEntityParaDto)
                .toList();
    }

    /**
     * Lista o histórico de movimentações de um estoque em um intervalo de datas.
     * @param token Token JWT do usuário logado
     * @param estoqueId ID do estoque
     * @param dataInicio Data inicial do intervalo
     * @param dataFinal Data final do intervalo
     * @return Lista de movimentações no período informado em formato DTO
     * @throws ObjetoNaoEncontradoException se o estoque não existir
     * @throws ObjetoInativoException se o estoque estiver inativo
     * @throws AcaoNaoPermitidaException se o estoque não pertencer ao usuário
     * @throws DadoIrregularException se a data inicial for posterior à final
     */
    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponseDTO> listarHistoricoMovimentacoesData(String token, Long estoqueId, LocalDate dataInicio, LocalDate dataFinal) {
        Usuario usuario = usuarioService.findByToken(token);
        Estoque estoque = estoqueService.buscarEstoqueEntityId(estoqueId);
        estoqueService.verificarEstoqueAtivo(estoque);
        estoqueService.verificarEstoqueUsuario(estoque, usuario);
        LocalDateTime dataHoraInicio = dataInicio.atStartOfDay();
        LocalDateTime dataHoraFinal = dataFinal.atTime(23, 59, 59);
        verificarDatas(dataHoraInicio, dataHoraFinal);
        return  repository.findByEstoqueIdAndDataHoraBetween(estoqueId, dataHoraInicio, dataHoraFinal)
                .stream()
                .map(converter::movEstoqueEntityParaDto)
                .toList();
    }

    /**
     * Lista o histórico de movimentações de um produto em um estoque específico,
     * dentro de um intervalo de datas.
     * @param token Token JWT do usuário logado
     * @param estoqueId ID do estoque
     * @param produtoId ID do produto
     * @param dataInicio Data inicial do intervalo
     * @param dataFinal Data final do intervalo
     * @return Lista de movimentações filtradas por produto e data em formato DTO
     * @throws ObjetoNaoEncontradoException se o estoque ou produto não existirem
     * @throws ObjetoInativoException se o estoque estiver inativo
     * @throws AcaoNaoPermitidaException se o usuário não tiver permissão sobre o produto/estoque
     * @throws DadoIrregularException se a data inicial for posterior à final
     */
    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueResponseDTO> listarHistoricoMovimentacoesProdutoEData(String token, Long estoqueId, Long produtoId, LocalDate dataInicio, LocalDate dataFinal) {
        Usuario usuario = usuarioService.findByToken(token);
        Estoque estoque = estoqueService.buscarEstoqueEntityId(estoqueId);
        Produto produto = produtoService.buscarProdutoId(produtoId);
        produtoService.verificarPermissaoProdutoUsuario(usuario, produto);
        estoqueService.verificarEstoqueAtivo(estoque);
        estoqueService.verificarEstoqueUsuario(estoque, usuario);
        estoqueService.verificarEstoqueProduto(estoque, produto);
        LocalDateTime dataHoraInicio = dataInicio.atStartOfDay();
        LocalDateTime dataHoraFinal = dataFinal.atTime(23, 59, 59);
        verificarDatas(dataHoraInicio, dataHoraFinal);
        return repository.listarHistoricoMovimentacoesProdutoEData(produtoId, estoqueId, dataHoraInicio, dataHoraFinal)
                .stream()
                .map(converter::movEstoqueEntityParaDto)
                .toList();
    }

    //ÚTEIS

    /**
     * Registra automaticamente uma movimentação de entrada quando um novo lote é criado.
     * @param event Evento de criação de lote
     */
    @EventListener
    public void registrarEntrada(LoteCriadoEvent event) {
        repository.save(converter.loteProdParaMovEstoque(event.loteProduto()));
    }

    /**
     * Verifica se a quantidade desejada para saída é menor ou igual
     * à quantidade total disponível do produto.
     * @param dto DTO da movimentação de saída
     * @param produto Produto a ser movimentado
     * @throws DadoIrregularException se a quantidade desejada for maior que a disponível
     */
    private void verificarQuantidadeTotalProduto(MovimentacaoSaidaDTO dto, Produto produto) {
        relatorioService.calcularQuantidadeTotalProduto(produto);
        if (dto.quantidade() > produto.getQuantidadeTotal())
            throw new DadoIrregularException("Você está tentando retirar mais unidades de produto do que existe no estoque.");
    }


    /**
     * Gera a lista de itens de movimentação a partir dos lotes disponíveis,
     * retirando a quantidade desejada conforme a ordem de validade.
     * @param movEstoque Movimentação associada
     * @param produtoId ID do produto a ser movimentado
     * @param quantidadeDesejada Quantidade total desejada para retirada
     * @return Lista de itens de movimentação de lote
     */
    private List<ItemMovimentacaoLote> listaDeItensMovimentacao(MovimentacaoEstoque movEstoque, Long produtoId, int quantidadeDesejada) {
        List<LoteProduto> lotes = loteProdutoService.buscarLoteProdutoPorDataValidade(produtoId);
        List<ItemMovimentacaoLote> itens = new ArrayList<>();
        Iterator<LoteProduto> iterator = lotes.iterator();
        while (quantidadeDesejada > 0 && iterator.hasNext()) {
            LoteProduto lote = iterator.next();
            int retirada = Math.min(lote.getQuantidadeLote(), quantidadeDesejada);
            lote.setQuantidadeLote(lote.getQuantidadeLote() - retirada);
            quantidadeDesejada -= retirada;
            itens.add(gerarItemMovimentacaoLote(movEstoque, lote, retirada));
        }
        loteProdutoService.salvarAlteracoes(
                itens.stream().map(ItemMovimentacaoLote::getLoteProduto).toList()
        );
        return itens;
    }

    /**
     * Cria um item de movimentação de lote representando a retirada de unidades de um lote.
     * @param movEstoque Movimentação associada
     * @param lt Lote do qual os itens foram retirados
     * @param retirada Quantidade retirada
     * @return Item de movimentação de lote gerado
     */
    private ItemMovimentacaoLote gerarItemMovimentacaoLote(MovimentacaoEstoque movEstoque, LoteProduto lt, int retirada) {
        return new ItemMovimentacaoLote(lt, movEstoque, retirada);
    }

    /**
     * Verifica se a data inicial é anterior ou igual à data final.
     * @param dataHoraInicio Data/hora inicial
     * @param dataHoraFinal Data/hora final
     * @throws DadoIrregularException se a data inicial for posterior à final
     */
    private void verificarDatas(LocalDateTime dataHoraInicio, LocalDateTime dataHoraFinal) {
        if(dataHoraInicio.isAfter(dataHoraFinal))
            throw new DadoIrregularException("A data de inicío deve ser anterior a data final.");
    }
}
