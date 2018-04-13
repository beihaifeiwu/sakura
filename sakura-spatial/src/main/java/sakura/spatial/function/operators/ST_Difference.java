package sakura.spatial.function.operators;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Compute the difference between two Geometries.
 */
public class ST_Difference {

    /**
     * @param a Geometry instance.
     * @param b Geometry instance
     * @return the difference between two geometries
     */
    public static Geometry difference(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.difference(b);
    }
}
