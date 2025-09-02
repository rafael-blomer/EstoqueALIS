package br.com.rafaelblomer.infrastructure.event;

import br.com.rafaelblomer.infrastructure.entities.LoteProduto;

public class LoteCriadoEvent {

    private final LoteProduto loteProduto;

    public LoteCriadoEvent(LoteProduto loteProduto) {
        this.loteProduto = loteProduto;
    }

    public LoteProduto getLoteProduto() {
        return loteProduto;
    }
}
