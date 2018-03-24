package com.github.beihaifeiwu.sakura.spatial.function.buffer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import lombok.experimental.UtilityClass;

/**
 * Compute a single buffer on one side.
 * <p>
 * Return a buffer at a given distance on only one side of each input lines of the geometry.
 * The optional third parameter can either specify number of segments used to approximate
 * a quarter circle (integer case, defaults to 8) or a list of blank-separated key=value pairs (string case)
 * to manage line style parameters : 'quad_segs=8' 'join=round|mitre|bevel' 'mitre_limit=5'
 * The end cap style for single-sided buffers is always ignored, and forced to the equivalent of flat.
 */
@UtilityClass
public class ST_SideBuffer {

    /**
     * Compute a single side buffer with default parameters
     *
     * @param geometry
     * @param distance
     * @return
     */
    public static Geometry singleSideBuffer(Geometry geometry, double distance) {
        if (geometry == null) {
            return null;
        }
        return computeSingleSideBuffer(geometry, distance, new BufferParameters());
    }

    /**
     * Compute a single side buffer with join and mitre parameters
     * Note :
     * The End Cap Style for single-sided buffers is always ignored, and forced to the equivalent of flat.
     *
     * @param geometry
     * @param distance
     * @param parameters
     * @return
     */
    public static Geometry singleSideBuffer(Geometry geometry, double distance, String parameters) {
        if (geometry == null) {
            return null;
        }
        String[] buffParemeters = parameters.split("\\s+");
        BufferParameters bufferParameters = new BufferParameters();
        for (String params : buffParemeters) {
            String[] keyValue = params.split("=");
            if ("join".equalsIgnoreCase(keyValue[0])) {
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
        return computeSingleSideBuffer(geometry, distance, bufferParameters);
    }

    /**
     * Compute the buffer
     *
     * @param geometry
     * @param distance
     * @param bufferParameters
     * @return
     */
    private static Geometry computeSingleSideBuffer(Geometry geometry, double distance, BufferParameters bufferParameters) {
        bufferParameters.setSingleSided(true);
        return BufferOp.bufferOp(geometry, distance, bufferParameters);
    }
}
