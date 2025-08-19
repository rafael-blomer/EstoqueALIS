package br.com.rafaelblomer.business.exceptions;

public class UsuarioInativoException extends RuntimeException{

    public UsuarioInativoException(String msg) {
        super(msg);
    }
}
