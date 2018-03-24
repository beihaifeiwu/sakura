package com.github.beihaifeiwu.sakura.spatial.function.generalize;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

/**
 * Reduce the geometry precision. Decimal_Place is the number of decimals to keep.
 */
public class ST_PrecisionReducer {

    /**
     * Reduce the geometry precision. Decimal_Place is the number of decimals to
     * keep.
     *
     * @param geometry
     * @param nbDec
     * @return
     */
    public static Geometry precisionReducer(Geometry geometry, int nbDec) {
        if (geometry == null) {
            return null;
        }
        if (nbDec < 0) {
            throw new SpatialException("Decimal_places has to be >= 0.");
        }
        PrecisionModel pm = new PrecisionModel(scaleFactorForDecimalPlaces(nbDec));
        GeometryPrecisionReducer geometryPrecisionReducer = new GeometryPrecisionReducer(pm);
        return geometryPrecisionReducer.reduce(geometry);
    }

    /**
     * Computes the scale factor for a given number of decimal places.
     *
     * @param decimalPlaces
     * @return the scale factor
     */
    public static double scaleFactorForDecimalPlaces(int decimalPlaces) {
        return Math.pow(10.0, decimalPlaces);
    }
}
