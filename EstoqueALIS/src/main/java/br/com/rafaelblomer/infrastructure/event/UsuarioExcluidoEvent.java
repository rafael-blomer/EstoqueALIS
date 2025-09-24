package br.com.rafaelblomer.infrastructure.event;

import br.com.rafaelblomer.infrastructure.entities.Usuario;

public record UsuarioExcluidoEvent(Usuario usuario) {
}
