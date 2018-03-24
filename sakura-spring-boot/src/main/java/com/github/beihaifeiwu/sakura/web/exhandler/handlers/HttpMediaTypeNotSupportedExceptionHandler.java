package com.github.beihaifeiwu.sakura.web.exhandler.handlers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class HttpMediaTypeNotSupportedExceptionHandler
        extends ErrorMessageRestExceptionHandler<HttpMediaTypeNotSupportedException> {

    public HttpMediaTypeNotSupportedExceptionHandler() {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected HttpHeaders createHeaders(HttpMediaTypeNotSupportedException ex,
                                        HttpServletRequest req) {
        HttpHeaders headers = super.createHeaders(ex, req);
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();

        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
        }
        return headers;
    }
}
