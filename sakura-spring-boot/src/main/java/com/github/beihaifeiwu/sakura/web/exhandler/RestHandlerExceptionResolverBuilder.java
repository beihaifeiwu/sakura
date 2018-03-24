package com.github.beihaifeiwu.sakura.web.exhandler;

import com.github.beihaifeiwu.sakura.core.MultipleReloadableResourceBundleMessageSource;
import com.github.beihaifeiwu.sakura.core.SakuraConstants;
import com.github.beihaifeiwu.sakura.web.exhandler.handlers.*;
import com.github.beihaifeiwu.sakura.web.exhandler.messages.DefaultMessagePopulator;
import com.github.beihaifeiwu.sakura.web.exhandler.messages.MessagePopulator;
import com.github.beihaifeiwu.sakura.web.exhandler.messages.MessagePopulatorAware;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.NONE;
import static org.springframework.util.StringUtils.hasText;

@Setter
@Accessors(fluent = true)
public class RestHandlerExceptionResolverBuilder {

    public static final String DEFAULT_MESSAGES_BASENAME = SakuraConstants.DEFAULT_EXCEPTION_MESSAGE_SOURCE;

    private final Map<Class<? extends Exception>, RestExceptionHandler> exceptionHandlers = new HashMap<>();

    @Setter(NONE) // to not conflict with overloaded setter
    private MediaType defaultContentType;

    /**
     * The {@link ContentNegotiationManager} to use to resolve acceptable media types.
     * If not provided, the default instance of {@code ContentNegotiationManager} with
     * {@link org.springframework.web.accept.HeaderContentNegotiationStrategy HeaderContentNegotiationStrategy}
     * and {@link org.springframework.web.accept.FixedContentNegotiationStrategy FixedContentNegotiationStrategy}
     * (with {@link #defaultContentType(MediaType) defaultContentType}) will be used.
     */
    private ContentNegotiationManager contentNegotiationManager;

    /**
     * The message body converters to use for converting an error message into HTTP response body.
     * getDefaultHttpMessageConverters()}).
     */
    private List<HttpMessageConverter<?>> httpMessageConverters;

    /**
     * The message source to set into all exception handlers implementing
     * {@link MessageSourceAware MessageSourceAware} interface, e.g.
     * {@link ErrorMessageRestExceptionHandler}. Required for built-in exception handlers.
     */
    private MessageSource messageSource;

    private MessagePopulator messagePopulator;

    /**
     * Whether to register default exception handlers for Spring exceptions. These are registered
     * <i>before</i> the provided exception handlers, so you can overwrite any of the default
     * mappings. Default is <tt>true</tt>.
     */
    private boolean withDefaultHandlers = true;

    /**
     * Whether to use the default (built-in) message source as a fallback to resolve messages that
     * the provided message source can't resolve. In other words, it sets the default message
     * source as a <i>parent</i> of the provided message source. Default is <tt>true</tt>.
     */
    private boolean withDefaultMessageSource = true;

    private ErrorProperties errorProperties = new ErrorProperties();


    public RestHandlerExceptionResolver build() {
        if (withDefaultMessageSource) {
            if (messageSource != null) {
                // set default message source as top parent
                HierarchicalMessageSource messages = resolveRootMessageSource(messageSource);
                if (messages != null) {
                    messages.setParentMessageSource(createMessageSource(DEFAULT_MESSAGES_BASENAME));
                }
            } else {
                messageSource = createMessageSource(DEFAULT_MESSAGES_BASENAME);
            }
        }

        if (withDefaultHandlers) {
            // add default handlers
            putAllIfAbsent(exceptionHandlers, getDefaultHandlers());
        }

        if (messagePopulator == null) {
            messagePopulator = new DefaultMessagePopulator(messageSource, errorProperties);
        }

        // initialize handlers
        for (RestExceptionHandler handler : exceptionHandlers.values()) {
            if (messageSource != null && handler instanceof MessageSourceAware) {
                ((MessageSourceAware) handler).setMessageSource(messageSource);
            }
            if (messagePopulator != null && handler instanceof MessagePopulatorAware) {
                ((MessagePopulatorAware) handler).setMessagePopulator(messagePopulator);
            }
        }

        RestHandlerExceptionResolver resolver = new RestHandlerExceptionResolver();
        resolver.setExceptionHandlers(exceptionHandlers);

        if (!CollectionUtils.isEmpty(httpMessageConverters)) {
            resolver.setMessageConverters(httpMessageConverters);
        }
        if (contentNegotiationManager != null) {
            resolver.setContentNegotiationManager(contentNegotiationManager);
        }
        if (defaultContentType != null) {
            resolver.setDefaultContentType(defaultContentType);
        }
        resolver.afterPropertiesSet();

        return resolver;
    }

