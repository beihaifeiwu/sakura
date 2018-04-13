package sakura.spring.web.exhandler.messages;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY) //for Jackson 2.x
@XmlRootElement(name = "problem") //for JAXB
public class ValidationErrorMessage extends ErrorMessage {

    private List<Error> errors = new ArrayList<>(6);

    public ValidationErrorMessage(ErrorMessage orig) {
        super(orig);
    }

    public ValidationErrorMessage addError(@Nullable String field,
                                           @Nullable Object rejectedValue,
                                           @Nullable String message) {
        errors.add(new Error(field, rejectedValue, message));
        return this;
    }

    public ValidationErrorMessage addError(@Nullable String message) {
        errors.add(new Error(null, null, message));
        return this;
    }


    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Error {
        private final String field;
        private final Object rejected;
        private final String message;
    }
}
