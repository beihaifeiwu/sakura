package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return the type of geometry : POINT, LINESTRING, POLYGON...
 */
public class ST_GeometryType {

    /**
     * @param geometry Geometry instance
     * @return Geometry type equivalent to {@link Geometry#getGeometryType()}
     */
    public static String getGeometryType(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getGeometryType();
    }
}
