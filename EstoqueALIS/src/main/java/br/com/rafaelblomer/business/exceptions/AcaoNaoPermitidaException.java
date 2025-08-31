package br.com.rafaelblomer.business.exceptions;

public class AcaoNaoPermitidaException extends RuntimeException {
	private static final long serialVersionUID = -5025806424832555213L;

	public AcaoNaoPermitidaException (String msg) {
        super(msg);
    }
}
