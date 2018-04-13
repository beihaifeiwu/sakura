package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Returns a LinearRing instance or Null if parameter is not a Polygon.
 */
public class ST_ExteriorRing {

    /**
     * @param geometry Instance of Polygon
     * @return LinearRing instance or Null if parameter is not a Polygon.
     */
    public static Geometry getExteriorRing(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof Polygon) {
            return ((Polygon) geometry).getExteriorRing();
        } else {
            return null;
        }
    }
}
