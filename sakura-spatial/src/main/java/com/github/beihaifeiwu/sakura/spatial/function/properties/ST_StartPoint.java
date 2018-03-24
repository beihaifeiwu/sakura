package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Returns the first coordinate of a Geometry as a POINT, given that the
 * Geometry is a LINESTRING or a MULTILINESTRING containing only one
 * LINESTRING; Returns NULL for all other Geometries.
 */
public class ST_StartPoint {

    /**
     * @param geometry Geometry
     * @return The first coordinate of a Geometry as a POINT, given that the
     * Geometry is a LINESTRING or a MULTILINESTRING containing only one
     * LINESTRING; Returns NULL for all other Geometries.
     */
    public static Geometry getStartPoint(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof MultiLineString) {
            if (geometry.getNumGeometries() == 1) {
                return ((LineString) geometry.getGeometryN(0)).getStartPoint();
            }
        } else if (geometry instanceof LineString) {
            return ((LineString) geometry).getStartPoint();

        }
        return null;
    }
}
