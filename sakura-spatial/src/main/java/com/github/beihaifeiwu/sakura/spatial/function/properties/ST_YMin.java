package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_YMin returns the minimal y-value of the given geometry.
 */
public class ST_YMin {

    /**
     * Returns the minimal y-value of the given geometry.
     *
     * @param geom Geometry
     * @return The minimal y-value of the given geometry, or null if the geometry is null.
     */
    public static Double getMinY(Geometry geom) {
        if (geom != null) {
            return geom.getEnvelopeInternal().getMinY();
        } else {
            return null;
        }
    }
}
