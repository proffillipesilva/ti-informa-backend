package br.com.tiinforma.backend.exceptions.handler;

import br.com.tiinforma.backend.exceptions.ExceptionResponse;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Date;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                status,
                exception.getMessage(),
                Date.from(Instant.now())
        );
        return new ResponseEntity<>(exceptionResponse, status);
    }



    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handlerException(Exception exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                status,
                exception.getMessage(),
                Date.from(Instant.now())
        );
        return new ResponseEntity<>(exceptionResponse, status);
    }
}
