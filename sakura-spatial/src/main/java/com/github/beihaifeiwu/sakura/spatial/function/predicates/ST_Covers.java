package com.github.beihaifeiwu.sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_Covers returns true if no point in geometry B is outside geometry A.
 */
public class ST_Covers {

    /**
     * Returns true if no point in geometry B is outside geometry A.
     *
     * @param geomA Geometry A
     * @param geomB Geometry B
     * @return True if no point in geometry B is outside geometry A
     */
    public static Boolean covers(Geometry geomA, Geometry geomB) {
        if (geomA == null || geomB == null) {
            return null;
        }
        return geomA.covers(geomB);
    }
}
