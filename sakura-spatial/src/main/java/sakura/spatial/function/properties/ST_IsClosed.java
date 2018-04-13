package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Return TRUE if the provided geometry is a closed LINESTRING or
 * MULTILINESTRING, null otherwise.
 */
public class ST_IsClosed {

    /**
     * @param geometry Geometry
     * @return True if the provided geometry is a closed LINESTRING or
     * MULTILINESTRING, null otherwise
     */
    public static Boolean isClosed(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof MultiLineString) {
            return ((MultiLineString) geometry).isClosed();
        } else if (geometry instanceof LineString) {
            return ((LineString) geometry).isClosed();
        }
        return null;
    }
}
