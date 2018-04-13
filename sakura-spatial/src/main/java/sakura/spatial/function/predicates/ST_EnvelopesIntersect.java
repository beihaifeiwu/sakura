package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if the envelope of Geometry A intersects the envelope of
 * Geometry B.
 */
public class ST_EnvelopesIntersect {

    /**
     * @param surface      Surface Geometry.
     * @param testGeometry Geometry instance
     * @return true if the envelope of Geometry A intersects the envelope of
     * Geometry B
     */
    public static Boolean intersects(Geometry surface, Geometry testGeometry) {
        if (surface == null && testGeometry == null) {
            return null;
        }
        return !(testGeometry == null || surface == null)
                && surface.getEnvelopeInternal().intersects(testGeometry.getEnvelopeInternal());
    }
}
