package sakura.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ognl.*;
import sakura.common.cache.Cache;
import sakura.common.cache.Caches;
import sakura.common.lang.Objects;
import sakura.common.lang.annotation.Nullable;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * http://commons.apache.org/proper/commons-ognl/language-guide.html
 * <p>
 * Created by haomu on 2018/4/20.
 */
@Slf4j
@UtilityClass
public class OgnlUtils {

    private static final Cache<String, Node> EXPRESSION_CACHE = Caches.newLRUCache(256);

    private static final TypeConverter TYPE_CONVERTER = new DefaultTypeConverter();

    @Nullable
    public static Object getValue(String expression,
                                  Object root, @Nullable Map context, @Nullable Class resultType) {
        try {
            Object parsedExp = parseExpression(expression);
            context = context == null ? Collections.EMPTY_MAP : context;
            Map ctx = Ognl.addDefaultContext(root, null, TYPE_CONVERTER, context);
            return Ognl.getValue(parsedExp, ctx, root, resultType);
        } catch (ExpressionSyntaxException e) {
            log.error("Parse expression failed: {}", expression, e);
        } catch (Exception ignore) {
        }
        return null;
    }

    @Nullable
    public static Object getValue(String expression, Object root, @Nullable Map context) {
        return getValue(expression, root, context, null);
    }

    @Nullable
    public static Object getValue(String expression, Object root, @Nullable Class resultType) {
        return getValue(expression, root, null, resultType);
    }

    @Nullable
    public static Object getValue(String expression, Object root) {
        return getValue(expression, root, null, null);
    }

    public static boolean getBoolean(String expression, Object root, @Nullable Map context) {
        Object value = getValue(expression, root, context);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
        }
        return value != null;
    }

    public static boolean getBoolean(String expression, Object root) {
        return getBoolean(expression, root, null);
    }

    public static Iterable<?> getIterable(String expression, Object root, @Nullable Map context) {
        Object value = getValue(expression, root, context);
        return Objects.toIterable(value);
    }

    public static Iterable<?> getIterable(String expression, Object root) {
        return getIterable(expression, root, null);
    }

    private static Object parseExpression(String expression) throws OgnlException {
        try {
            Node node = EXPRESSION_CACHE.get(expression);
            if (node == null) {
                node = new OgnlParser(new StringReader(expression)).topLevelExpression();
                EXPRESSION_CACHE.put(expression, node);
            }
            return node;
        } catch (ParseException | TokenMgrError e) {
            throw new ExpressionSyntaxException(expression, e);
        }
    }

}
