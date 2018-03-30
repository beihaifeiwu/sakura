package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.utils.CoordinateUtils;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.*;

import java.util.ArrayList;

/**
 * Remove duplicated points on a geometry
 * Returns a version of the given geometry with duplicated points removed.
 * If the tolerance parameter is provided, vertices within the tolerance of one another
 * will be considered the same for the purposes of removal.
 */
public class ST_RemoveRepeatedPoints {

    /**
     * Returns a version of the given geometry with duplicated points removed.
     *
     * @param geometry
     * @return
     */
    public static Geometry removeRepeatedPoints(Geometry geometry) {
        return removeDuplicateCoordinates(geometry, 0);
    }

    /**
     * Returns a version of the given geometry with duplicated points removed.
     *
     * @param geometry
     * @param tolerance to delete the coordinates
     * @return
     */
    public static Geometry removeRepeatedPoints(Geometry geometry, double tolerance) {
        return removeDuplicateCoordinates(geometry, tolerance);
    }

    /**
     * Removes duplicated points within a geometry.
     *
     * @param geom
     * @param tolerance to delete the coordinates
     * @return
     */
    public static Geometry removeDuplicateCoordinates(Geometry geom, double tolerance) {
        if (geom == null) {
            return null;
        } else if (geom.isEmpty()) {
            return geom;
        } else if (geom instanceof Point) {
            return geom;
        } else if (geom instanceof MultiPoint) {
            return geom;
        } else if (geom instanceof LineString) {
            return removeDuplicateCoordinates((LineString) geom, tolerance);
        } else if (geom instanceof MultiLineString) {
            return removeDuplicateCoordinates((MultiLineString) geom, tolerance);
        } else if (geom instanceof Polygon) {
            return removeDuplicateCoordinates((Polygon) geom, tolerance);
        } else if (geom instanceof MultiPolygon) {
            return removeDuplicateCoordinates((MultiPolygon) geom, tolerance);
        } else if (geom instanceof GeometryCollection) {
            return removeDuplicateCoordinates((GeometryCollection) geom, tolerance);
        }
        return null;
    }


    /**
     * Removes duplicated coordinates within a LineString.
     *
     * @param linestring
     * @param tolerance  to delete the coordinates
     * @return
     */
    public static LineString removeDuplicateCoordinates(LineString linestring, double tolerance) {
        Coordinate[] coords = CoordinateUtils.removeRepeatedCoordinates(linestring.getCoordinates(), tolerance, false);
        if (coords.length < 2) {
            throw new SpatialException("Not enough coordinates to build a new LineString.\n Please adjust the tolerance");
        }
        return GeometryFactories.default_().createLineString(coords);
    }

    /**
     * Removes duplicated coordinates within a linearRing.
     *
     * @param linearRing
     * @param tolerance  to delete the coordinates
     * @return
     */
    public static LinearRing removeDuplicateCoordinates(LinearRing linearRing, double tolerance) {
        Coordinate[] coords = CoordinateUtils.removeRepeatedCoordinates(linearRing.getCoordinates(), tolerance, true);
        return GeometryFactories.default_().createLinearRing(coords);
    }

    /**
     * Removes duplicated coordinates in a MultiLineString.
     *
     * @param multiLineString
     * @param tolerance       to delete the coordinates
     * @return
     */
    public static MultiLineString removeDuplicateCoordinates(MultiLineString multiLineString, double tolerance) {
        ArrayList<LineString> lines = new ArrayList<LineString>();
        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            LineString line = (LineString) multiLineString.getGeometryN(i);
            lines.add(removeDuplicateCoordinates(line, tolerance));
        }
        return GeometryFactories.default_().createMultiLineString(GeometryFactory.toLineStringArray(lines));
    }

    /**
     * Removes duplicated coordinates within a Polygon.
     *
     * @param polygon   the input polygon
     * @param tolerance to delete the coordinates
     * @return
     */
    public static Polygon removeDuplicateCoordinates(Polygon polygon, double tolerance) {
        GeometryFactory gf = GeometryFactories.default_();
        Coordinate[] shellCoords = CoordinateUtils.removeRepeatedCoordinates(polygon.getExteriorRing().getCoordinates(), tolerance, true);
        LinearRing shell = gf.createLinearRing(shellCoords);
        ArrayList<LinearRing> holes = new ArrayList<LinearRing>();
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            Coordinate[] holeCoords = CoordinateUtils.removeRepeatedCoordinates(polygon.getInteriorRingN(i).getCoordinates(), tolerance, true);
            if (holeCoords.length < 4) {
                throw new SpatialException("Not enough coordinates to build a new LinearRing.\n Please adjust the tolerance");
            }
            holes.add(gf.createLinearRing(holeCoords));
        }
        return gf.createPolygon(shell, GeometryFactory.toLinearRingArray(holes));
    }

    /**
     * Removes duplicated coordinates within a MultiPolygon.
     *
     * @param multiPolygon
     * @param tolerance    to delete the coordinates
     * @return
     */
    public static MultiPolygon removeDuplicateCoordinates(MultiPolygon multiPolygon, double tolerance) {
        ArrayList<Polygon> polys = new ArrayList<Polygon>();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon poly = (Polygon) multiPolygon.getGeometryN(i);
            polys.add(removeDuplicateCoordinates(poly, tolerance));
        }
        return GeometryFactories.default_().createMultiPolygon(GeometryFactory.toPolygonArray(polys));
    }

    /**
     * Removes duplicated coordinates within a GeometryCollection
     *
     * @param geometryCollection
     * @param tolerance          to delete the coordinates
     * @return
     */
    public static GeometryCollection removeDuplicateCoordinates(GeometryCollection geometryCollection, double tolerance) {
        ArrayList<Geometry> geoms = new ArrayList<>();
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            Geometry geom = geometryCollection.getGeometryN(i);
            geoms.add(removeDuplicateCoordinates(geom, tolerance));
        }
        return GeometryFactories.default_().createGeometryCollection(GeometryFactory.toGeometryArray(geoms));
    }
}
