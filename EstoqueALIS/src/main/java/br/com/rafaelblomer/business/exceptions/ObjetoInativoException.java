package br.com.rafaelblomer.business.exceptions;

public class ObjetoInativoException extends RuntimeException{
	private static final long serialVersionUID = 6218642067992977991L;

	public ObjetoInativoException(String msg) {
        super(msg);
    }
}
