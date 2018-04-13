package sakura.spring.template;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.List;

/**
 * Created by liupin on 2017/5/9.
 */
@Configuration
public class TemplateConfiguration {

    @Bean
    public TemplateService templateService(ObjectProvider<List<TemplateRender>> provider) {
        List<TemplateRender> renders = provider.getIfAvailable();
        if (renders != null) {
            AnnotationAwareOrderComparator.sort(renders);
        } else {
            renders = Lists.newArrayList();
        }
        return new TemplateServiceImpl(renders);
    }

}
