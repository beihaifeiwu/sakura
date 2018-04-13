package sakura.spatial.function.distance;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Returns the 2-dimensional longest line between the points of two geometries.
 */
public class ST_LongestLine {

    /**
     * Return the longest line between the points of two geometries.
     *
     * @param geomA
     * @param geomB
     * @return
     */
    public static Geometry longestLine(Geometry geomA, Geometry geomB) {
        Coordinate[] coords = new MaxDistanceOp(geomA, geomB).getCoordinatesDistance();
        if (coords != null) {
            return geomA.getFactory().createLineString(coords);
        }
        return null;
    }
}
