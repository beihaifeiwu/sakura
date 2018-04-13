package sakura.spatial.function.topography;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.math.Vector2D;
import com.vividsolutions.jts.math.Vector3D;
import sakura.spatial.utils.TriMarkers;

/**
 * This function is used to compute the aspect of a triangle. Aspect represents
 * the main slope direction angle compared to the north direction.
 */
public class ST_TriangleAspect {

    /**
     * Compute the aspect in degree. The geometry must be a triangle.
     *
     * @param geometry Polygon triangle
     * @return aspect in degree
     * @throws IllegalArgumentException ST_TriangleAspect accept only triangles
     */
    public static Double computeAspect(Geometry geometry) throws IllegalArgumentException {
        if (geometry == null) {
            return null;
        }
        Vector3D vector = TriMarkers.getSteepestVector(
                TriMarkers.getNormalVector(TINFeatureFactory.createTriangle(geometry)), TINFeatureFactory.EPSILON);
        if (vector.length() < TINFeatureFactory.EPSILON) {
            return 0d;
        } else {
            Vector2D v = new Vector2D(vector.getX(), vector.getY());
            return measureFromNorth(Math.toDegrees(v.angle()));
        }
    }

    /**
     * Transforms an angle measured in degrees counterclockwise from the x-axis
     * (mathematicians) to an angle measured in degrees clockwise from the
     * y-axis (geographers).
     *
     * @param angle Mathematician's angle
     * @return Geographer's angle
     */
    public static double measureFromNorth(double angle) {
        return (450 - angle) % 360;
    }
}
