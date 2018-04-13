package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Compute geometry area
 */
public class ST_Area {

    public static Double getArea(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getArea();
    }
}
