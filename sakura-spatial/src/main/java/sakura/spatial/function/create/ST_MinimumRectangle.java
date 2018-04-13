package sakura.spatial.function.create;

import com.vividsolutions.jts.algorithm.MinimumDiameter;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Gets the minimum rectangular POLYGON which encloses the input geometry. See
 * {@link MinimumDiameter}.
 */
public class ST_MinimumRectangle {

    /**
     * Gets the minimum rectangular {@link Polygon} which encloses the input geometry.
     *
     * @param geometry Input geometry
     * @return
     */
    public static Geometry computeMinimumRectangle(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return new MinimumDiameter(geometry).getMinimumRectangle();
    }
}
