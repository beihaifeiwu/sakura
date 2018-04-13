package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Computes the centroid of this Geometry. The centroid is equal to the centroid of the set of component Geometries of
 * highest dimension (since the lower-dimension geometries contribute zero "weight" to the centroid)
 */
public class ST_Centroid {

    public static Geometry getCentroid(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getCentroid();
    }
}
