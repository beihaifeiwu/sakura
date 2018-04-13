package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if Geometry A is equal to Geometry B.
 */
public class ST_Equals {

    /**
     * Return true if Geometry A is equal to Geometry B
     *
     * @param a Geometry Geometry.
     * @param b Geometry instance
     * @return true if Geometry A is equal to Geometry B
     */
    public static Boolean geomEquals(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.equals(b);
    }
}
