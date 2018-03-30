package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.*;

import java.util.LinkedList;
import java.util.List;

/**
 * ST_ToMultiSegments converts a geometry into a set of distinct segments
 * stored in a MultiLineString. Returns MULTILINESTRING EMPTY for geometries of
 * dimension 0.
 */
public class ST_ToMultiSegments {

    /**
     * Converts a geometry into a set of distinct segments stored in a
     * MultiLineString.
     *
     * @param geom Geometry
     * @return A MultiLineString of the geometry's distinct segments
     */
    public static MultiLineString createSegments(Geometry geom) {
        if (geom != null) {
            GeometryFactory gf = GeometryFactories.default_();
            List<LineString> result;
            if (geom.getDimension() > 0) {
                result = new LinkedList<>();
                createSegments(geom, result);
                return gf.createMultiLineString(
                        result.toArray(new LineString[result.size()]));
            } else {
                return gf.createMultiLineString(null);
            }
        }
        return null;
    }

    private static void createSegments(final Geometry geom,
                                       final List<LineString> result) {
        if (geom instanceof LineString) {
            createSegments((LineString) geom, result);
        } else if (geom instanceof Polygon) {
            createSegments((Polygon) geom, result);
        } else if (geom instanceof GeometryCollection) {
            createSegments((GeometryCollection) geom, result);
        }
    }

    public static void createSegments(final LineString geom,
                                      final List<LineString> result) {
        Coordinate[] coords = CoordinateArrays.removeRepeatedPoints(geom.getCoordinates());
        for (int j = 0; j < coords.length - 1; j++) {
            LineString lineString = GeometryFactories.default_().createLineString(
                    new Coordinate[]{coords[j], coords[j + 1]});
            result.add(lineString);
        }
    }

    private static void createSegments(final Polygon polygon,
                                       final List<LineString> result) {
        createSegments(polygon.getExteriorRing(), result);
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            createSegments(polygon.getInteriorRingN(i), result);
        }
    }

    private static void createSegments(final GeometryCollection geometryCollection,
                                       final List<LineString> result) {
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            createSegments(geometryCollection.getGeometryN(i), result);
        }
    }
}
