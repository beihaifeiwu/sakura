package sakura.spring.web.filter;

import lombok.Getter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * Created by liupin on 2017/7/13.
 */
@Getter
public class ShallowEtagHeaderExtFilter extends ShallowEtagHeaderFilter {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private String[] includePaths;
    private String[] excludePaths;

    public void setIncludePaths(String[] includePaths) {
        this.includePaths = ObjectUtils.isEmpty(includePaths) ? new String[0] : includePaths;
    }

    public void setExcludePaths(String[] excludePaths) {
        this.excludePaths = ObjectUtils.isEmpty(excludePaths) ? new String[0] : excludePaths;
    }

    @Override
    protected boolean isEligibleForEtag(HttpServletRequest request,
                                        HttpServletResponse response,
                                        int responseStatusCode, InputStream inputStream) {
        boolean eligibleForEtag = super.isEligibleForEtag(
                request, response, responseStatusCode, inputStream);
        if (eligibleForEtag) {
            String path = request.getRequestURI();
            for (String excludePath : excludePaths) {
                if (MATCHER.match(excludePath, path)) {
                    return false;
                }
            }
            for (String includePath : includePaths) {
                if (MATCHER.match(includePath, path)) {
                    return true;
                }
            }
        }
        return eligibleForEtag;
    }
}
