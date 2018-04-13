package sakura.spring.web.exhandler.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import sakura.spring.web.exhandler.messages.ErrorMessage;
import sakura.spring.web.exhandler.messages.ValidationErrorMessage;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 * Created by liupin on 2017/6/30.
 */
public class BindingExceptionHandler<E extends Exception> extends ErrorMessageRestExceptionHandler<E> {

    private final Function<E, BindingResult> function;

    public BindingExceptionHandler(Function<E, BindingResult> function) {
        super(HttpStatus.BAD_REQUEST);
        this.function = function;
    }

    @Override
    public ErrorMessage createBody(E ex, HttpServletRequest req) {
        ErrorMessage tmpl = super.createBody(ex, req);
        ValidationErrorMessage msg = new ValidationErrorMessage(tmpl);

        BindingResult bindingResult = function.apply(ex);
        addBindError(bindingResult, msg);

        return msg;
    }

    private void addBindError(BindingResult bindingResult, ValidationErrorMessage msg) {
        for (ObjectError err : bindingResult.getGlobalErrors()) {
            msg.addError(err.getDefaultMessage());
        }
        for (FieldError err : bindingResult.getFieldErrors()) {
            msg.addError(err.getField(), err.getRejectedValue(), err.getDefaultMessage());
        }
    }
}