    public RestHandlerExceptionResolverBuilder errorProperties(ErrorProperties errorProperties) {
        this.errorProperties = errorProperties;
        return this;
    }

    /**
     * The default content type that will be used as a fallback when the requested content type is
     * not supported.
     */
    public RestHandlerExceptionResolverBuilder defaultContentType(MediaType mediaType) {
        this.defaultContentType = mediaType;
        return this;
    }

    /**
     * The default content type that will be used as a fallback when the requested content type is
     * not supported.
     */
    public RestHandlerExceptionResolverBuilder defaultContentType(String mediaType) {
        defaultContentType(hasText(mediaType) ? MediaType.parseMediaType(mediaType) : null);
        return this;
    }

    /**
     * Registers the given exception handler for the specified exception type. This handler will be
     * also used for all the exception subtypes, when no more specific mapping is found.
     *
     * @param exceptionClass   The exception type handled by the given handler.
     * @param exceptionHandler An instance of the exception handler for the specified exception
     *                         type or its subtypes.
     */
    public <E extends Exception> RestHandlerExceptionResolverBuilder addHandler(
            Class<? extends E> exceptionClass, RestExceptionHandler<E, ?> exceptionHandler) {

        exceptionHandlers.put(exceptionClass, exceptionHandler);
        return this;
    }

    /**
     * Same as {@link #addHandler(Class, RestExceptionHandler)}, but the exception type is
     * determined from the handler.
     */
    public <E extends Exception> RestHandlerExceptionResolverBuilder addHandler(
            AbstractRestExceptionHandler<E, ?> exceptionHandler) {

        return addHandler(exceptionHandler.getExceptionClass(), exceptionHandler);
    }

    /**
     * Registers {@link ErrorMessageRestExceptionHandler} for the specified exception type.
     * This handler will be also used for all the exception subtypes, when no more specific mapping
     * is found.
     *
     * @param exceptionClass The exception type to handle.
     * @param status         The HTTP status to map the specified exception to.
     */
    public RestHandlerExceptionResolverBuilder addErrorMessageHandler(
            Class<? extends Exception> exceptionClass, HttpStatus status) {

        return addHandler(new ErrorMessageRestExceptionHandler<>(exceptionClass, status));
    }

    @Nullable
    private HierarchicalMessageSource resolveRootMessageSource(MessageSource messageSource) {

        if (messageSource instanceof HierarchicalMessageSource) {
            MessageSource parent = ((HierarchicalMessageSource) messageSource).getParentMessageSource();

            return parent != null ? resolveRootMessageSource(parent) : (HierarchicalMessageSource) messageSource;

        } else {
            return null;
        }
    }

    private Map<Class<? extends Exception>, RestExceptionHandler> getDefaultHandlers() {

        Map<Class<? extends Exception>, RestExceptionHandler> map = new HashMap<>();

        map.put(HttpRequestMethodNotSupportedException.class, new HttpRequestMethodNotSupportedExceptionHandler());
        map.put(HttpMediaTypeNotSupportedException.class, new HttpMediaTypeNotSupportedExceptionHandler());
        map.put(MethodArgumentNotValidException.class, new BindingExceptionHandler<>(MethodArgumentNotValidException::getBindingResult));
        map.put(BindException.class, new BindingExceptionHandler<>(BindException::getBindingResult));
        map.put(ConstraintViolationException.class, new ConstraintViolationExceptionHandler());
        map.put(Exception.class, new ErrorMessageRestExceptionHandler<>(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR));

        return map;
    }

    private MessageSource createMessageSource(String basename) {

        MultipleReloadableResourceBundleMessageSource messages = new MultipleReloadableResourceBundleMessageSource();
        messages.setBasename(basename);
        messages.setDefaultEncoding("UTF-8");
        messages.setFallbackToSystemLocale(false);

        return messages;
    }

    /**
     * Puts entries from the {@code source} map into the {@code target} map, but without overriding
     * any existing entry in {@code target} map, i.e. put only if the key does not exist in the
     * {@code target} map.
     *
     * @param target The target map where to put new entries.
     * @param source The source map from which read the entries.
     */
    private <K, V> void putAllIfAbsent(Map<K, V> target, Map<K, V> source) {

        for (Map.Entry<K, V> entry : source.entrySet()) {
            if (!target.containsKey(entry.getKey())) {
                target.put(entry.getKey(), entry.getValue());
            }
        }
    }
}
