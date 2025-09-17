package br.com.rafaelblomer.services;

import br.com.rafaelblomer.business.*;
import br.com.rafaelblomer.business.converters.MovimentacaoEstoqueConverter;
import br.com.rafaelblomer.business.dtos.MovimentacaoEstoqueResponseDTO;
import br.com.rafaelblomer.business.dtos.MovimentacaoSaidaDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.infrastructure.entities.*;
import br.com.rafaelblomer.infrastructure.entities.enums.TipoMovimentacao;
import br.com.rafaelblomer.infrastructure.event.LoteCriadoEvent;
import br.com.rafaelblomer.infrastructure.repositories.MovimentacaoEstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

    @InjectMocks
    private MovimentacaoEstoqueService movimentacaoEstoqueService;

    @Mock
    private MovimentacaoEstoqueRepository repository;
    @Mock
    private MovimentacaoEstoqueConverter converter;
    @Mock
    private EstoqueService estoqueService;
    @Mock
    private ProdutoService produtoService;
    @Mock
    private RelatorioService relatorioService;
    @Mock
    private LoteProdutoService loteProdutoService;
    @Mock
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Estoque estoque;
    private Produto produto;
    private MovimentacaoSaidaDTO saidaDTO;
    private final String TOKEN = "fake-token";
    private final Long ESTOQUE_ID = 1L;
    private final Long USUARIO_ID = 1L;
    private final Long PRODUTO_ID = 1L;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(USUARIO_ID);

        estoque = new Estoque("Estoque Teste", usuario);
        estoque.setId(ESTOQUE_ID);
        estoque.setAtivo(true);

        produto = new Produto();
        produto.setId(PRODUTO_ID);
        produto.setEstoque(estoque);
        produto.setAtivo(true);
        produto.setQuantidadeTotal(100);

        saidaDTO = new MovimentacaoSaidaDTO(ESTOQUE_ID, PRODUTO_ID, 50);
    }

    @Test
    @DisplayName("Deve registrar uma saída com sucesso retirando de um único lote")
    void registrarSaida_ComQuantidadeSuficienteEmUmUnicoLote_RetornaDTO() {
        LoteProduto lote1 = new LoteProduto(LocalDate.now().plusMonths(6), "LOTE-001", produto, 80);
        List<LoteProduto> lotesDisponiveis = List.of(lote1);

        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(produtoService.buscarProdutoId(PRODUTO_ID)).thenReturn(produto);
        when(loteProdutoService.buscarLoteProdutoPorDataValidade(PRODUTO_ID)).thenReturn(lotesDisponiveis);
        when(converter.movEstoqueEntityParaDto(any(MovimentacaoEstoque.class)))
                .thenReturn(new MovimentacaoEstoqueResponseDTO(1L, LocalDateTime.now(), TipoMovimentacao.SAIDA, null));

        MovimentacaoEstoqueResponseDTO resultado = movimentacaoEstoqueService.registrarSaida(saidaDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.tipoMovimentacao()).isEqualTo(TipoMovimentacao.SAIDA);
        assertThat(lote1.getQuantidadeLote()).isEqualTo(30);

        ArgumentCaptor<MovimentacaoEstoque> movimentacaoCaptor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(repository).save(movimentacaoCaptor.capture());
        MovimentacaoEstoque movimentacaoSalva = movimentacaoCaptor.getValue();

        assertThat(movimentacaoSalva.getTipoMov()).isEqualTo(TipoMovimentacao.SAIDA);
        assertThat(movimentacaoSalva.getEstoque()).isEqualTo(estoque);
        assertThat(movimentacaoSalva.getItensMovimentacao()).hasSize(1);
        assertThat(movimentacaoSalva.getItensMovimentacao().getFirst().getQuantidade()).isEqualTo(50);

        verify(estoqueService).verificarEstoqueAtivo(estoque);
        verify(produtoService).verificarProdutoAtivo(produto);
        verify(estoqueService).verificarEstoqueProduto(estoque, produto);
        verify(relatorioService).calcularQuantidadeTotalProduto(produto);
        verify(loteProdutoService).salvarAlteracoes(anyList());
    }

    @Test
    @DisplayName("Deve registrar uma saída com sucesso retirando de múltiplos lotes")
    void registrarSaida_ComQuantidadeInsuficienteNoPrimeiroLote_RetiraDeMultiplosLotes() {
        LoteProduto lote1 = new LoteProduto(LocalDate.now().plusMonths(6), "LOTE-001", produto, 30);
        LoteProduto lote2 = new LoteProduto(LocalDate.now().plusMonths(7), "LOTE-002", produto, 40);
        List<LoteProduto> lotesDisponiveis = List.of(lote1, lote2);

        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(produtoService.buscarProdutoId(PRODUTO_ID)).thenReturn(produto);
        when(loteProdutoService.buscarLoteProdutoPorDataValidade(PRODUTO_ID)).thenReturn(lotesDisponiveis);
        when(converter.movEstoqueEntityParaDto(any(MovimentacaoEstoque.class)))
                .thenReturn(new MovimentacaoEstoqueResponseDTO(1L, LocalDateTime.now(), TipoMovimentacao.SAIDA, null));

        movimentacaoEstoqueService.registrarSaida(saidaDTO);

        assertThat(lote1.getQuantidadeLote()).isZero();
        assertThat(lote2.getQuantidadeLote()).isEqualTo(20);

        ArgumentCaptor<MovimentacaoEstoque> movimentacaoCaptor = ArgumentCaptor.forClass(MovimentacaoEstoque.class);
        verify(repository).save(movimentacaoCaptor.capture());
        MovimentacaoEstoque movimentacaoSalva = movimentacaoCaptor.getValue();

        assertThat(movimentacaoSalva.getItensMovimentacao()).hasSize(2);
        assertThat(movimentacaoSalva.getItensMovimentacao().get(0).getQuantidade()).isEqualTo(30);
        assertThat(movimentacaoSalva.getItensMovimentacao().get(1).getQuantidade()).isEqualTo(20);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar registrar saída com quantidade maior que o total em estoque")
    void registrarSaida_ComQuantidadeMaiorQueTotalEmEstoque_LancaDadoIrregularException() {
        produto.setQuantidadeTotal(40);
        saidaDTO = new MovimentacaoSaidaDTO(ESTOQUE_ID, PRODUTO_ID, 50);

        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(produtoService.buscarProdutoId(PRODUTO_ID)).thenReturn(produto);

        assertThatThrownBy(() -> movimentacaoEstoqueService.registrarSaida(saidaDTO))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("Você está tentando retirar mais unidades de produto do que existe no estoque.");

        verify(repository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve listar o histórico de movimentações de um estoque com sucesso")
    void listarHistoricoMovimentacoesEstoque_ComDadosValidos_RetornaListaDeDTOs() {
        List<MovimentacaoEstoque> movimentacoes = List.of(new MovimentacaoEstoque(), new MovimentacaoEstoque());
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(repository.findByEstoqueId(ESTOQUE_ID)).thenReturn(movimentacoes);
        when(converter.movEstoqueEntityParaDto(any(MovimentacaoEstoque.class)))
                .thenReturn(new MovimentacaoEstoqueResponseDTO(null, null, null, null));

        List<MovimentacaoEstoqueResponseDTO> resultado = movimentacaoEstoqueService.listarHistoricoMovimentacoesEstoque(TOKEN, ESTOQUE_ID);

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        verify(estoqueService).verificarEstoqueAtivo(estoque);
        verify(estoqueService).verificarEstoqueUsuario(estoque, usuario);
        verify(repository).findByEstoqueId(ESTOQUE_ID);
    }

    @Test
    @DisplayName("Deve listar o histórico por produto com sucesso")
    void listarHistoricoMovimentacoesProduto_ComDadosValidos_RetornaListaDeDTOs() {
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(produtoService.buscarProdutoId(PRODUTO_ID)).thenReturn(produto);
        when(repository.listarHistoricoMovimentacoesProduto(PRODUTO_ID, ESTOQUE_ID)).thenReturn(List.of(new MovimentacaoEstoque()));

        List<MovimentacaoEstoqueResponseDTO> resultado = movimentacaoEstoqueService.listarHistoricoMovimentacoesProduto(TOKEN, ESTOQUE_ID, PRODUTO_ID);

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        verify(produtoService).verificarPermissaoProdutoUsuario(usuario, produto);
        verify(estoqueService).verificarEstoqueProduto(estoque, produto);
    }

    @Test
    @DisplayName("Deve listar o histórico por data com sucesso")
    void listarHistoricoMovimentacoesData_ComDatasValidas_RetornaListaDeDTOs() {
        LocalDate dataInicio = LocalDate.of(2025, 1, 1);
        LocalDate dataFinal = LocalDate.of(2025, 1, 31);
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);
        when(repository.findByEstoqueIdAndDataHoraBetween(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new MovimentacaoEstoque()));

        List<MovimentacaoEstoqueResponseDTO> resultado = movimentacaoEstoqueService.listarHistoricoMovimentacoesData(TOKEN, ESTOQUE_ID, dataInicio, dataFinal);

        assertThat(resultado).isNotNull().hasSize(1);
        verify(repository).findByEstoqueIdAndDataHoraBetween(
                eq(ESTOQUE_ID),
                eq(dataInicio.atStartOfDay()),
                eq(dataFinal.atTime(23, 59, 59))
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao listar por data com data de início após a data final")
    void listarHistoricoMovimentacoesData_ComDataInicioAposDataFinal_LancaDadoIrregularException() {
        LocalDate dataInicio = LocalDate.of(2025, 2, 1);
        LocalDate dataFinal = LocalDate.of(2025, 1, 31);
        when(usuarioService.findByToken(TOKEN)).thenReturn(usuario);
        when(estoqueService.buscarEstoqueEntityId(ESTOQUE_ID)).thenReturn(estoque);

        assertThatThrownBy(() -> movimentacaoEstoqueService.listarHistoricoMovimentacoesData(TOKEN, ESTOQUE_ID, dataInicio, dataFinal))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("A data de inicío deve ser anterior a data final.");
    }

    @Test
    @DisplayName("Deve registrar uma entrada quando um evento LoteCriadoEvent for recebido")
    void registrarEntrada_QuandoRecebeLoteCriadoEvent_SalvaMovimentacao() {
        LoteProduto loteNovo = new LoteProduto(LocalDate.now(), "NOVO-LOTE", produto, 100);
        LoteCriadoEvent event = new LoteCriadoEvent(loteNovo);

        MovimentacaoEstoque movimentacaoDeEntrada = new MovimentacaoEstoque();
        movimentacaoDeEntrada.setTipoMov(TipoMovimentacao.ENTRADA);

        when(converter.loteProdParaMovEstoque(loteNovo)).thenReturn(movimentacaoDeEntrada);

        movimentacaoEstoqueService.registrarEntrada(event);

        verify(converter, times(1)).loteProdParaMovEstoque(loteNovo);
        verify(repository, times(1)).save(movimentacaoDeEntrada);
    }
}
