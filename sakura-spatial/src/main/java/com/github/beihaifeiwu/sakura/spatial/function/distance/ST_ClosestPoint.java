package com.github.beihaifeiwu.sakura.spatial.function.distance;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;

/**
 * ST_ClosestPoint returns the 2D point on geometry A that is closest to
 * geometry B.  If the closest point is not unique, it returns the first one it
 * finds. This means that the point returned depends on the order of the
 * geometry's coordinates.
 */
public class ST_ClosestPoint {

    /**
     * Returns the 2D point on geometry A that is closest to geometry B.
     *
     * @param geomA Geometry A
     * @param geomB Geometry B
     * @return The 2D point on geometry A that is closest to geometry B
     */
    public static Point closestPoint(Geometry geomA, Geometry geomB) {
        if (geomA == null || geomB == null) {
            return null;
        }
        // Return the closest point on geomA. (We would have used index
        // 1 to return the closest point on geomB.)
        return geomA.getFactory().createPoint(DistanceOp.nearestPoints(geomA, geomB)[0]);
    }
}
