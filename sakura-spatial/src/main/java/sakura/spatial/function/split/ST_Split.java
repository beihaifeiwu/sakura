package sakura.spatial.function.split;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.distance.GeometryLocation;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;
import lombok.experimental.UtilityClass;
import sakura.spatial.SpatialException;
import sakura.spatial.function.convert.ST_ToMultiSegments;
import sakura.spatial.function.edit.EditUtilities;
import sakura.spatial.utils.CoordinateUtils;
import sakura.spatial.utils.GeometryFactories;

import java.util.*;

/**
 * This function split a line by a line a line by a point a polygon by a line
 */
@UtilityClass
@SuppressWarnings("unchecked")
public class ST_Split {

    public static final double PRECISION = 10E-6;

    /**
     * Split a geometry a according a geometry b. Supported operations are :
     * split a line by a line a line by a point a polygon by a line.
     * <p>
     * A default tolerance of 10E-6 is used to snap the cutter point.
     *
     * @param geomA
     * @param geomB
     * @return
     */
    public static Geometry split(Geometry geomA, Geometry geomB) {
        if (geomA == null || geomB == null) {
            return null;
        }
        if (geomA instanceof Polygon) {
            return splitPolygonWithLine((Polygon) geomA, (LineString) geomB);
        } else if (geomA instanceof MultiPolygon) {
            return splitMultiPolygonWithLine((MultiPolygon) geomA, (LineString) geomB);
        } else if (geomA instanceof LineString) {
            if (geomB instanceof LineString) {
                return splitLineStringWithLine((LineString) geomA, (LineString) geomB);
            } else if (geomB instanceof Point) {
                return splitLineWithPoint((LineString) geomA, (Point) geomB, PRECISION);
            }
        } else if (geomA instanceof MultiLineString) {
            if (geomB instanceof LineString) {
                return splitMultiLineStringWithLine((MultiLineString) geomA, (LineString) geomB);
            } else if (geomB instanceof Point) {
                return splitMultiLineStringWithPoint((MultiLineString) geomA, (Point) geomB, PRECISION);
            }
        }
        throw new SpatialException("Split a " + geomA.getGeometryType() + " by a " + geomB.getGeometryType() + " is not supported.");
    }

    /**
     * Split a geometry a according a geometry b using a snapping tolerance.
     * <p>
     * This function support only the operations :
     * <p>
     * - split a line or a multiline with a point.
     *
     * @param geomA     the geometry to be splited
     * @param geomB     the geometry used to split
     * @param tolerance a distance tolerance to snap the split geometry
     * @return
     */
    public static Geometry split(Geometry geomA, Geometry geomB, double tolerance) {
        if (geomA instanceof Polygon) {
            throw new SpatialException("Split a Polygon by a line is not supported using a tolerance. \n"
                    + "Please used ST_Split(geom1, geom2)");
        } else if (geomA instanceof LineString) {
            if (geomB instanceof LineString) {
                throw new SpatialException("Split a line by a line is not supported using a tolerance. \n"
                        + "Please used ST_Split(geom1, geom2)");
            } else if (geomB instanceof Point) {
                return splitLineWithPoint((LineString) geomA, (Point) geomB, tolerance);
            }
        } else if (geomA instanceof MultiLineString) {
            if (geomB instanceof LineString) {
                throw new SpatialException("Split a multiline by a line is not supported using a tolerance. \n"
                        + "Please used ST_Split(geom1, geom2)");
            } else if (geomB instanceof Point) {
                return splitMultiLineStringWithPoint((MultiLineString) geomA, (Point) geomB, tolerance);
            }
        }
        throw new SpatialException("Split a " + geomA.getGeometryType() + " by a " + geomB.getGeometryType() + " is not supported.");
    }

    /**
     * Split a linestring with a point The point must be on the linestring
     *
     * @param line
     * @param pointToSplit
     * @return
     */
    private static MultiLineString splitLineWithPoint(LineString line, Point pointToSplit, double tolerance) {
        return GeometryFactories.default_().createMultiLineString(splitLineStringWithPoint(line, pointToSplit, tolerance));
    }

    /**
     * Splits a LineString using a Point, with a distance tolerance.
     *
     * @param line
     * @param pointToSplit
     * @param tolerance
     * @return
     */
    private static LineString[] splitLineStringWithPoint(LineString line, Point pointToSplit, double tolerance) {
        Coordinate[] coords = line.getCoordinates();
        Coordinate firstCoord = coords[0];
        Coordinate lastCoord = coords[coords.length - 1];
        Coordinate coordToSplit = pointToSplit.getCoordinate();
        if ((coordToSplit.distance(firstCoord) <= PRECISION) || (coordToSplit.distance(lastCoord) <= PRECISION)) {
            return new LineString[]{line};
        } else {
            ArrayList<Coordinate> firstLine = new ArrayList<Coordinate>();
            firstLine.add(coords[0]);
            ArrayList<Coordinate> secondLine = new ArrayList<Coordinate>();
            GeometryLocation geometryLocation = EditUtilities.getVertexToSnap(line, pointToSplit, tolerance);
            if (geometryLocation != null) {
                int segmentIndex = geometryLocation.getSegmentIndex();
                Coordinate coord = geometryLocation.getCoordinate();
                int index = -1;
                for (int i = 1; i < coords.length; i++) {
                    index = i - 1;
                    if (index < segmentIndex) {
                        firstLine.add(coords[i]);
                    } else if (index == segmentIndex) {
                        coord.z = CoordinateUtils.interpolate(coords[i - 1], coords[i], coord);
                        firstLine.add(coord);
                        secondLine.add(coord);
                        if (!coord.equals2D(coords[i])) {
                            secondLine.add(coords[i]);
                        }
                    } else {
                        secondLine.add(coords[i]);
                    }
                }
                GeometryFactory gf = GeometryFactories.default_();
                LineString lineString1 = gf.createLineString(firstLine.toArray(new Coordinate[firstLine.size()]));
                LineString lineString2 = gf.createLineString(secondLine.toArray(new Coordinate[secondLine.size()]));
                return new LineString[]{lineString1, lineString2};
            }
        }
        return null;
    }

