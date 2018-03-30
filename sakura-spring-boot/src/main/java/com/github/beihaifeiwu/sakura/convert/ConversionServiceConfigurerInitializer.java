package com.github.beihaifeiwu.sakura.convert;

import com.github.beihaifeiwu.sakura.common.lang.EX;
import com.github.beihaifeiwu.sakura.core.SpringBeans;
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
        List<ConversionService> serviceList = SpringBeans.getBeans(this.conversionServices);
        List<ConversionServiceConfigurer> configurerList = SpringBeans.getBeans(this.configurers);

        if (CollectionUtils.isEmpty(serviceList)
                || CollectionUtils.isEmpty(configurerList)) {
            return;
        }
        for (ConversionServiceConfigurer configurer : configurerList) {
            serviceList.forEach(EX.unchecked(configurer::configure));
        }
    }

}