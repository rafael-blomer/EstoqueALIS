package br.com.rafaelblomer.business;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço responsável por gerar relatórios relacionados a produtos e lotes.
 * Funcionalidades principais:
 * - Calcular a quantidade total disponível de um produto somando seus lotes.
 * - Verificar lotes próximos da data de validade e disparar notificações.
 */
@Service
public class RelatorioService {

    @Autowired
    LoteProdutoRepository loteProdutoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Calcula a quantidade total de um produto somando todos os lotes associados.
     * @param produto Entidade Produto que terá sua quantidade total atualizada
     * Fluxo:
     * - Busca todos os lotes do produto pelo ID
     * - Filtra apenas os lotes com quantidade maior que zero
     * - Soma as quantidades restantes
     * - Atualiza o campo quantidadeTotal do produto
     */
    public void calcularQuantidadeTotalProduto(Produto produto) {
        produto.setQuantidadeTotal(loteProdutoRepository.findByProdutoId(produto.getId()).stream()
                .filter(l -> l.getQuantidadeLote() > 0)
                .mapToInt(LoteProduto::getQuantidadeLote)
                .sum());
    }

    /**
     * Verifica lotes que estão próximos de vencer em intervalos pré-definidos
     * (30, 20, 14, 7 e 3 dias antes do vencimento).
     * Para cada intervalo:
     * - Calcula a data alvo (data atual + dias)
     * - Busca lotes que vencem exatamente nessa data
     * - Filtra lotes com quantidade maior que zero
     * - Se houver lotes encontrados, dispara notificação via Telegram através do NotificacaoService.
     */
    public void verificarLotesVencendo() {
        int[] diasAviso = {30, 20, 14, 7, 3};

        for (int dias : diasAviso) {
            LocalDate dataAlvo = LocalDate.now().plusDays(dias);
            List<LoteProduto> lotes = loteProdutoRepository.findLotesQueVencemEm(dataAlvo)
                    .stream().filter(lt -> lt.getQuantidadeLote() > 0).toList();
            if (!lotes.isEmpty())
                notificacaoService.enviarAvisoLotes(lotes, dias);
        }
    }
}
