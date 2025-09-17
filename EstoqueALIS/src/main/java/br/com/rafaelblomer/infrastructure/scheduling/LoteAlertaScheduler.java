package br.com.rafaelblomer.infrastructure.scheduling;

import br.com.rafaelblomer.business.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoteAlertaScheduler {

    @Autowired
    private RelatorioService relatorioService;

    //Nesse caso está verificando a cada 30 segundos, porém, em produção seria apenas uma vez no dia
    @Scheduled(fixedDelay = 30000)
    public void executarVerificacaoDiaria() {
        relatorioService.verificarLotesVencendo();
    }
}