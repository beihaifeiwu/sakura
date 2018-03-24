package com.github.beihaifeiwu.sakura.spatial.function.buffer;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.function.create.ST_RingBuffer;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import lombok.experimental.UtilityClass;


/**
 * Compute a ring buffer on one side
 * <p>
 * Return a ring buffer at a given distance on only one side of each input lines of the geometry.
 * Avalaible arguments are :
 * (1) the geometry,
 * (2) the size of each ring,
 * (3) the number of rings,
 * (4) optional - a list of blank-separated key=value pairs (string case) iso used t manage line style parameters.
 * The end cap style for single-sided buffers is always ignored, and forced to the equivalent of flat.Please read the ST_Buffer documention.
 * (5) optional - createHole True if you want to keep only difference between buffers Default is true.
 * Note : Holes are not supported by this function.
 */
@UtilityClass
public class ST_RingSideBuffer {

    /**
     * Compute a ring buffer on one side of the geometry
     *
     * @param geom
     * @param bufferSize
     * @param numBuffer
     * @return
     */
    public static Geometry ringSideBuffer(Geometry geom, double bufferSize, int numBuffer) {
        return ringSideBuffer(geom, bufferSize, numBuffer, "endcap=flat");
    }

    /**
     * @param geom
     * @param bufferDistance
     * @param numBuffer
     * @param parameters
     * @return
     */
    public static Geometry ringSideBuffer(Geometry geom, double bufferDistance,
                                          int numBuffer, String parameters) {
        return ringSideBuffer(geom, bufferDistance, numBuffer, parameters, true);
    }

    /**
     * Compute a ring buffer on one side of the geometry
     *
     * @param geom
     * @param bufferDistance
     * @param numBuffer
     * @param parameters
     * @param doDifference
     * @return
     */
    public static Geometry ringSideBuffer(Geometry geom, double bufferDistance,
                                          int numBuffer, String parameters, boolean doDifference) {
        if (geom == null) {
            return null;
        }
        if (geom.getNumGeometries() > 1) {
            throw new SpatialException("This function supports only single geometry : point, linestring or polygon.");
        } else {
            String[] buffParemeters = parameters.split("\\s+");
            BufferParameters bufferParameters = new BufferParameters();
            bufferParameters.setSingleSided(true);
            for (String params : buffParemeters) {
                String[] keyValue = params.split("=");
                if ("endcap".equalsIgnoreCase(keyValue[0])) {
                    String param = keyValue[1];
                    if ("round".equalsIgnoreCase(param)) {
                        bufferParameters.setEndCapStyle(BufferParameters.CAP_FLAT);
                    } else if ("square".equalsIgnoreCase(param)) {
                        bufferParameters.setEndCapStyle(BufferParameters.CAP_FLAT);
                    } else if ("flat".equalsIgnoreCase(param)) {
                        bufferParameters.setEndCapStyle(BufferParameters.CAP_FLAT);
                    } else {
                        throw new IllegalArgumentException("Supported join values are round or square.");
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

            if (bufferDistance > 0) {
                return ST_RingBuffer.computePositiveRingBuffer(geom, bufferDistance, numBuffer, bufferParameters, doDifference);
            } else if (bufferDistance < 0) {
                if (geom instanceof Point) {
                    throw new SpatialException("Cannot compute a negative ring side buffer on a point.");
                } else {
                    return ST_RingBuffer.computeNegativeRingBuffer(geom, bufferDistance, numBuffer, bufferParameters, doDifference);
                }
            } else {
                return geom;
            }
        }
    }
}
