package com.github.beihaifeiwu.sakura.web.exhandler.handlers;

import com.github.beihaifeiwu.sakura.web.exhandler.messages.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestMethodNotSupportedExceptionHandler
        extends ErrorMessageRestExceptionHandler<HttpRequestMethodNotSupportedException> {

    private static final Logger LOG = LoggerFactory.getLogger(DispatcherServlet.PAGE_NOT_FOUND_LOG_CATEGORY);

    public HttpRequestMethodNotSupportedExceptionHandler() {
        super(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    public ResponseEntity<ErrorMessage> handleException(HttpRequestMethodNotSupportedException ex,
                                                        HttpServletRequest req) {
        LOG.warn(ex.getMessage());

        return super.handleException(ex, req);
    }

    @Override
    protected HttpHeaders createHeaders(HttpRequestMethodNotSupportedException ex,
                                        HttpServletRequest req) {
        HttpHeaders headers = super.createHeaders(ex, req);

        if (!ObjectUtils.isEmpty(ex.getSupportedMethods())) {
            headers.setAllow(ex.getSupportedHttpMethods());
        }

        return headers;
    }
}
