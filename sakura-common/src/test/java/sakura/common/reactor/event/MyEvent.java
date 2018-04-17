package sakura.common.reactor.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyEvent {
    private Date timestamp;
    private String message;

    @Override
    public String toString() {
        return "MyEvent{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }
}