package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if the geometry A is within the geometry B
 */
public class ST_Within {

    /**
     * @param a Surface Geometry.
     * @param b Geometry instance
     * @return true if the geometry A is within the geometry B
     */
    public static Boolean isWithin(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.within(b);
    }
}
