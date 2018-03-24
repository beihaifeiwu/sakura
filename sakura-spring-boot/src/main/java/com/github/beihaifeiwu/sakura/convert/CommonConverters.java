package com.github.beihaifeiwu.sakura.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by liupin on 2017/6/14.
 */
public abstract class CommonConverters {

    public static Collection<Converter<?, ?>> getConvertersToRegister() {
        List<Converter<?, ?>> converters = new ArrayList<>();

        converters.add(StringToBytesConverter.INSTANCE);
        converters.add(BytesToStringConverter.INSTANCE);

        return converters;
    }

    public enum StringToBytesConverter implements Converter<String, byte[]> {

        INSTANCE;

        @Override
        public byte[] convert(@Nullable String source) {
            return source == null ? null : source.getBytes(StandardCharsets.UTF_8);
        }
    }

    public enum BytesToStringConverter implements Converter<byte[], String> {

        INSTANCE;

        @Override
        public String convert(@Nullable byte[] source) {
            return source == null ? null : (source.length == 0 ? "" : new String(source, StandardCharsets.UTF_8));
        }
    }
}
