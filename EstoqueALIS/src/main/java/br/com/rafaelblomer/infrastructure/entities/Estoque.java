package br.com.rafaelblomer.infrastructure.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean ativo;

    @ManyToOne
    @NotBlank
    private Usuario dono;

    public Estoque(Usuario dono) {
        this.dono = dono;
        this.ativo = true;
    }

    public Estoque() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public @NotBlank Usuario getDono() {
        return dono;
    }

    public void setDono(@NotBlank Usuario dono) {
        this.dono = dono;
    }
}
