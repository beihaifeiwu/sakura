package com.github.beihaifeiwu.sakura.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static java.lang.reflect.Modifier.*;
import static org.springframework.util.ReflectionUtils.USER_DECLARED_METHODS;

@Slf4j
public class AnnotationFilter implements ReflectionUtils.MethodFilter, ReflectionUtils.FieldFilter {

    public static final int FIELDS =
            Modifier.PUBLIC | Modifier.PROTECTED | PRIVATE |
                    STATIC | FINAL | Modifier.TRANSIENT |
                    Modifier.VOLATILE;

    public static final int METHODS =
            Modifier.PUBLIC | Modifier.PROTECTED | PRIVATE |
                    ABSTRACT | STATIC | FINAL |
                    Modifier.SYNCHRONIZED | Modifier.NATIVE | Modifier.STRICT |
                    Modifier.TRANSIENT; // TRANSIENT flag is the same as the VARARGS flag

    public static final int INJECTABLE_FIELDS = FIELDS ^ (FINAL | STATIC);
    public static final int INSTANCE_FIELDS = FIELDS ^ STATIC;
    public static final int INSTANCE_METHODS = METHODS ^ (ABSTRACT | STATIC);
    public static final int PROXYABLE_METHODS = METHODS ^ (ABSTRACT | FINAL | PRIVATE | STATIC);

    private final Class<? extends Annotation> clazz;
    private final int methodModifiers;
    private final int fieldModifiers;

    public AnnotationFilter(final Class<? extends Annotation> clazz) {
        this(clazz, METHODS, FIELDS);
    }

    public AnnotationFilter(final Class<? extends Annotation> clazz, final int modifiers) {
        this(clazz, modifiers, modifiers);
    }

    public AnnotationFilter(final Class<? extends Annotation> clazz, final int methodModifiers, final int fieldModifiers) {
        this.clazz = clazz;
        this.methodModifiers = methodModifiers & METHODS;
        this.fieldModifiers = fieldModifiers & FIELDS;
    }

    @Override
    public boolean matches(Method method) {
        if (USER_DECLARED_METHODS.matches(method) && method.isAnnotationPresent(clazz)) {
            if (checkModifiers(method, methodModifiers)) {
                return true;
            } else {
                log.warn("Ignoring @{} on method {}.{} due to illegal modifiers: {}", clazz.getSimpleName(), method.getDeclaringClass().getCanonicalName(),
                        method.getName(), Modifier.toString(method.getModifiers() & ~methodModifiers));
            }
        }
        return false;
    }

    @Override
    public boolean matches(Field field) {
        if (field.isAnnotationPresent(clazz)) {
            if (checkModifiers(field, fieldModifiers)) {
                return true;
            } else {
                log.warn("Ignoring @{} on field {}.{} due to illegal modifiers: {}", clazz.getSimpleName(), field.getDeclaringClass().getCanonicalName(),
                        field.getName(), Modifier.toString(field.getModifiers() & ~fieldModifiers));
            }
        }
        return false;
    }

    private boolean checkModifiers(Member member, int allowed) {
        int modifiers = member.getModifiers();
        return (modifiers & allowed) == modifiers;
    }

    @Override
    public String toString() {
        return "[AnnotationFilter: @" + clazz.getSimpleName() + ", methodModifiers: (" + Modifier.toString(methodModifiers) + "), fieldModifiers: ("
                + Modifier.toString(fieldModifiers) + ")]";
    }

}
