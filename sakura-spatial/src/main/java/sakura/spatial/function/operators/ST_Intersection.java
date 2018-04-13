package sakura.spatial.function.operators;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Compute the intersection of two Geometries.
 */
public class ST_Intersection {

    /**
     * @param a Geometry instance.
     * @param b Geometry instance
     * @return the intersection between two geometries
     */
    public static Geometry intersection(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.intersection(b);
    }
}
