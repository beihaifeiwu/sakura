package com.github.beihaifeiwu.sakura.spatial.function.generalize;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

/**
 * Returns a simplified version of the given geometry using the Douglas-Peuker
 * algorithm.
 */
public class ST_Simplify {

    /**
     * Simplify the geometry using the douglad peucker algorithm.
     *
     * @param geometry
     * @param distance
     * @return
     */
    public static Geometry simplify(Geometry geometry, double distance) {
        if (geometry == null) {
            return null;
        }
        return DouglasPeuckerSimplifier.simplify(geometry, distance);
    }
}
