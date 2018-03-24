package com.github.beihaifeiwu.sakura.spatial.function.distance;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Polygon;

import java.util.HashSet;
import java.util.Set;

/**
 * ST_LocateAlong returns a MULTIPOINT containing points along the line
 * segments of the given geometry matching the specified segment length
 * fraction and offset distance. A positive offset places the point to the left
 * of the segment (with the ordering given by Coordinate traversal); a negative
 * offset to the right. For areal elements, only exterior rings are supported.
 */
public class ST_LocateAlong {

    /**
     * Returns a MULTIPOINT containing points along the line segments of the
     * given geometry matching the specified segment length fraction and offset
     * distance.
     *
     * @param geom                  Geometry
     * @param segmentLengthFraction Segment length fraction
     * @param offsetDistance        Offset distance
     * @return A MULTIPOINT containing points along the line segments of the
     * given geometry matching the specified segment length fraction and offset
     * distance
     */
    public static MultiPoint locateAlong(Geometry geom,
                                         double segmentLengthFraction,
                                         double offsetDistance) {
        if (geom == null) {
            return null;
        }
        if (geom.getDimension() == 0) {
            return null;
        }
        Set<Coordinate> result = new HashSet<Coordinate>();
        for (int i = 0; i < geom.getNumGeometries(); i++) {
            Geometry subGeom = geom.getGeometryN(i);
            if (subGeom instanceof Polygon) {
                // Ignore hole
                result.addAll(computePoints(((Polygon) subGeom).getExteriorRing().getCoordinates(),
                        segmentLengthFraction, offsetDistance));
            } else if (subGeom instanceof LineString) {
                result.addAll(computePoints(subGeom.getCoordinates(),
                        segmentLengthFraction, offsetDistance));
            }
        }
        return geom.getFactory().createMultiPoint(
                result.toArray(new Coordinate[result.size()]));
    }

    private static Set<Coordinate> computePoints(Coordinate[] coords,
                                                 double segmentLengthFraction,
                                                 double offsetDistance) {
        Set<Coordinate> pointsToAdd = new HashSet<Coordinate>();
        for (int j = 0; j < coords.length - 1; j++) {
            LineSegment segment = new LineSegment(coords[j], coords[j + 1]);
            pointsToAdd.add(segment.pointAlongOffset(segmentLengthFraction, offsetDistance));
        }
        return pointsToAdd;
    }
}
