package com.github.beihaifeiwu.sakura.spatial.function.buffer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.noding.SegmentString;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.buffer.OffsetCurveBuilder;
import com.vividsolutions.jts.operation.buffer.OffsetCurveSetBuilder;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Return an offset line at a given distance and side from an input geometry.
 * <p>
 * The optional third parameter can either specify number of segments used to
 * approximate a quarter circle(integer case, defaults to 8)
 * or a list of blank-separated key=value pairs (string case) to manage line style
 * parameters :'quad_segs=8' endcap=round|flat|square' 'join=round|mitre|bevel' 'mitre_limit=5'
 */
@UtilityClass
public class ST_OffSetCurve {

    /**
     * Return an offset line at a given distance and side from an input geometry
     *
     * @param geometry   the geometry
     * @param offset     the distance
     * @param parameters the buffer parameters
     * @return
     */
    public static Geometry offsetCurve(Geometry geometry, double offset, String parameters) {
        if (geometry == null) {
            return null;
        }
        String[] buffParameters = parameters.split("\\s+");
        BufferParameters bufferParameters = new BufferParameters();
        for (String params : buffParameters) {
            String[] keyValue = params.split("=");
            if ("endcap".equalsIgnoreCase(keyValue[0])) {
                String param = keyValue[1];
                if ("round".equalsIgnoreCase(param)) {
                    bufferParameters.setEndCapStyle(BufferParameters.CAP_ROUND);
                } else if ("flat".equalsIgnoreCase(param) || "butt".equalsIgnoreCase(param)) {
                    bufferParameters.setEndCapStyle(BufferParameters.CAP_FLAT);
                } else if ("square".equalsIgnoreCase(param)) {
                    bufferParameters.setEndCapStyle(BufferParameters.CAP_SQUARE);
                } else {
                    throw new IllegalArgumentException("Supported join values are round, flat, butt or square.");
                }
            } else if ("join".equalsIgnoreCase(keyValue[0])) {
                String param = keyValue[1];
                if ("bevel".equalsIgnoreCase(param)) {
                    bufferParameters.setJoinStyle(BufferParameters.JOIN_BEVEL);
                } else if ("mitre".equalsIgnoreCase(param) || "miter".equalsIgnoreCase(param)) {
                    bufferParameters.setJoinStyle(BufferParameters.JOIN_MITRE);
                } else if ("round".equalsIgnoreCase(param)) {
                    bufferParameters.setJoinStyle(BufferParameters.JOIN_ROUND);
                } else {
                    throw new IllegalArgumentException("Supported join values are bevel, mitre, miter or round.");
                }
            } else if ("mitre_limit".equalsIgnoreCase(keyValue[0]) || "miter_limit".equalsIgnoreCase(keyValue[0])) {
                bufferParameters.setMitreLimit(Double.valueOf(keyValue[1]));
            } else if ("quad_segs".equalsIgnoreCase(keyValue[0])) {
                bufferParameters.setQuadrantSegments(Integer.valueOf(keyValue[1]));
            } else {
                throw new IllegalArgumentException("Unknown parameters. Please read the documentation.");
            }
        }
        return computeOffsetCurve(geometry, offset, bufferParameters);
    }

    /**
     * Return an offset line at a given distance and side from an input geometry
     * without buffer parameters
     *
     * @param geometry the geometry
     * @param offset   the distance
     * @return
     */
    public static Geometry offsetCurve(Geometry geometry, double offset) {
        return computeOffsetCurve(geometry, offset, new BufferParameters());
    }

    /**
     * Method to compute the offset line
     *
     * @param geometry
     * @param offset
     * @param bufferParameters
     * @return
     */
    public static Geometry computeOffsetCurve(Geometry geometry, double offset, BufferParameters bufferParameters) {
        ArrayList<LineString> lineStrings = new ArrayList<LineString>();
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if (subGeom.getDimension() == 1) {
                lineStringOffSetCurve(lineStrings, (LineString) subGeom, offset, bufferParameters);
            } else {
                geometryOffSetCurve(lineStrings, subGeom, offset, bufferParameters);
            }
        }
        if (!lineStrings.isEmpty()) {
            if (lineStrings.size() == 1) {
                return lineStrings.get(0);
            } else {
                return geometry.getFactory().createMultiLineString(lineStrings.toArray(new LineString[lineStrings.size()]));
            }
        }
        return null;
    }

    /**
     * Compute the offset curve for a linestring
     *
     * @param list
     * @param lineString
     * @param offset
     * @param bufferParameters
     */
    public static void lineStringOffSetCurve(List<LineString> list, LineString lineString,
                                             double offset, BufferParameters bufferParameters) {
        Coordinate[] coordinates = new OffsetCurveBuilder(lineString.getPrecisionModel(), bufferParameters)
                .getOffsetCurve(lineString.getCoordinates(), offset);
        list.add(lineString.getFactory().createLineString(coordinates));
    }

    /**
     * Compute the offset curve for a polygon, a point or a collection of geometries
     *
     * @param list
     * @param geometry
     * @param offset
     * @param bufferParameters
     */
    @SuppressWarnings("unchecked")
    public static void geometryOffSetCurve(List<LineString> list, Geometry geometry,
                                           double offset, BufferParameters bufferParameters) {
        final List curves = new OffsetCurveSetBuilder(geometry, offset,
                new OffsetCurveBuilder(geometry.getFactory().getPrecisionModel(), bufferParameters))
                .getCurves();
        for (SegmentString curve : (Iterable<SegmentString>) curves) {
            list.add(geometry.getFactory().createLineString(curve.getCoordinates()));
        }
    }

}
