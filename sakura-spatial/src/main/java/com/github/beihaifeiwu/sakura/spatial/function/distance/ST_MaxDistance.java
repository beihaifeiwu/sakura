package com.github.beihaifeiwu.sakura.spatial.function.distance;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Compute the maximum distance between two geometries.
 */
public class ST_MaxDistance {

    /**
     * Return the maximum distance
     *
     * @param geomA
     * @param geomB
     * @return
     */
    public static Double maxDistance(Geometry geomA, Geometry geomB) {
        return new MaxDistanceOp(geomA, geomB).getDistance();
    }

}
