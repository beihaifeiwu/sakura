package sakura.common.http.request;

import lombok.Data;
import lombok.NonNull;

import java.util.Map;

/**
 * Created by haomu on 2018/4/20.
 */
@Data
public abstract class HttpRequest {

    protected final String url;
    protected final Map<String, String> params;
    protected final Map<String, String> headers;
    protected final Object tag;
    protected final int id;

    protected HttpRequest(@NonNull String url,
                          Map<String, String> params,
                          Map<String, String> headers, Object tag, int id) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.id = id;
    }



}
