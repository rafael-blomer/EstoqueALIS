package br.com.rafaelblomer.controllers.exceptions;

import br.com.rafaelblomer.business.exceptions.EntidadeNaoEncontrada;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontrada.class)
    public ResponseEntity<StandardError> entidadeNaoEncontrada(EntidadeNaoEncontrada e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Entidade não encontrada.", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }
}
