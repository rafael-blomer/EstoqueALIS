package br.com.rafaelblomer.controllers;

import br.com.rafaelblomer.business.MovimentacaoEstoqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovimentacaoEstoqueController {

    @Autowired
    private MovimentacaoEstoqueService service;
}
