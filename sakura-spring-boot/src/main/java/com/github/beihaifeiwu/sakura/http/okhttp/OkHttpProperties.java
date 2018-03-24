package com.github.beihaifeiwu.sakura.http.okhttp;

import com.github.beihaifeiwu.sakura.core.SakuraConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ClassUtils;

@Data
@ConfigurationProperties(prefix = SakuraConstants.OKHTTP_PROP_PREFIX)
public class OkHttpProperties {

    private long connectionTimeout = 30000L;
    private long readTimeout = 60000L;
    private long writeTimeout = 60000L;
    /**
     * The interval between web socket pings initiated by this client (The default value of 0 disables client-initiated pings).
     */
    private long pingInterval = 0L;
    /**
     * Whether to follow redirects from HTTPS to HTTP and from HTTP to HTTPS.
     */
    private boolean followSslRedirects = true;
    private boolean followRedirects = true;
    private boolean retryOnConnectionFailure = true;

    private Cache cache = new Cache();
    private Logging logging = new Logging();
    private Proxy proxy;

    @Data
    public static class Cache {
        private long size = 10485760;
        private String directory;
        private Mode mode = Mode.NONE;

        public enum Mode {
            NONE, TEMPORARY, PERSISTENT
        }
    }

    @Data
    public static class Logging {
        private Level level = Level.NONE;
        private String logName = ClassUtils.getPackageName(getClass());
        private org.slf4j.event.Level logLevel = org.slf4j.event.Level.DEBUG;

        public enum Level {
            NONE, BASIC, HEADERS, BODY
        }
    }

    @Data
    public static class Proxy {
        private java.net.Proxy.Type type;
        private String host;
        private Integer port;
    }

}