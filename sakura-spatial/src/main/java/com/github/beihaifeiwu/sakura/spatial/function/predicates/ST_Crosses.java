package com.github.beihaifeiwu.sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if Geometry A crosses Geometry B.
 */
public class ST_Crosses {

    /**
     * @param a Geometry Geometry.
     * @param b Geometry instance
     * @return true if Geometry A crosses Geometry B
     */
    public static Boolean geomCrosses(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.crosses(b);
    }
}