    /**
     * Splits a MultilineString using a point.
     *
     * @param multiLineString
     * @param pointToSplit
     * @param tolerance
     * @return
     */
    private static MultiLineString splitMultiLineStringWithPoint(MultiLineString multiLineString, Point pointToSplit,
                                                                 double tolerance) {
        ArrayList<LineString> linestrings = new ArrayList<LineString>();
        boolean notChanged = true;
        int nb = multiLineString.getNumGeometries();
        for (int i = 0; i < nb; i++) {
            LineString subGeom = (LineString) multiLineString.getGeometryN(i);
            LineString[] result = splitLineStringWithPoint(subGeom, pointToSplit, tolerance);
            if (result != null) {
                Collections.addAll(linestrings, result);
                notChanged = false;
            } else {
                linestrings.add(subGeom);
            }
        }
        if (!notChanged) {
            return GeometryFactories.default_().createMultiLineString(linestrings.toArray(new LineString[linestrings.size()]));
        }
        return null;
    }

    /**
     * Splits a Polygon with a LineString.
     *
     * @param polygon
     * @param lineString
     * @return
     */
    private static Collection<Polygon> splitPolygonizer(Polygon polygon, LineString lineString) {
        LinkedList<LineString> result = new LinkedList<LineString>();
        ST_ToMultiSegments.createSegments(polygon.getExteriorRing(), result);
        result.add(lineString);
        int holes = polygon.getNumInteriorRing();
        for (int i = 0; i < holes; i++) {
            ST_ToMultiSegments.createSegments(polygon.getInteriorRingN(i), result);
        }
        // Perform union of all extracted LineStrings (the edge-noding process)
        UnaryUnionOp uOp = new UnaryUnionOp(result);
        Geometry union = uOp.union();

        // Create polygons from unioned LineStrings
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(union);
        Collection<Polygon> polygons = polygonizer.getPolygons();

        if (polygons.size() > 1) {
            return polygons;
        }
        return null;
    }

    /**
     * Splits a Polygon using a LineString.
     *
     * @param polygon
     * @param lineString
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    private static Geometry splitPolygonWithLine(Polygon polygon, LineString lineString) {
        Collection<Polygon> pols = polygonWithLineSplitter(polygon, lineString);
        if (pols != null) {
            return GeometryFactories.default_().buildGeometry(polygonWithLineSplitter(polygon, lineString));
        }
        return null;
    }

    /**
     * Splits a Polygon using a LineString.
     *
     * @param polygon
     * @param lineString
     * @return
     */
    private static Collection<Polygon> polygonWithLineSplitter(Polygon polygon, LineString lineString) {
        Collection<Polygon> polygons = splitPolygonizer(polygon, lineString);
        if (polygons != null && polygons.size() > 1) {
            List<Polygon> pols = new ArrayList<Polygon>();
            for (Polygon pol : polygons) {
                if (polygon.contains(pol.getInteriorPoint())) {
                    pols.add(pol);
                }
            }
            return pols;
        }
        return null;
    }

    /**
     * Splits a MultiPolygon using a LineString.
     *
     * @param multiPolygon
     * @param lineString
     * @return
     */
    private static Geometry splitMultiPolygonWithLine(MultiPolygon multiPolygon, LineString lineString) {
        ArrayList<Polygon> allPolygons = new ArrayList<Polygon>();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Collection<Polygon> polygons = splitPolygonizer((Polygon) multiPolygon.getGeometryN(i), lineString);
            if (polygons != null) {
                allPolygons.addAll(polygons);
            }
        }
        if (!allPolygons.isEmpty()) {
            return GeometryFactories.default_().buildGeometry(allPolygons);
        }
        return null;
    }

    /**
     * Splits the specified lineString with another lineString.
     *
     * @param input
     * @param cut
     */
    private static Geometry splitLineStringWithLine(LineString input, LineString cut) {
        return input.difference(cut);
    }

    /**
     * Splits the specified MultiLineString with another lineString.
     *
     * @param input
     * @param cut
     */
    private static Geometry splitMultiLineStringWithLine(MultiLineString input, LineString cut) {
        Geometry lines = input.difference(cut);
        //Only to preserve SQL constrains
        if (lines instanceof LineString) {
            return GeometryFactories.default_().createMultiLineString(new LineString[]{(LineString) lines.getGeometryN(0)});
        }
        return lines;
    }
}
