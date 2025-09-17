package br.com.rafaelblomer.controllers.exceptions;

import br.com.rafaelblomer.business.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ObjetoNaoEncontradoException.class)
    public ResponseEntity<StandardError> notFoundException(ObjetoNaoEncontradoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Objeto não encontrado.", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ObjetoInativoException.class)
    public ResponseEntity<StandardError> inactiveUserException(ObjetoInativoException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Objeto foi desativado.", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(AcaoNaoPermitidaException.class)
    public ResponseEntity<StandardError> notPermissionException(AcaoNaoPermitidaException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Usuário não tem permissão para realizar essa ação.", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DadoIrregularException.class)
    public ResponseEntity<StandardError> dataConflictException(DadoIrregularException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Dado irregular passado.", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(VerficacaoEmailException.class)
    public ResponseEntity<StandardError> emailException(VerficacaoEmailException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Houve um erro ao verificar seu email.", e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> pegarValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String mensagens = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Erro de validação");
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Erro de validação nos campos.", mensagens, request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> pegarDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        String mensagem = "Violação de integridade de dados.";
        String causeMessage = e.getMostSpecificCause().getMessage().toLowerCase();
        if (causeMessage.contains("cnpj"))
            mensagem = "Já existe um usuário com esse CNPJ.";
        else if (causeMessage.contains("email"))
            mensagem = "Já existe um usuário com esse e-mail.";
        else if (causeMessage.contains("telefone"))
            mensagem = "Já existe um usuário com esse telefone.";
        StandardError err = new StandardError(System.currentTimeMillis(), status.value(), "Erro de integridade de dados.", mensagem, request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
