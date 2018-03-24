package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Returns the last coordinate of a Geometry as a POINT, given that the
 * Geometry is a LINESTRING or a MULTILINESTRING containing only one
 * LINESTRING; Returns NULL for all other Geometries.
 */
public class ST_EndPoint {

    /**
     * @param geometry Geometry
     * @return The last coordinate of a Geometry as a POINT, given that the
     * Geometry is a LINESTRING or a MULTILINESTRING containing only one
     * LINESTRING; Returns NULL for all other Geometries.
     */
    public static Geometry getEndPoint(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof MultiLineString) {
            if (geometry.getNumGeometries() == 1) {
                return ((LineString) geometry.getGeometryN(0)).getEndPoint();
            }
        } else if (geometry instanceof LineString) {
            return ((LineString) geometry).getEndPoint();
        }
        return null;
    }
}
