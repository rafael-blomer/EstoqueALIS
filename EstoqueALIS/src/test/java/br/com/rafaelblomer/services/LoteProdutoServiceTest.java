package br.com.rafaelblomer.services;
import br.com.rafaelblomer.business.LoteProdutoService;
import br.com.rafaelblomer.business.ProdutoService;
import br.com.rafaelblomer.business.converters.LoteProdutoConverter;
import br.com.rafaelblomer.business.dtos.LoteProdutoCadastroDTO;
import br.com.rafaelblomer.business.dtos.LoteProdutoResponseDTO;
import br.com.rafaelblomer.business.exceptions.DadoIrregularException;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.event.LoteCriadoEvent;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteProdutoServiceTest {

    @InjectMocks
    private LoteProdutoService loteProdutoService;

    @Mock
    private LoteProdutoRepository repository;
    @Mock
    private ProdutoService produtoService;
    @Mock
    private ApplicationEventPublisher publisher;
    @Mock
    private LoteProdutoConverter converter;

    private Produto produto;
    private LoteProduto loteProduto;
    private LoteProdutoCadastroDTO cadastroDTO;
    private final Long PRODUTO_ID = 1L;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(PRODUTO_ID);
        produto.setAtivo(true);

        cadastroDTO = new LoteProdutoCadastroDTO(
                PRODUTO_ID,
                100,
                LocalDate.now().plusMonths(6),
                "FAB-001"
        );

        loteProduto = new LoteProduto();
        loteProduto.setId(1L);
        loteProduto.setProduto(produto);
        loteProduto.setQuantidadeLote(100);
    }

    @Test
    @DisplayName("Deve cadastrar um lote com sucesso e publicar um evento")
    void cadastrarLote_ComDadosValidos_SalvaLoteEPublicaEvento() {
        when(produtoService.buscarProdutoId(PRODUTO_ID)).thenReturn(produto);
        when(converter.dtoParaLoteProdutoEntity(cadastroDTO, produto)).thenReturn(loteProduto);
        when(repository.save(loteProduto)).thenReturn(loteProduto);
        when(converter.paraLoteProdutoDTO(loteProduto))
                .thenReturn(new LoteProdutoResponseDTO(1L, null, 100, null, "FAB-001"));

        LoteProdutoResponseDTO resultado = loteProdutoService.cadastrarLote(cadastroDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.loteFabricante()).isEqualTo("FAB-001");

        verify(produtoService, times(1)).buscarProdutoId(PRODUTO_ID);
        verify(produtoService, times(1)).verificarProdutoAtivo(produto);

        verify(repository, times(1)).save(loteProduto);

        ArgumentCaptor<LoteCriadoEvent> eventCaptor = ArgumentCaptor.forClass(LoteCriadoEvent.class);
        verify(publisher, times(1)).publishEvent(eventCaptor.capture());

        LoteCriadoEvent eventoPublicado = eventCaptor.getValue();
        assertThat(eventoPublicado.loteProduto()).isEqualTo(loteProduto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar lote com quantidade zero")
    void cadastrarLote_ComQuantidadeZero_LancaDadoIrregularException() {
        LoteProdutoCadastroDTO dtoInvalido = new LoteProdutoCadastroDTO(PRODUTO_ID, 0, LocalDate.now().plusDays(1), "FAB-002");

        assertThatThrownBy(() -> loteProdutoService.cadastrarLote(dtoInvalido))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("A quantidade total do lote tem que ser maior que 0.");

        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar lote com data de validade no passado")
    void cadastrarLote_ComDataDeValidadeNoPassado_LancaDadoIrregularException() {
        LoteProdutoCadastroDTO dtoInvalido = new LoteProdutoCadastroDTO(PRODUTO_ID, 100, LocalDate.now().minusDays(1), "FAB-003");

        assertThatThrownBy(() -> loteProdutoService.cadastrarLote(dtoInvalido))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("A data de validade tem que ser após a data atual");

        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar lote para um produto inativo")
    void cadastrarLote_ParaProdutoInativo_LancaExcecao() {
        when(produtoService.buscarProdutoId(PRODUTO_ID)).thenReturn(produto);
        doThrow(new DadoIrregularException("O produto foi desativado."))
                .when(produtoService).verificarProdutoAtivo(produto);

        assertThatThrownBy(() -> loteProdutoService.cadastrarLote(cadastroDTO))
                .isInstanceOf(DadoIrregularException.class)
                .hasMessage("O produto foi desativado.");

        verify(repository, never()).save(any());
        verify(publisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("Deve buscar lotes disponíveis ordenados por data de validade")
    void buscarLoteProdutoPorDataValidade_QuandoChamado_RetornaListaDoRepositorio() {
        List<LoteProduto> lotesEsperados = List.of(new LoteProduto(), new LoteProduto());
        when(repository.findLotesDisponiveisOrdenadosPorValidade(PRODUTO_ID)).thenReturn(lotesEsperados);

        List<LoteProduto> resultado = loteProdutoService.buscarLoteProdutoPorDataValidade(PRODUTO_ID);

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(2);
        assertThat(resultado).isEqualTo(lotesEsperados);
        verify(repository, times(1)).findLotesDisponiveisOrdenadosPorValidade(PRODUTO_ID);
    }

    @Test
    @DisplayName("Deve salvar uma lista de lotes alterados")
    void salvarAlteracoes_ComListaDeLotes_ChamaSaveAllDoRepositorio() {
        List<LoteProduto> lotesParaSalvar = List.of(new LoteProduto(), new LoteProduto());

        loteProdutoService.salvarAlteracoes(lotesParaSalvar);

        verify(repository, times(1)).saveAll(lotesParaSalvar);
    }
}
