package br.com.rafaelblomer.business;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;
import br.com.rafaelblomer.infrastructure.entities.Produto;
import br.com.rafaelblomer.infrastructure.repositories.LoteProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    LoteProdutoRepository loteProdutoRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    public void calcularQuantidadeTotalProduto(Produto produto) {
        produto.setQuantidadeTotal(loteProdutoRepository.findByProdutoId(produto.getId()).stream()
                .filter(l -> l.getQuantidadeLote() > 0)
                .mapToInt(LoteProduto::getQuantidadeLote)
                .sum());
    }

    public void verificarLotesVencendo() {
        int[] diasAviso = {20, 14, 7, 3};

        for (int dias : diasAviso) {
            LocalDate dataAlvo = LocalDate.now().plusDays(dias);
            List<LoteProduto> lotes = loteProdutoRepository.findLotesQueVencemEm(dataAlvo);

            if (!lotes.isEmpty()) {
                notificacaoService.enviarAvisoLotes(lotes, dias);
            }
        }
    }
}
