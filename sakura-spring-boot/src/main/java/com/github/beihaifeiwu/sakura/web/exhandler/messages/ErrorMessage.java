package com.github.beihaifeiwu.sakura.web.exhandler.messages;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@ToString(exclude = "trace")
@JsonInclude(Include.NON_EMPTY) //for Jackson 2.x
@XmlRootElement(name = "problem") //for JAXB
public class ErrorMessage implements Serializable {

    // The time that the errors were extracted
    private long timestamp;

    // The status code
    private Integer status;

    // The error reason
    private String error;

    // The class name of the root exception (if configured)
    private String exception;

    // The exception message
    private String message;

    // The exception stack trace
    private String trace;

    // The URL path when the exception was raised
    private String path;

    // Other messages for extension
    private Map<String, Object> other = new HashMap<>(4);

    public ErrorMessage(ErrorMessage orig) {
        this.timestamp = orig.timestamp;
        this.status = orig.status;
        this.error = orig.error;
        this.exception = orig.exception;
        this.message = orig.message;
        this.trace = orig.trace;
        this.status = orig.status;
        this.path = orig.path;
    }

    @JsonProperty
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonIgnore
    public void setStatus(HttpStatus status) {
        this.status = status.value();
    }

    @JsonAnyGetter
    public Map<String, Object> getOther() {
        return other;
    }

    @JsonAnySetter
    public void setOther(String name, Object value) {
        other.put(name, value);
    }
}
