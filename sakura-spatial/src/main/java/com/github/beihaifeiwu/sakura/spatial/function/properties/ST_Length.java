package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Compute the geometry length.
 * Returns the 2D length of the geometry if it is a LineString or MultiLineString.
 * 0 is returned for other geometries
 */
public class ST_Length {

    /**
     * @param geometry Geometry instance or 0
     * @return Geometry length for LineString or MultiLineString otherwise 0
     */
    public static Double getLength(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof LineString || geometry instanceof MultiLineString) {
            return geometry.getLength();
        }
        return 0.0d;
    }
}
