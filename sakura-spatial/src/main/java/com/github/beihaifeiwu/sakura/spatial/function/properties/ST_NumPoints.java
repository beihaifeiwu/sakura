package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Get the number of points inside a geometry
 */
public class ST_NumPoints {
    /**
     * @param geometry Geometry instance or null
     * @return Number of points or null if Geometry is null.
     */
    public static Integer getNumPoints(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof LineString) {
            return geometry.getNumPoints();
        }
        return null;
    }
}
