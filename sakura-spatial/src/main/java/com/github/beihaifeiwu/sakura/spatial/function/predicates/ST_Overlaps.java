package com.github.beihaifeiwu.sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if the geometry A overlaps the geometry B
 */
public class ST_Overlaps {

    /**
     * @param a Surface Geometry.
     * @param b Geometry instance
     * @return true if the geometry A overlaps the geometry B
     */
    public static Boolean isOverlaps(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.overlaps(b);
    }
}
