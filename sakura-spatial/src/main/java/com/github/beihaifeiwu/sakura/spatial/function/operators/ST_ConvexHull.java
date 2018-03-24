package com.github.beihaifeiwu.sakura.spatial.function.operators;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Computes the smallest convex POLYGON that contains all the points in the
 * Geometry.
 */
public class ST_ConvexHull {

    /**
     * @param geometry
     * @return smallest convex Polygon that contains all the points in the Geometry
     */
    public static Geometry convexHull(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.convexHull();
    }
}
