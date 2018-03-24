package com.github.beihaifeiwu.sakura.spatial.function.distance;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

/**
 * Projet a point along a linestring. If the point projected is out of line
 * the first or last point on the line will be returned otherwise the input point.
 */
public class ST_ProjectPoint {

    /**
     * Project a point on a linestring or multilinestring
     *
     * @param point
     * @param geometry
     * @return
     */
    public static Point projectPoint(Geometry point, Geometry geometry) {
        if (point == null || geometry == null) {
            return null;
        }
        if (point.getDimension() == 0 && geometry.getDimension() == 1) {
            LengthIndexedLine ll = new LengthIndexedLine(geometry);
            double index = ll.project(point.getCoordinate());
            Point result = geometry.getFactory().createPoint(ll.extractPoint(index));
            return result;
        } else {
            return null;
        }
    }

}
