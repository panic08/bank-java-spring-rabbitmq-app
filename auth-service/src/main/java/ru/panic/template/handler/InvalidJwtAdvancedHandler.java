package ru.panic.template.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.panic.template.exception.InvalidJwtException;

@RestControllerAdvice
public class InvalidJwtAdvancedHandler {
    @ResponseBody
    @ExceptionHandler(InvalidJwtException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    private String handleInvalidJwt(InvalidJwtException exception){
        return exception.getMessage();
    }
}
