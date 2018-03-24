package com.github.beihaifeiwu.sakura.convert;

import com.github.beihaifeiwu.sakura.core.SpringBeans;
import com.github.beihaifeiwu.sakura.common.lang.EX;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ConversionServiceConfigurerInitializer implements InitializingBean {

    @Autowired
    private ObjectProvider<List<ConversionService>> conversionServices;

    @Autowired
    private ObjectProvider<List<ConversionServiceConfigurer>> configurers;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<ConversionService> conversionServices = SpringBeans.getBeans(this.conversionServices);
        List<ConversionServiceConfigurer> configurers = SpringBeans.getBeans(this.configurers);

        if (CollectionUtils.isEmpty(conversionServices)
                || CollectionUtils.isEmpty(configurers)) {
            return;
        }
        for (ConversionServiceConfigurer configurer : configurers) {
            conversionServices.forEach(EX.unchecked(configurer::configure));
        }
    }

}