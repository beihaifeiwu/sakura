package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_XMax returns the maximal x-value of the given geometry.
 */
public class ST_XMax {

    /**
     * Returns the maximal x-value of the given geometry.
     *
     * @param geom Geometry
     * @return The maximal x-value of the given geometry, or null if the geometry is null.
     */
    public static Double getMaxX(Geometry geom) {
        if (geom != null) {
            return geom.getEnvelopeInternal().getMaxX();
        } else {
            return null;
        }
    }
}
