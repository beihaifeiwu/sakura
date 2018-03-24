
package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get the first Y coordinate
 */
public class ST_Y {

    /**
     * @param geometry Geometry instance
     * @return A y coordinate or null if null or empty geometry.
     */
    public static Double getY(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getCoordinate().y;
    }
}
