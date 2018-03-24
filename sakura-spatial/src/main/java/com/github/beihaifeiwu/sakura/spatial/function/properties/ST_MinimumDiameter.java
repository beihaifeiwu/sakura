package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.algorithm.MinimumDiameter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * Compute a minimum diameter for a given geometry
 */
public class ST_MinimumDiameter {

    public static LineString minimumDiameter(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return new MinimumDiameter(geometry).getDiameter();
    }
}
