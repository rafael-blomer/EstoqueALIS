package br.com.rafaelblomer.services;

import br.com.rafaelblomer.business.NotificacaoService;
import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.entities.Estoque;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @InjectMocks
    private NotificacaoService notificacaoService;

    @Test
    void naoDeveEnviarMensagemSeListaForVazia() {
        assertDoesNotThrow(() -> notificacaoService.enviarAvisoLotes(List.of(), 7));
    }

    @Test
    void deveEscaparCaracteresMarkdown() {
        String texto = "Produto *novo* (teste)!";
        String resultado = notificacaoService.escaparMarkdownV2(texto);

        assertTrue(resultado.contains("\\*novo\\*"));
        assertTrue(resultado.contains("\\("));
        assertTrue(resultado.contains("\\)"));
        assertTrue(resultado.contains("\\!"));
    }

    @Test
    void deveEnviarMensagemComLote() {
        Estoque estoque = new Estoque();
        estoque.setId(1L);
        estoque.setNomeEstoque("Estoque Principal");

        Produto produto = new Produto();
        produto.setNome("Baton branco");
        produto.setMarca("Garoto");
        produto.setEstoque(estoque);

        LoteProduto lote = new LoteProduto();
        lote.setProduto(produto);
        lote.setLoteFabricante("L123");
        lote.setQuantidadeLote(10);
        lote.setDataValidade(LocalDate.now().plusDays(7));

        assertDoesNotThrow(() -> notificacaoService.enviarAvisoLotes(List.of(lote), 7));
    }
}
