package br.fai.findcollectors.config;

import br.fai.findcollectors.dto.response.ExceptionResponse;
import br.fai.findcollectors.exceptions.BusinessRuleException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessRuleException(
            BusinessRuleException ex,
            HttpServletRequest request
    ) {

        ExceptionResponse response = new ExceptionResponse(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        ExceptionResponse response = new ExceptionResponse(
                ex.getClass().getSimpleName(),
                ex.getBindingResult().getFieldErrors().getFirst().getDefaultMessage(),
                request.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleRuntime(
            RuntimeException ex,
            HttpServletRequest request
    ) {

        ExceptionResponse response = new ExceptionResponse(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                request.getRequestURI(),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
