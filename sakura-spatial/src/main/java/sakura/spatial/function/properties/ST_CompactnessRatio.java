package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * ST_CompactnessRatio computes the perimeter of a circle whose area is equal to the
 * given polygon's area, and returns the ratio of this computed perimeter to the given
 * polygon's perimeter.
 * <p/>
 * Equivalent definition: ST_CompactnessRatio returns the square root of the
 * polygon's area divided by the area of the circle with circumference equal to
 * the polygon's perimeter.
 * <p/>
 * Note: This uses the 2D perimeter/area of the polygon.
 */
public class ST_CompactnessRatio {

    /**
     * Returns the compactness ratio of the given polygon
     *
     * @param geom Geometry
     * @return The compactness ratio of the given polygon
     */
    public static Double computeCompacity(Geometry geom) {
        if (geom == null) {
            return null;
        }
        if (geom instanceof Polygon) {
            final double circleRadius = Math.sqrt(geom.getArea() / Math.PI);
            final double circleCurcumference = 2 * Math.PI * circleRadius;
            return circleCurcumference / geom.getLength();
        }
        return null;
    }
}
