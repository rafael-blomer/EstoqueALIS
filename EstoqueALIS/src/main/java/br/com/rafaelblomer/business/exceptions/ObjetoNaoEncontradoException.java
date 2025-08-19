package br.com.rafaelblomer.business.exceptions;

public class ObjetoNaoEncontradoException extends RuntimeException{

    public ObjetoNaoEncontradoException(String msg) {
        super(msg);
    }
}
