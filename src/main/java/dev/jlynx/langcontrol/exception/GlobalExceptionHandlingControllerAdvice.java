package dev.jlynx.langcontrol.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandlingControllerAdvice {

    @ExceptionHandler(AuthenticationNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseBody handleAuthenticationNotFoundException(AuthenticationNotFoundException e,
                                                                   HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.UNAUTHORIZED, req.getRequestURI(), e,
                "Authentication not found");
    }

    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseBody handleAccessForbiddenException(AccessForbiddenException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.FORBIDDEN, req.getRequestURI(), e);
    }

    @ExceptionHandler(AssetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseBody handleAssetNotFoundException(AssetNotFoundException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.NOT_FOUND, req.getRequestURI(), e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                   HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleConstraintViolationException(ConstraintViolationException e,
                                                                HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleIllegalArgumentException(IllegalArgumentException e,
                                                            HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleIllegalStateException(IllegalStateException e,
                                                         HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(GeneralBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleGeneralBadRequestException(GeneralBadRequestException e, HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), e);
    }

    @ExceptionHandler(InvalidDatabaseValueException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInvalidDatabaseValueException(InvalidDatabaseValueException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseBody> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex,
                                                                                  HttpServletRequest req) {
        ErrorResponseBody body = new ErrorResponseBody(HttpStatus.CONFLICT, req.getRequestURI(), ex);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DeckCreationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseBody handleDeckCreationException(DeckCreationException ex,
                                                         HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), ex);
    }

    @ExceptionHandler(OpenaiClientResponseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseBody handleOpenaiClientResponseException(OpenaiClientResponseException ex,
                                                                 HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), ex,
                "OpenAI API WebClient responded with error status code");
    }

    @ExceptionHandler(DeeplClientResponseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseBody handleDeeplClientResponseException(DeeplClientResponseException ex,
                                                                HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), ex,
                "Deepl API responded with error status code");
    }

    @ExceptionHandler(WrongPasswordException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseBody handleWrongPasswordException(WrongPasswordException ex,
                                                          HttpServletRequest req) {
        return new ErrorResponseBody(HttpStatus.BAD_REQUEST, req.getRequestURI(), ex,
                "Invalid credentials have been provided.");

    }
}
