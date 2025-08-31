package br.com.rafaelblomer.business.exceptions;

public class DadoIrregularException extends RuntimeException{
	private static final long serialVersionUID = 1212125294291918471L;

	public DadoIrregularException(String msg) {
        super(msg);
    }
}
