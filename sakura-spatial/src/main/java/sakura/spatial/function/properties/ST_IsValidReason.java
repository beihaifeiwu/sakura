package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

/**
 * Returns text stating if a geometry is valid or not an if not valid, a reason why
 */
public class ST_IsValidReason {

    /**
     * Returns text stating whether a geometry is valid.
     * If not, returns a reason why.
     *
     * @param geometry
     * @return
     */
    public static String isValidReason(Geometry geometry) {
        return isValidReason(geometry, 0);
    }

    /**
     * Returns text stating whether a geometry is valid.
     * If not, returns a reason why.
     *
     * @param geometry
     * @param flag
     * @return
     */
    public static String isValidReason(Geometry geometry, int flag) {
        if (geometry != null) {
            if (flag == 0) {
                return validReason(geometry, false);
            } else if (flag == 1) {
                return validReason(geometry, true);
            } else {
                throw new IllegalArgumentException("Supported arguments are 0 or 1.");
            }
        }
        return "Null Geometry";
    }

    /**
     * @param geometry
     * @return
     */
    private static String validReason(Geometry geometry, boolean flag) {
        IsValidOp validOP = new IsValidOp(geometry);
        validOP.setSelfTouchingRingFormingHoleValid(flag);
        TopologyValidationError error = validOP.getValidationError();
        if (error != null) {
            return error.toString();
        } else {
            return "Valid Geometry";
        }
    }

}
