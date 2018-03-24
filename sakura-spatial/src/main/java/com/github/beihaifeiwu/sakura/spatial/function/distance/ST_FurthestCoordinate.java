package com.github.beihaifeiwu.sakura.spatial.function.distance;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.util.HashSet;
import java.util.Set;

/**
 * ST_FurthestCoordinate computes the furthest coordinate(s) contained in the
 * given geometry starting from the given point, using the 2D distance. If the
 * coordinate is unique, it is returned as a POINT. If it is not, then all
 * furthest coordinates are returned in a MULTIPOINT.
 */
public class ST_FurthestCoordinate {

    /**
     * Computes the furthest coordinate(s) contained in the given geometry starting
     * from the given point, using the 2D distance.
     *
     * @param point Point
     * @param geom  Geometry
     * @return The furthest coordinate(s) contained in the given geometry starting from
     * the given point, using the 2D distance
     */
    public static Geometry getFurthestCoordinate(Point point, Geometry geom) {
        if (point == null || geom == null) {
            return null;
        }
        double maxDistance = Double.NEGATIVE_INFINITY;
        Coordinate pointCoordinate = point.getCoordinate();
        Set<Coordinate> furthestCoordinates = new HashSet<Coordinate>();
        for (Coordinate c : geom.getCoordinates()) {
            double distance = c.distance(pointCoordinate);
            if (Double.compare(distance, maxDistance) == 0) {
                furthestCoordinates.add(c);
            }
            if (Double.compare(distance, maxDistance) > 0) {
                maxDistance = distance;
                furthestCoordinates.clear();
                furthestCoordinates.add(c);
            }
        }
        if (furthestCoordinates.size() == 1) {
            return GeometryFactories.default_().createPoint(furthestCoordinates.iterator().next());
        }
        return GeometryFactories.default_().createMultiPoint(
                furthestCoordinates.toArray(new Coordinate[furthestCoordinates.size()]));
    }
}
