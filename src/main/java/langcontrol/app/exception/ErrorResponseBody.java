package langcontrol.app.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.ZonedDateTime;

@Getter @Setter
public class ErrorResponseBody {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss Z")
    private ZonedDateTime timestamp;
    private String requestURI;
    private String message;


    private ErrorResponseBody() {
        this.timestamp = ZonedDateTime.now(Clock.systemUTC());
    }

    public ErrorResponseBody(HttpStatus status) {
        this();
        this.status = status;
    }

    public ErrorResponseBody(HttpStatus status, String requestURI) {
        this(status);
        this.requestURI = requestURI;
    }

    public ErrorResponseBody(HttpStatus status,String requestURI, Throwable ex) {
        this(status, requestURI);
        this.message = ex.getLocalizedMessage();
    }
}
