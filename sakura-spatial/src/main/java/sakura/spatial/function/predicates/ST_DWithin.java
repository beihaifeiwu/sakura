package sakura.spatial.function.predicates;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_DWithin returns true if the geometries are within the specified distance of one another.
 */
public class ST_DWithin {

    /**
     * Returns true if the geometries are within the specified distance of one another.
     *
     * @param geomA    Geometry A
     * @param geomB    Geometry B
     * @param distance Distance
     * @return True if if the geometries are within the specified distance of one another
     */
    public static Boolean isWithinDistance(Geometry geomA, Geometry geomB, Double distance) {
        if (geomA == null || geomB == null) {
            return null;
        }
        return geomA.isWithinDistance(geomB, distance);
    }
}
