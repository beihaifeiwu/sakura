package com.github.beihaifeiwu.sakura.spatial.function.create;

import com.vividsolutions.jts.algorithm.MinimumBoundingCircle;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * Compute the minimum bounding circle center of a geometry
 */
public class ST_BoundingCircleCenter {

    /**
     * Compute the minimum bounding circle center of a geometry
     *
     * @param geometry Any geometry
     * @return Minimum bounding circle center point
     */
    public static Point getCircumCenter(Geometry geometry) {
        if (geometry == null || geometry.getNumPoints() == 0) {
            return null;
        }
        return geometry.getFactory().createPoint(new MinimumBoundingCircle(geometry).getCentre());
    }
}
