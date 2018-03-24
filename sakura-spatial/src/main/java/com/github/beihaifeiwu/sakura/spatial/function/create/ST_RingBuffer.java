package com.github.beihaifeiwu.sakura.spatial.function.create;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import lombok.experimental.UtilityClass;

/**
 * Compute a ring buffer around a geometry.
 *
 * @author Erwan Bocher
 */
@UtilityClass
public class ST_RingBuffer {

    /**
     * Compute a ring buffer around a geometry
     *
     * @param geom
     * @param bufferSize
     * @param numBuffer
     * @return
     */
    public static Geometry ringBuffer(Geometry geom, double bufferSize, int numBuffer) {
        return ringBuffer(geom, bufferSize, numBuffer, "endcap=round");
    }

    /**
     * @param geom
     * @param bufferDistance
     * @param numBuffer
     * @param parameters
     * @return
     */
    public static Geometry ringBuffer(Geometry geom, double bufferDistance,
                                      int numBuffer, String parameters) {
        return ringBuffer(geom, bufferDistance, numBuffer, parameters, true);
    }

    /**
     * Compute a ring buffer around a geometry
     *
     * @param geom
     * @param bufferDistance
     * @param numBuffer
     * @param parameters
     * @param doDifference
     * @return
     */
    public static Geometry ringBuffer(Geometry geom, double bufferDistance,
                                      int numBuffer, String parameters, boolean doDifference) {
        if (geom == null) {
            return null;
        }
        if (geom.getNumGeometries() > 1) {
            throw new SpatialException("This function supports only single geometry : point, linestring or polygon.");
        } else {
            String[] buffParemeters = parameters.split("\\s+");
            BufferParameters bufferParameters = new BufferParameters();
            for (String params : buffParemeters) {
                String[] keyValue = params.split("=");
                if ("endcap".equalsIgnoreCase(keyValue[0])) {
                    String param = keyValue[1];
                    if ("round".equalsIgnoreCase(param)) {
                        bufferParameters.setEndCapStyle(BufferParameters.CAP_ROUND);
                    } else if ("square".equalsIgnoreCase(param)) {
                        bufferParameters.setEndCapStyle(BufferParameters.CAP_SQUARE);
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
                return computePositiveRingBuffer(geom, bufferDistance, numBuffer, bufferParameters, doDifference);
            } else if (bufferDistance < 0) {
                if (geom instanceof Point) {
                    throw new SpatialException("Cannot compute a negative ring buffer on a point.");
                } else {
                    return computeNegativeRingBuffer(geom, bufferDistance, numBuffer, bufferParameters, doDifference);
                }
            } else {
                return geom;
            }
        }
    }

    /**
     * Compute a ring buffer with a positive offset
     *
     * @param geom
     * @param bufferDistance
     * @param numBuffer
     * @param bufferParameters
     * @param doDifference
     * @return
     */
    public static Geometry computePositiveRingBuffer(Geometry geom, double bufferDistance,
                                                     int numBuffer, BufferParameters bufferParameters, boolean doDifference) {
        Polygon[] buffers = new Polygon[numBuffer];
        if (geom instanceof Polygon) {
            //Work arround to manage polygon with hole
            geom = geom.getFactory().createPolygon(((Polygon) geom).getExteriorRing().getCoordinateSequence());
        }
        Geometry previous = geom;
        double distance = 0;
        for (int i = 0; i < numBuffer; i++) {
            distance += bufferDistance;
            Geometry newBuffer = runBuffer(geom, distance, bufferParameters);
            if (doDifference) {
                buffers[i] = (Polygon) newBuffer.difference(previous);
            } else {
                buffers[i] = (Polygon) newBuffer;
            }
            previous = newBuffer;
        }
        return geom.getFactory().createMultiPolygon(buffers);
    }

    /**
     * Compute a ring buffer with a negative offset
     *
     * @param geom
     * @param bufferDistance
     * @param numBuffer
     * @param bufferParameters
     * @param doDifference
     * @return
     */
    public static Geometry computeNegativeRingBuffer(Geometry geom, double bufferDistance,
                                                     int numBuffer, BufferParameters bufferParameters, boolean doDifference) {
        Polygon[] buffers = new Polygon[numBuffer];
        Geometry previous = geom;
        double distance = 0;
        if (geom instanceof Polygon) {
            geom = ((Polygon) geom).getExteriorRing();
            bufferParameters.setSingleSided(true);
        }
        for (int i = 0; i < numBuffer; i++) {
            distance += bufferDistance;
            Geometry newBuffer = runBuffer(geom, distance, bufferParameters);
            if (i == 0) {
                buffers[i] = (Polygon) newBuffer;
            } else {
                if (doDifference) {
                    buffers[i] = (Polygon) newBuffer.difference(previous);
                } else {
                    buffers[i] = (Polygon) newBuffer;
                }
            }
            previous = newBuffer;
        }
        return geom.getFactory().createMultiPolygon(buffers);
    }

    /**
     * Calculate the ring buffer
     *
     * @param geom
     * @param bufferSize
     * @param bufferParameters
     * @return
     */
    public static Geometry runBuffer(final Geometry geom, final double bufferSize,
                                     final BufferParameters bufferParameters) {
        return BufferOp.bufferOp(geom, bufferSize, bufferParameters);
    }
}
