package sakura.spatial.function.operators;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Compute the symmetric difference between two Geometries.
 */
public class ST_SymDifference {

    /**
     * @param a Geometry instance.
     * @param b Geometry instance
     * @return the symmetric difference between two geometries
     */
    public static Geometry symDifference(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.symDifference(b);
    }
}
