package sakura.spatial.function.earth;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import java.util.Date;

/**
 * Compute the sun position and return a new coordinate with x = azimuth and y = altitude
 */
public class ST_SunPosition {

    /**
     * Return the current sun position
     *
     * @param point
     * @return
     */
    public static Geometry sunPosition(Geometry point) {
        return sunPosition(point, new Date());
    }

    /**
     * Return the sun position for a given date
     *
     * @param point
     * @param date
     * @return
     * @throws IllegalArgumentException
     */
    public static Geometry sunPosition(Geometry point, Date date) throws IllegalArgumentException {
        if (point == null) {
            return null;
        }
        if (point instanceof Point) {
            Coordinate coord = point.getCoordinate();
            return point.getFactory().createPoint(SunCalc.getPosition(date, coord.y, coord.x));
        } else {
            throw new IllegalArgumentException("The sun position is computed according a point location.");
        }
    }
}
