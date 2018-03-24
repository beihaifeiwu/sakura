package com.github.beihaifeiwu.sakura.spatial.function.distance;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * ST_ClosestCoordinate computes the closest coordinate(s) contained in the
 * given geometry starting from the given point, using the 2D distance. If the
 * coordinate is unique, it is returned as a POINT. If it is not, then all
 * closest coordinates are returned in a MULTIPOINT.
 */
public class ST_ClosestCoordinate {

    /**
     * Computes the closest coordinate(s) contained in the given geometry starting
     * from the given point, using the 2D distance.
     *
     * @param point Point
     * @param geom  Geometry
     * @return The closest coordinate(s) contained in the given geometry starting from
     * the given point, using the 2D distance
     */
    public static Geometry getFurthestCoordinate(Point point, Geometry geom) {
        if (point == null || geom == null) {
            return null;
        }
        double minDistance = Double.POSITIVE_INFINITY;
        Coordinate pointCoordinate = point.getCoordinate();
        Set<Coordinate> closestCoordinates = new HashSet<Coordinate>();
        for (Coordinate c : geom.getCoordinates()) {
            double distance = c.distance(pointCoordinate);
            if (Double.compare(distance, minDistance) == 0) {
                closestCoordinates.add(c);
            }
            if (Double.compare(distance, minDistance) < 0) {
                minDistance = distance;
                closestCoordinates.clear();
                closestCoordinates.add(c);
            }
        }
        if (closestCoordinates.size() == 1) {
            return GeometryFactories.default_().createPoint(closestCoordinates.iterator().next());
        }
        return GeometryFactories.default_().createMultiPoint(
                closestCoordinates.toArray(new Coordinate[closestCoordinates.size()]));
    }
}
