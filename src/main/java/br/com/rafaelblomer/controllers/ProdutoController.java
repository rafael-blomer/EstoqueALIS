package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.infrastructure.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProdutoController {

    @Autowired
    private ProdutoRepository repository;
}
