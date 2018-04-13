package sakura.spatial.function.affine_transformations;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import lombok.experimental.UtilityClass;

/**
 * ST_Rotate rotates a geometry by a given angle (in radians) about the
 * geometry's center.
 */
@UtilityClass
public class ST_Rotate {

    /**
     * Rotates a geometry by a given angle (in radians) about the center
     * of the geometry's envelope.
     *
     * @param geom  Geometry
     * @param theta Angle
     * @return The geometry rotated about the center of its envelope
     */
    public static Geometry rotate(Geometry geom, double theta) {
        if (geom != null) {
            Coordinate center = geom.getEnvelopeInternal().centre();
            return rotate(geom, theta, center.x, center.y);
        } else {
            return null;
        }
    }

    /**
     * Rotates a geometry by a given angle (in radians) about the specified
     * point.
     *
     * @param geom  Geometry
     * @param theta Angle
     * @param point The point about which to rotate
     * @return The geometry rotated by theta about the given point
     */
    public static Geometry rotate(Geometry geom, double theta, Point point) {
        return rotate(geom, theta, point.getX(), point.getY());
    }

    /**
     * Rotates a geometry by a given angle (in radians) about the specified
     * point at (x0, y0).
     *
     * @param geom  Geometry
     * @param theta Angle
     * @param x0    x-coordinate of point about which to rotate
     * @param y0    y-coordinate of point about which to rotate
     * @return The geometry rotated by theta about (x0, y0)
     */
    public static Geometry rotate(Geometry geom, double theta, double x0, double y0) {
        if (geom != null) {
            return AffineTransformation.rotationInstance(theta, x0, y0).transform(geom);
        } else {
            return null;
        }
    }
}
