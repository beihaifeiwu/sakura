package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.github.beihaifeiwu.sakura.spatial.utils.CoordinateUtils;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.*;

import java.util.ArrayList;

/**
 * Returns a version of the given geometry without duplicated coordinates.
 */
public class ST_RemoveDuplicatedCoordinates {

    /**
     * Returns a version of the given geometry with duplicated coordinates removed.
     *
     * @param geometry
     * @return
     */
    public static Geometry removeDuplicatedCoordinates(Geometry geometry) {
        return removeCoordinates(geometry);
    }

    /**
     * Removes duplicated coordinates within a geometry.
     *
     * @param geom
     * @return
     */
    public static Geometry removeCoordinates(Geometry geom) {
        if (geom == null) {
            return null;
        } else if (geom.isEmpty()) {
            return geom;
        } else if (geom instanceof Point) {
            return geom;
        } else if (geom instanceof MultiPoint) {
            return removeCoordinates((MultiPoint) geom);
        } else if (geom instanceof LineString) {
            return removeCoordinates((LineString) geom);
        } else if (geom instanceof MultiLineString) {
            return removeCoordinates((MultiLineString) geom);
        } else if (geom instanceof Polygon) {
            return removeCoordinates((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return removeCoordinates((MultiPolygon) geom);
        } else if (geom instanceof GeometryCollection) {
            return removeCoordinates((GeometryCollection) geom);
        }
        return null;
    }


    /**
     * Removes duplicated coordinates within a MultiPoint.
     *
     * @param g
     * @return
     */
    public static MultiPoint removeCoordinates(MultiPoint g) {
        Coordinate[] coords = CoordinateUtils.removeDuplicatedCoordinates(g.getCoordinates(), false);
        return GeometryFactories.default_().createMultiPoint(coords);
    }

    /**
     * Removes duplicated coordinates within a LineString.
     *
     * @param g
     * @return
     */
    public static LineString removeCoordinates(LineString g) {
        Coordinate[] coords = CoordinateUtils.removeDuplicatedCoordinates(g.getCoordinates(), false);
        return GeometryFactories.default_().createLineString(coords);
    }

    /**
     * Removes duplicated coordinates within a linearRing.
     *
     * @param g
     * @return
     */
    public static LinearRing removeCoordinates(LinearRing g) {
        Coordinate[] coords = CoordinateUtils.removeDuplicatedCoordinates(g.getCoordinates(), false);
        return GeometryFactories.default_().createLinearRing(coords);
    }

    /**
     * Removes duplicated coordinates in a MultiLineString.
     *
     * @param g
     * @return
     */
    public static MultiLineString removeCoordinates(MultiLineString g) {
        ArrayList<LineString> lines = new ArrayList<LineString>();
        for (int i = 0; i < g.getNumGeometries(); i++) {
            LineString line = (LineString) g.getGeometryN(i);
            lines.add(removeCoordinates(line));
        }
        return GeometryFactories.default_().createMultiLineString(GeometryFactory.toLineStringArray(lines));
    }

    /**
     * Removes duplicated coordinates within a Polygon.
     *
     * @param poly
     * @return
     */
    public static Polygon removeCoordinates(Polygon poly) {
        GeometryFactory gf = GeometryFactories.default_();
        Coordinate[] shellCoords = CoordinateUtils.removeDuplicatedCoordinates(poly.getExteriorRing().getCoordinates(), true);
        LinearRing shell = gf.createLinearRing(shellCoords);
        ArrayList<LinearRing> holes = new ArrayList<LinearRing>();
        for (int i = 0; i < poly.getNumInteriorRing(); i++) {
            Coordinate[] holeCoords = CoordinateUtils.removeDuplicatedCoordinates(poly.getInteriorRingN(i).getCoordinates(), true);
            holes.add(gf.createLinearRing(holeCoords));
        }
        return gf.createPolygon(shell, GeometryFactory.toLinearRingArray(holes));
    }

    /**
     * Removes duplicated coordinates within a MultiPolygon.
     *
     * @param g
     * @return
     */
    public static MultiPolygon removeCoordinates(MultiPolygon g) {
        ArrayList<Polygon> polys = new ArrayList<Polygon>();
        for (int i = 0; i < g.getNumGeometries(); i++) {
            Polygon poly = (Polygon) g.getGeometryN(i);
            polys.add(removeCoordinates(poly));
        }
        return GeometryFactories.default_().createMultiPolygon(GeometryFactory.toPolygonArray(polys));
    }

    /**
     * Removes duplicated coordinates within a GeometryCollection
     *
     * @param g
     * @return
     */
    public static GeometryCollection removeCoordinates(GeometryCollection g) {
        ArrayList<Geometry> geoms = new ArrayList<Geometry>();
        for (int i = 0; i < g.getNumGeometries(); i++) {
            Geometry geom = g.getGeometryN(i);
            geoms.add(removeCoordinates(geom));
        }
        return GeometryFactories.default_().createGeometryCollection(GeometryFactory.toGeometryArray(geoms));
    }
}
