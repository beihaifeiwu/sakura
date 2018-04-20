package sakura.common.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ognl.*;
import sakura.common.lang.Objects;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by haomu on 2018/4/20.
 */
@Slf4j
@UtilityClass
public class OgnlUtils {

    private static final Map<String, Node> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    public static Object getValue(String expression, Object root) {
        try {
            return Ognl.getValue(parseExpression(expression), root);
        } catch (ExpressionSyntaxException e) {
            log.error("Parse expression failed: {}", expression, e);
        } catch (Exception ignore) {
        }
        return null;
    }

    public static boolean getBoolean(String expression, Object root) {
        Object value = getValue(expression, root);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
        }
        return value != null;
    }

    public static Iterable<?> getIterable(String expression, Object root) {
        Object value = getValue(expression, root);
        return Objects.toIterable(value);
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
