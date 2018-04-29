package sakura.common.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.*;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by liupin on 2017/3/17.
 */
@UtilityClass
public class Jackson {

    private static volatile ObjectMapper objectMapper;

    public static synchronized void setObjectMapper(ObjectMapper objectMapper) {
        Jackson.objectMapper = objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            synchronized (Jackson.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
                    objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    objectMapper.configure(MapperFeature.USE_STATIC_TYPING, false);
                    objectMapper.configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, true);
                    try {
                        objectMapper.findAndRegisterModules();
                    } catch (Throwable ignore) {
                    }
                }
            }
        }
        return objectMapper;
    }

    public static TypeFactory getTypeFactory() {
        return getObjectMapper().getTypeFactory();
    }

    @SneakyThrows
    public static <T> T readObject(byte[] source, Class<T> type) {
        return getObjectMapper().readValue(source, type);
    }

    @SneakyThrows
    public static <T> T readObject(byte[] source, JavaType type) {
        return getObjectMapper().readValue(source, type);
    }

    @SneakyThrows
    public static <T> T readObject(String source, Class<T> type) {
        return getObjectMapper().readValue(source, type);
    }

    @SneakyThrows
    public static <T> T readObject(String source, JavaType type) {
        return getObjectMapper().readValue(source, type);
    }

    public static Map<String, Object> readObject(String source) {
        return readObject(source, mapType(String.class, Object.class));
    }

    @SneakyThrows
    public static <E> List<E> readArray(byte[] source, Class<E> elementType) {
        return getObjectMapper().readValue(source, listType(elementType));
    }

    @SneakyThrows
    public static <E> List<E> readArray(byte[] source, JavaType elementType) {
        return getObjectMapper().readValue(source, listType(elementType));
    }

    @SneakyThrows
    public static <E> List<E> readArray(String source, Class<E> elementType) {
        return getObjectMapper().readValue(source, listType(elementType));
    }

    @SneakyThrows
    public static <E> List<E> readArray(String source, JavaType elementType) {
        return getObjectMapper().readValue(source, listType(elementType));
    }

    @SneakyThrows
    public static String writeAsString(Object value) {
        return getObjectMapper().writeValueAsString(value);
    }

    @SneakyThrows
    public static byte[] writeAsBytes(Object value) {
        return getObjectMapper().writeValueAsBytes(value);
    }

    public static byte[] decodeBase64(String source) {
        return convertValue(source, byte[].class);
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        if (fromValue instanceof String) {
            return readObject((String) fromValue, toValueType);
        }
        return getObjectMapper().convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, JavaType toValueType) {
        return getObjectMapper().convertValue(fromValue, toValueType);
    }

    public static JavaType type(Type type) {
        return getTypeFactory().constructType(type);
    }

    public static JavaType type(TypeReference<?> typeRef) {
        return getTypeFactory().constructType(typeRef);
    }

    public static ArrayType arrayType(Class<?> elementType) {
        return getTypeFactory().constructArrayType(elementType);
    }

    public static ArrayType arrayType(JavaType elementType) {
        return getTypeFactory().constructArrayType(elementType);
    }

    public static CollectionType listType(Class<?> elementClass) {
        return collectionType(ArrayList.class, elementClass);
    }

    public static CollectionType listType(JavaType elementType) {
        return collectionType(ArrayList.class, elementType);
    }

    public static CollectionType collectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    public static CollectionType collectionType(Class<? extends Collection> collectionClass, JavaType elementType) {
        return getTypeFactory().constructCollectionType(collectionClass, elementType);
    }

    public static CollectionLikeType collectionLikeType(Class<?> collectionClass, Class<?> elementClass) {
        return getTypeFactory().constructCollectionLikeType(collectionClass, elementClass);
    }

    public static CollectionLikeType collectionLikeType(Class<?> collectionClass, JavaType elementType) {
        return getTypeFactory().constructCollectionLikeType(collectionClass, elementType);
    }

    public static MapType mapType(Class<?> keyClass, Class<?> valueClass) {
        return mapType(HashMap.class, keyClass, valueClass);
    }

    public static MapType mapType(JavaType keyType, JavaType valueType) {
        return mapType(HashMap.class, keyType, valueType);
    }

    public static MapType mapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    public static MapType mapType(Class<? extends Map> mapClass, JavaType keyType, JavaType valueType) {
        return getTypeFactory().constructMapType(mapClass, keyType, valueType);
    }

    public static MapLikeType mapLikeType(Class<?> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return getTypeFactory().constructMapLikeType(mapClass, keyClass, valueClass);
    }

    public static MapLikeType mapLikeType(Class<?> mapClass, JavaType keyType, JavaType valueType) {
        return getTypeFactory().constructMapLikeType(mapClass, keyType, valueType);
    }

}
