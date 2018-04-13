package sakura.spring.template.velocity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import sakura.spring.core.SakuraConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ConfigurationProperties} for configuring Velocity.
 *
 * @author Andy Wilkinson
 * @since 1.1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = SakuraConstants.VELOCITY_PROP_PREFIX)
public class VelocityProperties extends AbstractTemplateViewResolverProperties {

    public static final String DEFAULT_RESOURCE_LOADER_PATH = "classpath:/templates/";

    public static final String DEFAULT_PREFIX = "";

    public static final String DEFAULT_SUFFIX = ".vm";

    /**
     * Name of the DateTool helper object to expose in the Velocity context of the view.
     */
    private String dateToolAttribute;

    /**
     * Name of the NumberTool helper object to expose in the Velocity context of the view.
     */
    private String numberToolAttribute;

    /**
     * Additional velocity properties.
     */
    private Map<String, String> properties = new HashMap<String, String>();

    /**
     * Template path.
     */
    private String resourceLoaderPath = DEFAULT_RESOURCE_LOADER_PATH;

    /**
     * Velocity Toolbox config location, for example "/WEB-INF/toolbox.xml". Automatically
     * loads a Velocity Tools toolbox definition file and expose all defined tools in the
     * specified scopes.
     */
    private String toolboxConfigLocation;

    /**
     * Prefer file system access for template loading. File system access enables hot
     * detection of template changes.
     */
    private boolean preferFileSystemAccess = true;

    public VelocityProperties() {
        super(DEFAULT_PREFIX, DEFAULT_SUFFIX);
    }

}