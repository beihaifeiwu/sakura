package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if the geometry A intersects the geometry B
 */
public class ST_Intersects {

    /**
     * @param surface      Surface Geometry.
     * @param testGeometry Geometry instance
     * @return true if the geometry A intersects the geometry B
     */
    public static Boolean isIntersects(Geometry surface, Geometry testGeometry) {
        if (surface == null) {
            return null;
        }
        if (testGeometry == null) {
            return false;
        }
        return surface.intersects(testGeometry);
    }
}
