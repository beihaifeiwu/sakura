package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import sakura.spatial.utils.CoordinateUtils;

/**
 * ST_ZMin returns the minimal z-value of the given geometry.
 */
public class ST_ZMin {

    /**
     * Returns the minimal z-value of the given geometry.
     *
     * @param geom Geometry
     * @return The minimal z-value of the given geometry, or null if the geometry is null.
     */
    public static Double getMinZ(Geometry geom) {
        if (geom != null) {
            return CoordinateUtils.zMinMax(geom.getCoordinates())[0];
        } else {
            return null;
        }
    }
}
