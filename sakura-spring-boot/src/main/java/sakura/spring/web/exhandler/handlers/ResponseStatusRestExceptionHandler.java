package sakura.spring.web.exhandler.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * Simple {@link RestExceptionHandler} that just returns response with the specified status code
 * and no content.
 */
public class ResponseStatusRestExceptionHandler implements RestExceptionHandler<Exception, Void> {

    private final HttpStatus status;


    public ResponseStatusRestExceptionHandler(HttpStatus status) {
        this.status = status;
    }

    public ResponseEntity<Void> handleException(Exception ex, HttpServletRequest request) {
        return new ResponseEntity<>(status);
    }
}
