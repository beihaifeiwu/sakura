package sakura.spatial.function.topography;

import com.vividsolutions.jts.geom.Geometry;
import sakura.spatial.utils.TriMarkers;

/**
 * This function is used to compute the slope direction of a triangle.
 */
public class ST_TriangleSlope {

    /**
     * @param geometry Triangle
     * @return slope of a triangle expressed in percents
     * @throws IllegalArgumentException Accept only triangles
     */
    public static Double computeSlope(Geometry geometry) throws IllegalArgumentException {
        if (geometry == null) {
            return null;
        }
        return TriMarkers.getSlopeInPercent(
                TriMarkers.getNormalVector(TINFeatureFactory.createTriangle(geometry)), TINFeatureFactory.EPSILON);
    }

}
