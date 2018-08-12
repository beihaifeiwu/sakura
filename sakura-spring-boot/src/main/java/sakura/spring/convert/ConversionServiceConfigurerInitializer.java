package sakura.spring.convert;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;
import sakura.common.$;
import sakura.spring.core.SpringBeans;

import java.util.List;

public class ConversionServiceConfigurerInitializer implements InitializingBean {

    @Autowired
    private ObjectProvider<List<ConversionService>> conversionServices;

    @Autowired
    private ObjectProvider<List<ConversionServiceConfigurer>> configurers;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ConversionService> serviceList = SpringBeans.getBeans(this.conversionServices);
        List<ConversionServiceConfigurer> configurerList = SpringBeans.getBeans(this.configurers);

        if (CollectionUtils.isEmpty(serviceList)
                || CollectionUtils.isEmpty(configurerList)) {
            return;
        }
        for (ConversionServiceConfigurer configurer : configurerList) {
            serviceList.forEach($.unchecked(configurer::configure));
        }
    }

}