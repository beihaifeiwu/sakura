package sakura.spring.web;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import sakura.spring.core.SakuraConstants;

import java.util.Collections;
import java.util.List;

/**
 * Created by liupin on 2017/6/9.
 */
@Data
@ConfigurationProperties(prefix = SakuraConstants.WEB_PROP_PREFIX)
public class WebProperties {

    private Cors cors = new Cors();

    private ShallowEtag shallowEtag = new ShallowEtag();

    @Data
    public static class Cors {

        private boolean enabled = true;

        private List<String> allowedOrigins = Lists.newArrayList("*");

        private List<String> allowedMethods = Lists.newArrayList("*");

        private List<String> allowedHeaders = Lists.newArrayList("*");

        private List<String> exposedHeaders = Collections.emptyList();

        private Boolean allowCredentials = true;

        private Long maxAge;
    }

    @Data
    public static class ShallowEtag {

        private boolean enabled = false;

        private boolean writeWeakEtag = false;

        private String[] includePaths = new String[]{"/**"};

        private String[] excludePaths;
    }
}
