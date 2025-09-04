package br.com.rafaelblomer.infrastructure.scheduling;

import br.com.rafaelblomer.business.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoteAlertaScheduler {

    @Autowired
    private RelatorioService relatorioService;

    @Scheduled(cron = "0 0 9 * * *")
    public void executarVerificacaoDiaria() {
        relatorioService.verificarLotesVencendo();
    }
}