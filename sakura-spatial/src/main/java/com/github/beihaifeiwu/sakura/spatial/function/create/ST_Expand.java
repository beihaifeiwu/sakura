package com.github.beihaifeiwu.sakura.spatial.function.create;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Expands a geometry's envelope by the given delta X and delta Y.
 */
public class ST_Expand {

    /**
     * Expands a geometry's envelope by the given delta X and delta Y. Both
     * positive and negative distances are supported.
     *
     * @param geometry the input geometry
     * @param delta    the distance to expand the envelope along the X and Y axis
     * @return the expanded geometry
     */
    public static Geometry expand(Geometry geometry, double delta) {
        return expand(geometry, delta, delta);
    }

    /**
     * Expands a geometry's envelope by the given delta X and delta Y. Both
     * positive and negative distances are supported.
     *
     * @param geometry the input geometry
     * @param deltaX   the distance to expand the envelope along the X axis
     * @param deltaY   the distance to expand the envelope along the Y axis
     * @return the expanded geometry
     */
    public static Geometry expand(Geometry geometry, double deltaX, double deltaY) {
        if (geometry == null) {
            return null;
        }
        Envelope env = geometry.getEnvelopeInternal();
        // As the time of writing Envelope.expand is buggy with negative parameters
        double minX = env.getMinX() - deltaX;
        double maxX = env.getMaxX() + deltaX;
        double minY = env.getMinY() - deltaY;
        double maxY = env.getMaxY() + deltaY;
        Envelope expandedEnvelope = new Envelope(minX < maxX ? minX : (env.getMaxX() - env.getMinX()) / 2 + env.getMinX(),
                minX < maxX ? maxX : (env.getMaxX() - env.getMinX()) / 2 + env.getMinX(),
                minY < maxY ? minY : (env.getMaxY() - env.getMinY()) / 2 + env.getMinY(),
                minY < maxY ? maxY : (env.getMaxY() - env.getMinY()) / 2 + env.getMinY());
        return geometry.getFactory().toGeometry(expandedEnvelope);
    }
}
