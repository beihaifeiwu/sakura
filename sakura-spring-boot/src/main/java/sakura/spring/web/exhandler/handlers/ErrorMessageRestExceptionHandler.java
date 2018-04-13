package sakura.spring.web.exhandler.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import sakura.spring.web.exhandler.messages.ErrorMessage;
import sakura.spring.web.exhandler.messages.MessagePopulator;
import sakura.spring.web.exhandler.messages.MessagePopulatorAware;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link RestExceptionHandler} that produces {@link ErrorMessage}.
 *
 * @param <E> Type of the handled exception.
 */
@Slf4j
public class ErrorMessageRestExceptionHandler<E extends Exception>
        extends AbstractRestExceptionHandler<E, ErrorMessage> implements MessagePopulatorAware {

    protected MessagePopulator messagePopulator;

    /**
     * @param exceptionClass Type of the handled exceptions; it's used as a prefix of key to
     *                       resolve messages (via MessageSource).
     * @param status         HTTP status that will be sent to client.
     */
    public ErrorMessageRestExceptionHandler(Class<E> exceptionClass, HttpStatus status) {
        super(exceptionClass, status);
    }

    /**
     * @see AbstractRestExceptionHandler#AbstractRestExceptionHandler(HttpStatus) AbstractRestExceptionHandler
     */
    protected ErrorMessageRestExceptionHandler(HttpStatus status) {
        super(status);
    }

    @Override
    public ResponseEntity<ErrorMessage> handleException(E ex, HttpServletRequest req) {
        logException(ex, req);

        ErrorMessage body = createBody(ex, req);
        HttpHeaders headers = createHeaders(ex, req);

        return new ResponseEntity<>(body, headers, HttpStatus.valueOf(body.getStatus()));
    }

    public ErrorMessage createBody(E ex, HttpServletRequest req) {
        return messagePopulator.createAndPopulate(ex, req, getStatus());
    }

    @Override
    public void setMessagePopulator(MessagePopulator messagePopulator) {
        Assert.notNull(messagePopulator, "MessagePopulator cannot be null");
        this.messagePopulator = messagePopulator;
    }
}
