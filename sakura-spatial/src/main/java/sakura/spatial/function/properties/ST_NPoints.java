package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;


/**
 * Return the number of points (vertexes) in a geometry.
 */
public class ST_NPoints {

    /**
     * @param geometry Geometry instance or null
     * @return Number of points or null if Geometry is null.
     */
    public static Integer getNPoints(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getNumPoints();
    }

}
