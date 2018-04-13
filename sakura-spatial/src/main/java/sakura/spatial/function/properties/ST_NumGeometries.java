package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get the number of geometries inside a geometry collection
 */
public class ST_NumGeometries {

    /**
     * @param geometry Geometry instance or null
     * @return Number of geometries or null if Geometry is null.
     */
    public static Integer getNumGeometries(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getNumGeometries();
    }
}
