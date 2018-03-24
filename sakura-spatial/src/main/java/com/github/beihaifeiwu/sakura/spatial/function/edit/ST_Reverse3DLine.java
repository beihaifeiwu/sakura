package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequences;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Returns a 1 dimension geometry with vertex order reversed according the start
 * and the end z values.
 */
public class ST_Reverse3DLine {

    /**
     * Returns a 1 dimension geometry with vertex order reversed using the ascending
     * value.
     *
     * @param geometry
     * @return
     */
    public static Geometry reverse3DLine(Geometry geometry) {
        return reverse3DLine(geometry, "asc");
    }

    /**
     * Returns a 1 dimension geometry with vertex order reversed according values
     * ascending (asc) or descending (desc)
     *
     * @param geometry
     * @param order
     * @return
     */
    public static Geometry reverse3DLine(Geometry geometry, String order) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof LineString) {
            return reverse3D((LineString) geometry, order);
        } else if (geometry instanceof MultiLineString) {
            return reverse3D((MultiLineString) geometry, order);
        }
        return null;
    }

    /**
     * Reverses a LineString according to the z value. The z of the first point
     * must be lower than the z of the end point.
     *
     * @param lineString
     * @return
     */
    private static LineString reverse3D(LineString lineString, String order) {
        CoordinateSequence seq = lineString.getCoordinateSequence();
        double startZ = seq.getCoordinate(0).z;
        double endZ = seq.getCoordinate(seq.size() - 1).z;
        if ("desc".equalsIgnoreCase(order)) {
            if (!Double.isNaN(startZ) && !Double.isNaN(endZ) && startZ < endZ) {
                CoordinateSequences.reverse(seq);
                return GeometryFactories.default_().createLineString(seq);
            }
        } else if ("asc".equalsIgnoreCase(order)) {
            if (!Double.isNaN(startZ) && !Double.isNaN(endZ) && startZ > endZ) {
                CoordinateSequences.reverse(seq);
                return GeometryFactories.default_().createLineString(seq);
            }
        } else {
            throw new IllegalArgumentException("Supported order values are asc or desc.");
        }
        return lineString;
    }

    /**
     * Reverses a multilinestring according to z value. If asc : the z first
     * point must be lower than the z end point if desc : the z first point must
     * be greater than the z end point
     *
     * @param multiLineString
     * @return
     */
    public static MultiLineString reverse3D(MultiLineString multiLineString, String order) {
        int num = multiLineString.getNumGeometries();
        LineString[] lineStrings = new LineString[num];
        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            lineStrings[i] = reverse3D((LineString) multiLineString.getGeometryN(i), order);

        }
        return GeometryFactories.default_().createMultiLineString(lineStrings);
    }
}
