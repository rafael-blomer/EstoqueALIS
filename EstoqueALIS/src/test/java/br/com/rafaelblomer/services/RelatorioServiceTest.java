package br.com.rafaelblomer.services;

import br.com.rafaelblomer.business.NotificacaoService;
import br.com.rafaelblomer.business.RelatorioService;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class RelatorioServiceTest {

    @InjectMocks
    private RelatorioService relatorioService;

    @Mock
    private LoteProdutoRepository loteProdutoRepository;

    @Mock
    private NotificacaoService notificacaoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Dipirona");
        produto.setMarca("Genérico");
        produto.setDescricao("Analgésico");
        produto.setAtivo(true);
    }

    @Test
    void deveCalcularQuantidadeTotalProduto_ComLotesValidos() {
        LoteProduto lote1 = new LoteProduto();
        lote1.setQuantidadeLote(10);
        lote1.setProduto(produto);

        LoteProduto lote2 = new LoteProduto();
        lote2.setQuantidadeLote(5);
        lote2.setProduto(produto);

        when(loteProdutoRepository.findByProdutoId(produto.getId()))
                .thenReturn(Arrays.asList(lote1, lote2));

        relatorioService.calcularQuantidadeTotalProduto(produto);

        assertEquals(15, produto.getQuantidadeTotal());
    }

    @Test
    void deveCalcularQuantidadeTotalProduto_IgnorandoLotesComZero() {
        LoteProduto lote1 = new LoteProduto();
        lote1.setQuantidadeLote(0);
        lote1.setProduto(produto);

        LoteProduto lote2 = new LoteProduto();
        lote2.setQuantidadeLote(20);
        lote2.setProduto(produto);

        when(loteProdutoRepository.findByProdutoId(produto.getId()))
                .thenReturn(Arrays.asList(lote1, lote2));

        relatorioService.calcularQuantidadeTotalProduto(produto);

        assertEquals(20, produto.getQuantidadeTotal());
    }

    @Test
    void deveRetornarZeroQuandoNaoExistemLotes() {
        when(loteProdutoRepository.findByProdutoId(produto.getId()))
                .thenReturn(Collections.emptyList());

        relatorioService.calcularQuantidadeTotalProduto(produto);

        assertEquals(0, produto.getQuantidadeTotal());
    }

    @Test
    void deveChamarNotificacaoQuandoExistemLotesVencendo() {
        int dias = 7;
        LocalDate dataAlvo = LocalDate.now().plusDays(dias);

        LoteProduto lote = new LoteProduto();
        lote.setId(1L);
        lote.setProduto(produto);
        lote.setQuantidadeLote(5);
        lote.setDataValidade(dataAlvo);

        when(loteProdutoRepository.findLotesQueVencemEm(dataAlvo))
                .thenReturn(List.of(lote));

        relatorioService.verificarLotesVencendo();

        verify(notificacaoService, atLeastOnce()).enviarAvisoLotes(anyList(), anyInt());
    }

    @Test
    void naoDeveChamarNotificacaoQuandoNaoExistemLotesVencendo() {
        int dias = 14;
        LocalDate dataAlvo = LocalDate.now().plusDays(dias);

        when(loteProdutoRepository.findLotesQueVencemEm(dataAlvo))
                .thenReturn(Collections.emptyList());

        relatorioService.verificarLotesVencendo();

        verify(notificacaoService, never()).enviarAvisoLotes(anyList(), eq(dias));
    }
}

