package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Returns a LinearRing instance or Null if parameter is not a Polygon.
 * {@link ST_NumInteriorRings}
 */
public class ST_InteriorRingN {

    private static final String OUT_OF_BOUNDS_ERR_MESSAGE =
            "Interior ring index out of range. Must be between 1 and ST_NumInteriorRings.";

    /**
     * @param geometry Polygon
     * @param n        Index of interior ring number n in [1-N]
     * @return Interior ring number n or NULL if parameter is null.
     */
    public static LineString getInteriorRing(Geometry geometry, Integer n) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof Polygon) {
            Polygon polygon = (Polygon) geometry;
            if (n >= 1 && n <= polygon.getNumInteriorRing()) {
                return polygon.getInteriorRingN(n - 1);
            } else {
                throw new SpatialException(OUT_OF_BOUNDS_ERR_MESSAGE);
            }
        } else {
            return null;
        }
    }
}
