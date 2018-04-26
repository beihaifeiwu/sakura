package sakura.common.lang;

import com.google.common.base.CharMatcher;
import lombok.experimental.UtilityClass;
import sakura.common.lang.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Helper methods for enum types.
 */
@UtilityClass
public class Enums {

    public static Field getField(Enum<?> enumValue) {
        return com.google.common.base.Enums.getField(enumValue);
    }

    public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value) {
        return com.google.common.base.Enums.getIfPresent(enumClass, value).toJavaUtil();
    }

    /**
     * Convert a string to an enum with more permissive rules than {@link Enum} valueOf().
     * <p/>
     * This method is more permissive in the following ways:
     * <ul>
     * <li>Whitespace is permitted but stripped from the input.</li>
     * <li>Dashes and periods in the value are converted to underscores.</li>
     * <li>Matching against the enum values is case insensitive.</li>
     * </ul>
     *
     * @param value     The string to convert.
     * @param constants The list of constants for the {@link Enum} to which you wish to convert.
     * @return The enum or null, if no enum constant matched the input value.
     */
    @Nullable
    public static Enum<?> fromStringFuzzy(String value, Enum<?>[] constants) {
        final String text = CharMatcher.whitespace()
                .removeFrom(value)
                .replace('-', '_')
                .replace('.', '_');
        for (Enum<?> constant : constants) {
            if (constant.name().equalsIgnoreCase(text)) {
                return constant;
            }
        }

        // In some cases there are certain enums that don't follow the same pattern across an enterprise.  So this
        // means that you have a mix of enums that use toString(), some use @JsonCreator, and some just use the
        // standard constant name().  This block handles finding the proper enum by toString()
        for (Enum<?> constant : constants) {
            if (constant.toString().equalsIgnoreCase(value)) {
                return constant;
            }
        }

        return null;
    }

}
