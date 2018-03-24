package com.github.beihaifeiwu.sakura.spatial.function.split;

import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.noding.IntersectionAdder;
import com.vividsolutions.jts.noding.MCIndexNoder;
import com.vividsolutions.jts.noding.NodedSegmentString;
import com.vividsolutions.jts.noding.SegmentString;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * LineIntersector is used to split an input geometry (LineString or MultiLineString) by
 * a set of geometries.
 */
@UtilityClass
public class ST_LineIntersector {

    private static final RobustLineIntersector ROBUST_INTERSECTOR = new RobustLineIntersector();

    /**
     * Split a lineal geometry by a another geometry
     *
     * @param inputLines
     * @param clipper
     * @return
     */
    public static Geometry lineIntersector(Geometry inputLines, Geometry clipper) {
        if (inputLines == null || clipper == null) {
            return null;
        }
        if (inputLines.getDimension() == 1) {
            MCIndexNoder mCIndexNoder = new MCIndexNoder();
            mCIndexNoder.setSegmentIntersector(new IntersectionAdder(ROBUST_INTERSECTOR));
            mCIndexNoder.computeNodes(getSegments(inputLines, clipper));
            Collection nodedSubstring = mCIndexNoder.getNodedSubstrings();
            GeometryFactory gf = inputLines.getFactory();
            ArrayList<LineString> linestrings = new ArrayList<LineString>(nodedSubstring.size());
            for (Iterator it = nodedSubstring.iterator(); it.hasNext(); ) {
                SegmentString segment = (SegmentString) it.next();
                //We keep only the segments of the input geometry
                if ((Integer) segment.getData() == 0) {
                    Coordinate[] cc = segment.getCoordinates();
                    cc = CoordinateArrays.atLeastNCoordinatesOrNothing(2, cc);
                    if (cc.length > 1) {
                        linestrings.add(gf.createLineString(cc));
                    }
                }
            }
            if (linestrings.isEmpty()) {
                return inputLines;
            } else {
                return gf.createMultiLineString(linestrings.toArray(new LineString[linestrings.size()]));
            }
        }
        throw new IllegalArgumentException("Split a " + inputLines.getGeometryType() + " by a " +
                clipper.getGeometryType() + " is not supported.");
    }

    /***
     * Convert the input geometries as a list of segments and mark them with a flag
     * to identify input and output geometries.
     * @param inputLines
     * @param clipper
     * @return
     */
    public static ArrayList<SegmentString> getSegments(Geometry inputLines, Geometry clipper) {
        ArrayList<SegmentString> segments = new ArrayList<SegmentString>();
        addGeometryToSegments(inputLines, 0, segments);
        addGeometryToSegments(clipper, 1, segments);
        return segments;
    }

    /**
     * Convert the a geometry as a list of segments and mark it with a flag
     *
     * @param geometry
     * @param flag
     * @param segments
     */
    public static void addGeometryToSegments(Geometry geometry, int flag, ArrayList<SegmentString> segments) {
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry component = geometry.getGeometryN(i);
            if (component instanceof Polygon) {
                add((Polygon) component, flag, segments);
            } else if (component instanceof LineString) {
                add((LineString) component, flag, segments);
            }
        }
    }

    /**
     * Convert a polygon as a list of segments and mark it with a flag
     *
     * @param poly
     * @param flag
     * @param segments
     */
    private static void add(Polygon poly, int flag, ArrayList<SegmentString> segments) {
        add(poly.getExteriorRing(), flag, segments);
        for (int j = 0; j < poly.getNumInteriorRing(); j++) {
            add(poly.getInteriorRingN(j), flag, segments);
        }
    }

    /**
     * Convert a linestring as a list of segments and mark it with a flag
     *
     * @param line
     * @param flag
     * @param segments
     */
    private static void add(LineString line, int flag, ArrayList<SegmentString> segments) {
        SegmentString ss = new NodedSegmentString(line.getCoordinates(), flag);
        segments.add(ss);
    }
}
