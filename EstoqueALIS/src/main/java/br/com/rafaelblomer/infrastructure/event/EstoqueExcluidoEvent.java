package br.com.rafaelblomer.infrastructure.event;

import br.com.rafaelblomer.infrastructure.entities.Estoque;

public record EstoqueExcluidoEvent(Estoque estoque) {
}
