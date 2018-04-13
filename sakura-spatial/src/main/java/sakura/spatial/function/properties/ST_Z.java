package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get the first Z coordinate
 */
public class ST_Z {

    /**
     * @param geometry Geometry instance
     * @return A z coordinate or null if null or empty geometry.
     */
    public static Double getZ(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getCoordinate().z;
    }
}
