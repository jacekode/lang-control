package langcontrol.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GeneralExceptionHandlingControllerAdvice {

    @ExceptionHandler(AuthenticationNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleAuthenticationNotFoundException(AuthenticationNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AccessNotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponseBody handleAccessNotAllowedException(AccessNotAllowedException e, HttpServletRequest r) {
        return new ErrorResponseBody(HttpStatus.FORBIDDEN, r.getRequestURI(), e);
    }

    @ExceptionHandler(GeneralNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponseBody handleCustomNotFoundException(GeneralNotFoundException e, HttpServletRequest r) {
        return new ErrorResponseBody(HttpStatus.FORBIDDEN, r.getRequestURI(), e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleConstraintViolationException(ConstraintViolationException e) {
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleIllegalArgumentException(IllegalArgumentException e) {
        return e.getMessage();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleIllegalStateException(IllegalStateException e) {
        return e.getMessage();
    }

    @ExceptionHandler(GeneralBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleGeneralBadRequestException(GeneralBadRequestException e) {
        return e.getMessage();
    }

    @ExceptionHandler(InvalidDatabaseValueException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleInvalidDatabaseValueException(InvalidDatabaseValueException e) {
        return e.getMessage();
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponseBody handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex,
                                                                  HttpServletRequest req) {
        var body = new ErrorResponseBody(HttpStatus.CONFLICT, req.getRequestURI(), ex);
        return body;
    }
}
