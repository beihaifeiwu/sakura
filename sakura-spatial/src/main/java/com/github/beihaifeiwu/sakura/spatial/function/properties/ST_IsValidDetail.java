package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

/**
 * Returns a valid_detail (valid,reason,location) as an array of objects.
 * If a geometry is valid or not and if not valid, a reason why and a location where.
 */
public class ST_IsValidDetail {

    /**
     * Returns a valid_detail as an array of objects
     * [0] = isvalid,[1] = reason, [2] = error location
     *
     * @param geometry
     * @return
     */
    public static Object[] isValidDetail(Geometry geometry) {
        return isValidDetail(geometry, 0);
    }

    /**
     * Returns a valid_detail as an array of objects
     * [0] = isvalid,[1] = reason, [2] = error location
     * <p>
     * isValid equals true if the geometry is valid.
     * reason correponds to an error message describing this error.
     * error returns the location of this error (on the {@link Geometry}
     * containing the error.
     *
     * @param geometry
     * @param flag
     * @return
     */
    public static Object[] isValidDetail(Geometry geometry, int flag) {
        if (geometry != null) {
            if (flag == 0) {
                return detail(geometry, false);
            } else if (flag == 1) {
                return detail(geometry, true);
            } else {
                throw new IllegalArgumentException("Supported arguments is 0 or 1.");
            }
        }
        return null;
    }

    /**
     * @param geometry
     * @return
     */
    private static Object[] detail(Geometry geometry, boolean flag) {
        Object[] details = new Object[3];
        IsValidOp validOP = new IsValidOp(geometry);
        validOP.setSelfTouchingRingFormingHoleValid(flag);
        TopologyValidationError error = validOP.getValidationError();
        if (error != null) {
            details[0] = false;
            details[1] = error.getMessage();
            details[2] = GeometryFactories.default_().createPoint(error.getCoordinate());
        } else {
            details[0] = true;
            details[1] = "Valid Geometry";
        }
        return details;
    }

}
