package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_XMin returns the minimal x-value of the given geometry.
 */
public class ST_XMin {

    /**
     * Returns the minimal x-value of the given geometry.
     *
     * @param geom Geometry
     * @return The minimal x-value of the given geometry, or null if the geometry is null.
     */
    public static Double getMinX(Geometry geom) {
        if (geom != null) {
            return geom.getEnvelopeInternal().getMinX();
        } else {
            return null;
        }
    }
}
