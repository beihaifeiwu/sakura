package sakura.spatial.function.operators;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;

/**
 * Compute the union of two or more Geometries.
 */
public class ST_Union {

    /**
     * @param a Geometry instance.
     * @param b Geometry instance
     * @return union of Geometries a and b
     */
    public static Geometry union(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.union(b);
    }

    /**
     * @param geomList Geometry list
     * @return union of all Geometries in geomList
     */
    public static Geometry union(Geometry geomList) {
        return UnaryUnionOp.union(geomList);
    }
}
