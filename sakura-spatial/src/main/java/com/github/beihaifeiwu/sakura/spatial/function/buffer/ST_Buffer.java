package com.github.beihaifeiwu.sakura.spatial.function.buffer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import lombok.experimental.UtilityClass;

/**
 * ST_Buffer computes a buffer around a Geometry.  Circular arcs are
 * approximated using 8 segments per quadrant. In particular, circles contain
 * 32 line segments.
 */
@UtilityClass
public class ST_Buffer {

    /**
     * @param geom     Geometry instance
     * @param distance Buffer width in projection unit
     * @return geom buffer around geom geometry.
     */
    public static Geometry buffer(Geometry geom, Double distance) {
        if (geom == null || distance == null) {
            return null;
        }
        return geom.buffer(distance);
    }

    /**
     * @param geom     Geometry instance
     * @param distance Buffer width in projection unit
     * @param value    Int or String end type
     * @return a buffer around a geometry.
     */
    public static Geometry buffer(Geometry geom, Double distance, Object value) throws IllegalArgumentException {
        if (geom == null) {
            return null;
        }
        if (value instanceof String) {
            String[] buffParameters = ((String) value).split("\\s+");
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
            BufferOp bufOp = new BufferOp(geom, bufferParameters);
            return bufOp.getResultGeometry(distance);
        } else if (value instanceof Integer) {
            BufferOp bufOp = new BufferOp(geom, new BufferParameters((Integer) value));
            return bufOp.getResultGeometry(distance);
        } else {
            throw new IllegalArgumentException("The third argument must be an int or a String.");
        }
    }
}
