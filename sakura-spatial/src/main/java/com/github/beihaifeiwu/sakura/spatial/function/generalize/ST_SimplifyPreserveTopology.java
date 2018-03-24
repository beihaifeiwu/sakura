package com.github.beihaifeiwu.sakura.spatial.function.generalize;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

/**
 * Simplifies a geometry and ensures that the result is a valid geometry.
 */
public class ST_SimplifyPreserveTopology {

    /**
     * Simplifies a geometry and ensures that the result is a valid geometry
     * having the same dimension and number of components as the input, and with
     * the components having the same topological relationship.
     *
     * @param geometry
     * @param distance
     * @return
     */
    public static Geometry simplyPreserve(Geometry geometry, double distance) {
        if (geometry == null) {
            return null;
        }
        return TopologyPreservingSimplifier.simplify(geometry, distance);
    }
}
