package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get dimension of a geometry 0 for a Point, 1 for a line and 2 for a polygon.
 */
public class ST_Dimension {

    /**
     * @param geometry Geometry instance
     * @return Geometry dimension
     */
    public static Integer getDimension(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getDimension();
    }
}
