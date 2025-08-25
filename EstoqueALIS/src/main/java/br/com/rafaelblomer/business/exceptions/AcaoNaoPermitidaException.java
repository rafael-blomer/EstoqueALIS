package br.com.rafaelblomer.business.exceptions;

public class AcaoNaoPermitidaException extends RuntimeException {

    public AcaoNaoPermitidaException (String msg) {
        super(msg);
    }
}
