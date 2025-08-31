package br.com.rafaelblomer.business.exceptions;

public class ObjetoNaoEncontradoException extends RuntimeException{
	private static final long serialVersionUID = 116537646207763451L;

	public ObjetoNaoEncontradoException(String msg) {
        super(msg);
    }
}
