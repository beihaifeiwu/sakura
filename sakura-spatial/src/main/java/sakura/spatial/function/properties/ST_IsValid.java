package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_IsValid returns true if the given geometry is valid.
 */
public class ST_IsValid {

    /**
     * Returns true if the given geometry is valid.
     *
     * @param geometry Geometry
     * @return True if the given geometry is valid
     */
    public static Boolean isValid(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.isValid();
    }
}
