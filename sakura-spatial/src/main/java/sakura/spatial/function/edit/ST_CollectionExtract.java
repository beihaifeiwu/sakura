package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;

import java.util.ArrayList;

/**
 * Given a (multi)geometry, returns a (multi)geometry consisting only of elements of the specified dimension.
 * Dimension numbers are 1 == POINT, 2 == LINESTRING, 3 == POLYGON
 */
public class ST_CollectionExtract {

    /**
     * Given a (multi)geometry, returns a (multi)geometry consisting only of
     * elements of the specified type. Sub-geometries that are not the specified
     * type are ignored. If there are no sub-geometries of the right type, an
     * EMPTY geometry will be returned. Only points, lines and polygons are
     * extracted.
     *
     * @param geometry
     * @param dimension
     * @return
     * @throws ParseException
     */
    public static Geometry collectionExtract(Geometry geometry, int dimension) throws ParseException {
        if (geometry == null) {
            return null;
        }
        if ((dimension < 1) || (dimension > 3)) {
            throw new IllegalArgumentException(
                    "Dimension out of range (1..3)");
        }
        if (dimension == 1) {
            ArrayList<Point> points = new ArrayList<Point>();
            getPunctualGeometry(points, geometry);
            if (points.isEmpty()) {
                return geometry.getFactory().buildGeometry(points);
            } else if (points.size() == 1) {
                return points.get(0);
            } else {
                return geometry.getFactory().createMultiPoint(points.toArray(new Point[points.size()]));
            }
        } else if (dimension == 2) {
            ArrayList<LineString> lines = new ArrayList<LineString>();
            getLinealGeometry(lines, geometry);
            if (lines.isEmpty()) {
                return geometry.getFactory().buildGeometry(lines);
            } else if (lines.size() == 1) {
                return lines.get(0);
            } else {
                return geometry.getFactory().createMultiLineString(lines.toArray(new LineString[lines.size()]));
            }
        } else if (dimension == 3) {
            ArrayList<Polygon> polygones = new ArrayList<Polygon>();
            getArealGeometry(polygones, geometry);
            if (polygones.isEmpty()) {
                return geometry.getFactory().buildGeometry(polygones);
            } else if (polygones.size() == 1) {
                return polygones.get(0);
            } else {
                return geometry.getFactory().createMultiPolygon(polygones.toArray(new Polygon[polygones.size()]));
            }
        }
        return null;
    }

    /**
     * Filter point from a geometry
     *
     * @param points
     * @param geometry
     */
    private static void getPunctualGeometry(ArrayList<Point> points, Geometry geometry) {
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if (subGeom instanceof Point) {
                points.add((Point) subGeom);
            } else if (subGeom instanceof GeometryCollection) {
                getPunctualGeometry(points, subGeom);
            }
        }
    }

    /**
     * Filter line from a geometry
     *
     * @param lines
     * @param geometry
     */
    private static void getLinealGeometry(ArrayList<LineString> lines, Geometry geometry) {
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if (subGeom instanceof LineString) {
                lines.add((LineString) subGeom);
            } else if (subGeom instanceof GeometryCollection) {
                getLinealGeometry(lines, subGeom);
            }
        }
    }

    /**
     * Filter polygon from a geometry
     *
     * @param polygones
     * @param geometry
     */
    private static void getArealGeometry(ArrayList<Polygon> polygones, Geometry geometry) {
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if (subGeom instanceof Polygon) {
                polygones.add((Polygon) subGeom);
            } else if (subGeom instanceof GeometryCollection) {
                getArealGeometry(polygones, subGeom);
            }
        }
    }
}
