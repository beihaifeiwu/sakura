package com.github.beihaifeiwu.sakura.convert;

import com.github.beihaifeiwu.sakura.core.SpringBeans;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.convert.JodaTimeConverters;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ThreeTenBackPortConverters;
import org.springframework.data.geo.format.DistanceFormatter;
import org.springframework.data.geo.format.PointFormatter;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * Created by liupin on 2017/7/4.
 */
public class DefaultConversionServiceConfigurer implements ConversionServiceConfigurer {

    private static final boolean IS_SPRING_DATA_PRESENT =
            ClassUtils.isPresent("org.springframework.data.repository.CrudRepository",
                    ClassUtils.getDefaultClassLoader());

    @Autowired
    private ObjectProvider<List<FormatterRegistrar>> formatterRegistrars;

    @Autowired
    private ObjectProvider<List<Converter<?, ?>>> converters;

    @Autowired
    private ObjectProvider<List<GenericConverter>> genericConverters;

    @Override
    public void configure(ConversionService conversionService) throws Exception {
        if (conversionService instanceof ConfigurableConversionService) {
            configureConverter((ConfigurableConversionService) conversionService);
        }
        if (conversionService instanceof FormattingConversionService) {
            configureFormatter((FormattingConversionService) conversionService);
        }
    }

    protected void configureConverter(ConfigurableConversionService ccs) {
        CommonConverters.getConvertersToRegister().forEach(ccs::addConverter);

        if (IS_SPRING_DATA_PRESENT) {
            Jsr310Converters.getConvertersToRegister().forEach(ccs::addConverter);
            JodaTimeConverters.getConvertersToRegister().forEach(ccs::addConverter);
            ThreeTenBackPortConverters.getConvertersToRegister().forEach(ccs::addConverter);
        }

        SpringBeans.getBeans(converters).forEach(ccs::addConverter);
        SpringBeans.getBeans(genericConverters).forEach(ccs::addConverter);
    }

    protected void configureFormatter(FormattingConversionService fcs) {
        if (IS_SPRING_DATA_PRESENT) {
            fcs.addFormatter(DistanceFormatter.INSTANCE);
            fcs.addFormatter(PointFormatter.INSTANCE);
        }

        SpringBeans.getBeans(formatterRegistrars).forEach(fr -> fr.registerFormatters(fcs));
    }
}
