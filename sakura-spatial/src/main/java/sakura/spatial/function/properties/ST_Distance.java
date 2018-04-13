package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * For geometry type returns the 2-dimensional minimum Cartesian
 * distance between two geometries in projected units (spatial ref units)
 */
public class ST_Distance {

    /**
     * @param a Geometry instance or null
     * @param b Geometry instance or null
     * @return the 2-dimensional minimum Cartesian distance between two geometries
     * in projected units (spatial ref units)
     */
    public static Double distance(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.distance(b);
    }
}
