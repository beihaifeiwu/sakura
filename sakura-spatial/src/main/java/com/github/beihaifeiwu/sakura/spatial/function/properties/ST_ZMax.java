package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.github.beihaifeiwu.sakura.spatial.utils.CoordinateUtils;
import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_ZMax returns the maximal z-value of the given geometry.
 */
public class ST_ZMax {
    /**
     * Returns the maximal z-value of the given geometry.
     *
     * @param geom Geometry
     * @return The maximal z-value of the given geometry, or null if the geometry is null.
     */
    public static Double getMaxZ(Geometry geom) {
        if (geom != null) {
            return CoordinateUtils.zMinMax(geom.getCoordinates())[1];
        } else {
            return null;
        }
    }
}
