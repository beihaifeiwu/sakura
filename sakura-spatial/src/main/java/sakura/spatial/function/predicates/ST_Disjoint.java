package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Return true if the two Geometries are disjoint.
 */
public class ST_Disjoint {

    /**
     * Return true if the two Geometries are disjoint
     *
     * @param a Geometry Geometry.
     * @param b Geometry instance
     * @return true if the two Geometries are disjoint
     */
    public static Boolean geomDisjoint(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.disjoint(b);
    }
}
