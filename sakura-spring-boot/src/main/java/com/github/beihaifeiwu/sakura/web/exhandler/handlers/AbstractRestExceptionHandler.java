package com.github.beihaifeiwu.sakura.web.exhandler.handlers;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The base implementation of the {@link RestExceptionHandler} interface.
 */
public abstract class AbstractRestExceptionHandler<E extends Exception, T> implements RestExceptionHandler<E, T> {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    private final Class<E> exceptionClass;
    private final HttpStatus status;


    /**
     * This constructor determines the exception class from the generic class parameter {@code E}.
     *
     * @param status HTTP status
     */
    protected AbstractRestExceptionHandler(HttpStatus status) {
        this.exceptionClass = determineTargetType();
        this.status = status;
        LOG.trace("Determined generic exception type: {}", exceptionClass.getName());
    }

    protected AbstractRestExceptionHandler(Class<E> exceptionClass, HttpStatus status) {
        this.exceptionClass = exceptionClass;
        this.status = status;
    }


    ////// Abstract methods //////

    public abstract T createBody(E ex, HttpServletRequest req);


    ////// Template methods //////

    public ResponseEntity<T> handleException(E ex, HttpServletRequest req) {

        logException(ex, req);

        T body = createBody(ex, req);
        HttpHeaders headers = createHeaders(ex, req);

        return new ResponseEntity<>(body, headers, getStatus());
    }

    public Class<E> getExceptionClass() {
        return exceptionClass;
    }

    public HttpStatus getStatus() {
        return status;
    }


    protected HttpHeaders createHeaders(E ex, HttpServletRequest req) {
        return new HttpHeaders();
    }

    /**
     * Logs the exception; on ERROR level when status is 5xx, otherwise on INFO level without stack
     * trace, or DEBUG level with stack trace. The logger name is
     * {@code RestExceptionHandler}.
     *
     * @param ex  The exception to log.
     * @param req The current web request.
     */
    protected void logException(E ex, HttpServletRequest req) {
        Marker marker = MarkerFactory.getMarker(ex.getClass().getName());

        String uri = req.getRequestURI();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s %s\n", req.getMethod(), uri, getStatus()));

        if (req.getQueryString() != null) {
            uri += '?' + req.getQueryString();
        }
        sb.append("\nUri:\n").append("\t").append(uri).append("\n");
        sb.append("\nHeaders:\n");
        List<String> headerNames = Collections.list(req.getHeaderNames());
        headerNames.sort(Comparator.naturalOrder());
        for (String name : headerNames) {
            sb.append(String.format("\t%-20s:%s\n", name, req.getHeader(name)));
        }
        sb.append("\nStackTrace:\n");
        sb.append(Throwables.getStackTraceAsString(ex)).append("\n");

        LOG.error(marker, "{}", sb);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private Class<E> determineTargetType() {
        return (Class<E>) GenericTypeResolver.resolveTypeArguments(getClass(), AbstractRestExceptionHandler.class)[0];
    }
}
