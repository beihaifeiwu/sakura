package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if Geometry A contains Geometry B.
 */
public class ST_Contains {

    /**
     * @param surface      Surface Geometry.
     * @param testGeometry Geometry instance
     * @return True only if no points of testGeometry lie outside of surface
     */
    public static Boolean isContains(Geometry surface, Geometry testGeometry) {
        if (surface == null) {
            return null;
        }
        if (testGeometry == null) {
            return false;
        }
        return surface.contains(testGeometry);
    }
}
