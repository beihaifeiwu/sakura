package sakura.spatial.function.trigonometry;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;


/**
 * Returns the azimuth of the segment defined by the given Point geometries.
 * Return value is in radians.
 */
public class ST_Azimuth {

    /**
     * This code compute the angle in radian as postgis does.
     *
     * @param pointA
     * @param pointB
     * @return
     * @author :  Jose Martinez-Llario from JASPA. JAva SPAtial for SQL
     */
    public static Double azimuth(Geometry pointA, Geometry pointB) {
        if (pointA == null || pointB == null) {
            return null;
        }
        if ((pointA instanceof Point) && (pointB instanceof Point)) {
            Double angle;
            double x0 = ((Point) pointA).getX();
            double y0 = ((Point) pointA).getY();
            double x1 = ((Point) pointB).getX();
            double y1 = ((Point) pointB).getY();

            if (x0 == x1) {
                if (y0 < y1) {
                    angle = 0.0;
                } else if (y0 > y1) {
                    angle = Math.PI;
                } else {
                    angle = null;
                }
            } else if (y0 == y1) {
                if (x0 < x1) {
                    angle = Math.PI / 2;
                } else if (x0 > x1) {
                    angle = Math.PI + (Math.PI / 2);
                } else {
                    angle = null;
                }
            } else if (x0 < x1) {
                if (y0 < y1) {
                    angle = Math.atan(Math.abs(x0 - x1) / Math.abs(y0 - y1));
                } else { /* ( y0 > y1 ) - equality case handled above */
                    angle = Math.atan(Math.abs(y0 - y1) / Math.abs(x0 - x1)) + (Math.PI / 2);
                }
            } else { /* ( x0 > x1 ) - equality case handled above */
                if (y0 > y1) {
                    angle = Math.atan(Math.abs(x0 - x1) / Math.abs(y0 - y1)) + Math.PI;
                } else { /* ( y0 < y1 ) - equality case handled above */
                    angle = Math.atan(Math.abs(y0 - y1) / Math.abs(x0 - x1)) + (Math.PI + (Math.PI / 2));
                }
            }
            return angle;
        }
        return null;
    }
}
