package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;


/**
 * This function is used to compute the relation between two geometries, as
 * described in the SFS specification. It can be used in two ways. First, if it is given two geometries,it returns a
 * 9-character String representation of the 2 geometries IntersectionMatrix. If it is given two geometries and a
 * IntersectionMatrix representation, it will return a boolean : true it the two geometries' IntersectionMatrix match
 * the given one, false otherwise.
 */
public class ST_Relate {

    /**
     * @param a Geometry Geometry.
     * @param b Geometry instance
     * @return 9-character String representation of the 2 geometries IntersectionMatrix
     */
    public static String relate(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return null;
        }
        return a.relate(b).toString();
    }

    /**
     * @param a       Geometry instance
     * @param b       Geometry instance
     * @param iMatrix IntersectionMatrix representation
     * @return true it the two geometries' IntersectionMatrix match the given one, false otherwise.
     */
    public static Boolean relate(Geometry a, Geometry b, String iMatrix) {
        if (a == null || b == null || iMatrix == null) {
            return null;
        }
        return a.relate(b, iMatrix);
    }
}
