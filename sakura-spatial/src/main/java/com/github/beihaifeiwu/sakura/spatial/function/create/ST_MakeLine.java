package com.github.beihaifeiwu.sakura.spatial.function.create;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * ST_MakeLine constructs a LINESTRING from POINT and MULTIPOINT geometries.
 */
public class ST_MakeLine {

    public static final int REQUIRED_NUMBER_OF_POINTS = 2;

    /**
     * Constructs a LINESTRING from the given POINTs or MULTIPOINTs
     *
     * @param pointA         The first POINT or MULTIPOINT
     * @param optionalPoints Optional POINTs or MULTIPOINTs
     * @return The LINESTRING constructed from the given POINTs or MULTIPOINTs
     */
    public static LineString createLine(Geometry pointA, Geometry... optionalPoints) {
        if (pointA == null || optionalPoints.length > 0 && optionalPoints[0] == null) {
            return null;
        }
        if (pointA.getNumGeometries() == 1 && !atLeastTwoPoints(optionalPoints, countPoints(pointA))) {
            throw new SpatialException("At least two points are required to make a line.");
        }
        List<Coordinate> coordinateList = new LinkedList<Coordinate>();
        addCoordinatesToList(pointA, coordinateList);
        for (Geometry optionalPoint : optionalPoints) {
            addCoordinatesToList(optionalPoint, coordinateList);
        }
        return pointA.getFactory().createLineString(
                coordinateList.toArray(new Coordinate[optionalPoints.length]));
    }

    /**
     * Constructs a LINESTRING from the given collection of POINTs and/or
     * MULTIPOINTs
     *
     * @param points Points
     * @return The LINESTRING constructed from the given collection of POINTs
     * and/or MULTIPOINTs
     */
    public static LineString createLine(GeometryCollection points) {
        if (points == null) {
            return null;
        }
        final int size = points.getNumGeometries();
        if (!atLeastTwoPoints(points)) {
            throw new SpatialException("At least two points are required to make a line.");
        }
        List<Coordinate> coordinateList = new LinkedList<Coordinate>();
        for (int i = 0; i < size; i++) {
            coordinateList.addAll(Arrays.asList(points.getGeometryN(i).getCoordinates()));
        }
        return points.getGeometryN(0).getFactory().createLineString(
                coordinateList.toArray(new Coordinate[size]));
    }

    private static void addCoordinatesToList(Geometry puntal, List<Coordinate> list) {
        if (puntal instanceof Point) {
            list.add(puntal.getCoordinate());
        } else if (puntal instanceof MultiPoint) {
            list.addAll(Arrays.asList(puntal.getCoordinates()));
        } else {
            throw new SpatialException("Only Points and MultiPoints are accepted.");
        }
    }

    private static boolean atLeastTwoPoints(GeometryCollection points) {
        return atLeastTwoPoints(points, 0);
    }

    /**
     * Returns true as soon as we know the collection contains at least two
     * points. Start counting from the initial number of points.
     *
     * @param points                Collection of points
     * @param initialNumberOfPoints The initial number of points
     * @return True as soon as we know the collection contains at least two
     * points.
     */
    private static boolean atLeastTwoPoints(Geometry[] points,
                                            int initialNumberOfPoints) {
        if (points.length < 1) {
            throw new SpatialException("The geometry collection must not be empty");
        }
        return atLeastTwoPoints(points[0].getFactory().createGeometryCollection(points),
                initialNumberOfPoints);
    }

    /**
     * Returns true as soon as we know the collection contains at least two
     * points. Start counting from the initial number of points.
     *
     * @param points                Collection of points
     * @param initialNumberOfPoints The initial number of points
     * @return True as soon as we know the collection contains at least two
     * points.
     */
    private static boolean atLeastTwoPoints(GeometryCollection points,
                                            int initialNumberOfPoints) {
        int numberOfPoints = initialNumberOfPoints;
        for (int i = 0; i < points.getNumGeometries(); i++) {
            Geometry p = points.getGeometryN(i);
            if (numberOfPoints >= REQUIRED_NUMBER_OF_POINTS) {
                return true;
            }
            numberOfPoints = numberOfPoints + countPoints(p);
        }
        return numberOfPoints >= REQUIRED_NUMBER_OF_POINTS;
    }

    private static int countPoints(Geometry p) {
        if (p instanceof Point) {
            return 1;
        } else if (p instanceof MultiPoint) {
            return p.getNumPoints();
        } else {
            throw new SpatialException("Only Points and MultiPoints are accepted.");
        }
    }
}
