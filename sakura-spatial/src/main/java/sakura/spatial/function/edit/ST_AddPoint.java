package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
import sakura.spatial.SpatialException;
import sakura.spatial.utils.CoordinateUtils;
import sakura.spatial.utils.GeometryFactories;

import java.util.ArrayList;

/**
 * Adds a point to a geometry.
 * A tolerance could be set to snap the point to the geometry.
 *
 * @author Erwan Bocher
 */
public class ST_AddPoint {

    public static final double PRECISION = 10E-6;


    /**
     * Returns a new geometry based on an existing one, with a specific point as
     * a new vertex. A default distance 10E-6 is used to snap the input point.
     *
     * @param geometry
     * @param point
     * @return
     */
    public static Geometry addPoint(Geometry geometry, Point point) {
        return addPoint(geometry, point, PRECISION);
    }

    /**
     * Returns a new geometry based on an existing one, with a specific point as
     * a new vertex.
     *
     * @param geometry
     * @param point
     * @param tolerance
     * @return Null if the vertex cannot be inserted
     * @throws SpatialException If the vertex can be inserted but it makes the
     *                          geometry to be in an invalid shape
     */
    public static Geometry addPoint(Geometry geometry, Point point, double tolerance) {
        if (geometry == null || point == null) {
            return null;
        }
        if (geometry instanceof MultiPoint) {
            return insertVertexInMultipoint(geometry, point);
        } else if (geometry instanceof LineString) {
            return insertVertexInLineString((LineString) geometry, point, tolerance);
        } else if (geometry instanceof MultiLineString) {
            LineString[] linestrings = new LineString[geometry.getNumGeometries()];
            boolean any = false;
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                LineString line = (LineString) geometry.getGeometryN(i);

                LineString inserted = insertVertexInLineString(line, point, tolerance);
                if (inserted != null) {
                    linestrings[i] = inserted;
                    any = true;
                } else {
                    linestrings[i] = line;
                }
            }
            if (any) {
                return GeometryFactories.default_().createMultiLineString(linestrings);
            } else {
                return null;
            }
        } else if (geometry instanceof Polygon) {
            return insertVertexInPolygon((Polygon) geometry, point, tolerance);
        } else if (geometry instanceof MultiPolygon) {
            Polygon[] polygons = new Polygon[geometry.getNumGeometries()];
            boolean any = false;
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                Polygon polygon = (Polygon) geometry.getGeometryN(i);
                Polygon inserted = insertVertexInPolygon(polygon, point, tolerance);
                if (inserted != null) {
                    any = true;
                    polygons[i] = inserted;
                } else {
                    polygons[i] = polygon;
                }
            }
            if (any) {
                return GeometryFactories.default_().createMultiPolygon(polygons);
            } else {
                return null;
            }
        } else if (geometry instanceof Point) {
            return null;
        }
        throw new SpatialException("Unknown geometry type" + " : " + geometry.getGeometryType());
    }

    /**
     * Adds a Point into a MultiPoint geometry.
     *
     * @param g
     * @param vertexPoint
     * @return
     */
    private static Geometry insertVertexInMultipoint(Geometry g, Point vertexPoint) {
        ArrayList<Point> geoms = new ArrayList<Point>();
        for (int i = 0; i < g.getNumGeometries(); i++) {
            Point geom = (Point) g.getGeometryN(i);
            geoms.add(geom);
        }
        GeometryFactory gf = GeometryFactories.default_();
        geoms.add(gf.createPoint(new Coordinate(vertexPoint.getX(), vertexPoint.getY())));
        return gf.createMultiPoint(GeometryFactory.toPointArray(geoms));
    }

    /**
     * Inserts a vertex into a LineString with a given tolerance.
     *
     * @param lineString
     * @param vertexPoint
     * @param tolerance
     * @return
     */
    private static LineString insertVertexInLineString(LineString lineString, Point vertexPoint,
                                                       double tolerance) {
        GeometryLocation geomLocation = EditUtilities.getVertexToSnap(lineString, vertexPoint, tolerance);
        if (geomLocation != null) {
            Coordinate[] coords = lineString.getCoordinates();
            int index = geomLocation.getSegmentIndex();
            Coordinate coord = geomLocation.getCoordinate();
            if (!CoordinateUtils.contains2D(coords, coord)) {
                Coordinate[] ret = new Coordinate[coords.length + 1];
                System.arraycopy(coords, 0, ret, 0, index + 1);
                ret[index + 1] = coord;
                System.arraycopy(coords, index + 1, ret, index + 2, coords.length
                        - (index + 1));
                return GeometryFactories.default_().createLineString(ret);
            }
            return null;
        } else {
            return lineString;
        }
    }

    /**
     * Adds a vertex into a Polygon with a given tolerance.
     *
     * @param polygon
     * @param vertexPoint
     * @param tolerance
     * @return
     */
    private static Polygon insertVertexInPolygon(Polygon polygon,
                                                 Point vertexPoint, double tolerance) {
        Polygon geom = polygon;
        LineString linearRing = polygon.getExteriorRing();
        int index = -1;
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            double distCurr = computeDistance(polygon.getInteriorRingN(i), vertexPoint, tolerance);
            if (distCurr < tolerance) {
                index = i;
            }
        }
        if (index == -1) {
            //The point is a on the exterior ring.
            LinearRing inserted = insertVertexInLinearRing(linearRing, vertexPoint, tolerance);
            if (inserted != null) {
                LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
                for (int i = 0; i < holes.length; i++) {
                    holes[i] = (LinearRing) polygon.getInteriorRingN(i);
                }
                geom = GeometryFactories.default_().createPolygon(inserted, holes);
            }
        } else {
            //We add the vertex on the first hole
            LinearRing[] holes = new LinearRing[polygon.getNumInteriorRing()];
            for (int i = 0; i < holes.length; i++) {
                if (i == index) {
                    holes[i] = insertVertexInLinearRing(polygon.getInteriorRingN(i), vertexPoint, tolerance);
                } else {
                    holes[i] = (LinearRing) polygon.getInteriorRingN(i);
                }
            }
            geom = GeometryFactories.default_().createPolygon((LinearRing) linearRing, holes);
        }
        if (geom != null) {
            if (!geom.isValid()) {
                throw new SpatialException("Geometry not valid");
            }
        }
        return geom;
    }

    /**
     * Return minimum distance between a geometry and a point.
     *
     * @param geometry
     * @param vertexPoint
     * @param tolerance
     * @return
     */
    private static double computeDistance(Geometry geometry, Point vertexPoint, double tolerance) {
        DistanceOp distanceOp = new DistanceOp(geometry, vertexPoint, tolerance);
        return distanceOp.distance();
    }

    /**
     * Adds a vertex into a LinearRing with a given tolerance.
     *
     * @param lineString
     * @param vertexPoint
     * @param tolerance
     * @return
     */
    private static LinearRing insertVertexInLinearRing(LineString lineString,
                                                       Point vertexPoint, double tolerance) {
        GeometryLocation geomLocation = EditUtilities.getVertexToSnap(lineString, vertexPoint, tolerance);
        if (geomLocation != null) {
            Coordinate[] coords = lineString.getCoordinates();
            int index = geomLocation.getSegmentIndex();
            Coordinate coord = geomLocation.getCoordinate();
            if (!CoordinateUtils.contains2D(coords, coord)) {
                Coordinate[] ret = new Coordinate[coords.length + 1];
                System.arraycopy(coords, 0, ret, 0, index + 1);
                ret[index + 1] = coord;
                System.arraycopy(coords, index + 1, ret, index + 2, coords.length
                        - (index + 1));
                return GeometryFactories.default_().createLinearRing(ret);
            }
            return null;
        } else {
            return null;
        }
    }
}
