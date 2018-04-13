package sakura.spatial.function.create;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import sakura.spatial.utils.GeometryFactories;

/**
 * ST_MakePoint constructs POINT from two or three doubles.
 */
public class ST_MakePoint {

    /**
     * Constructs POINT from two doubles.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return The POINT constructed from the given coordinatesk
     */
    public static Point createPoint(double x, double y) {
        return createPoint(x, y, Coordinate.NULL_ORDINATE);
    }

    /**
     * Constructs POINT from three doubles.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param z Z-coordinate
     * @return The POINT constructed from the given coordinates
     */
    public static Point createPoint(double x, double y, double z) {
        return GeometryFactories.default_().createPoint(new Coordinate(x, y, z));
    }
}
