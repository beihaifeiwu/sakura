package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get the first X coordinate
 */
public class ST_X {

    /**
     * @param geometry Geometry instance
     * @return A x coordinate or null if null or empty geometry.
     */
    public static Double getX(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getCoordinate().x;
    }
}
