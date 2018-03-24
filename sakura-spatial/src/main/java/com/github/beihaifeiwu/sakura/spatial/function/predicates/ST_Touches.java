package com.github.beihaifeiwu.sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if the geometry A touches the geometry B
 */
public class ST_Touches {

    /**
     * Return true if the geometry A touches the geometry B
     *
     * @param a Geometry Geometry.
     * @param b Geometry instance
     * @return true if the geometry A touches the geometry B
     */
    public static Boolean geomTouches(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.touches(b);
    }
}
