package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Converts this Geometry to normal form (canonical form)
 */
public class ST_Normalize {

    /**
     * Converts this Geometry to normal form (canonical form).
     *
     * @param geometry
     * @return
     */
    public static Geometry normalize(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        geometry.normalize();
        return geometry;
    }
}
