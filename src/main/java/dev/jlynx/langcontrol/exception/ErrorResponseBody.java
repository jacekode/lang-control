package dev.jlynx.langcontrol.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.ZonedDateTime;

/**
 * Represents the API's response body for errors.
 */
@Getter @Setter
public class ErrorResponseBody {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss Z")
    private ZonedDateTime timestamp;
    private String requestURI;
    private String message;
    private String errorDescription;

    public ErrorResponseBody(HttpStatus status, String requestURI, Throwable ex) {
        this.status = status;
        this.timestamp = ZonedDateTime.now(Clock.systemUTC());
        this.requestURI = requestURI;
        this.message = ex.getMessage();
        this.errorDescription = "";
    }

    public ErrorResponseBody(HttpStatus status, String requestURI, Throwable ex, String errorDescription) {
        this.status = status;
        this.timestamp = ZonedDateTime.now(Clock.systemUTC());
        this.requestURI = requestURI;
        this.message = ex.getMessage();
        this.errorDescription = errorDescription;
    }
}
