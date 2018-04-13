package sakura.spatial.function.create;

import com.vividsolutions.jts.algorithm.MinimumBoundingCircle;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Compute the minimum bounding circle of a geometry. For more information,
 * see {@link MinimumBoundingCircle}.
 */
public class ST_BoundingCircle {


    /**
     * Computes the bounding circle
     *
     * @param geometry
     * @return
     */
    public static Geometry computeBoundingCircle(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return new MinimumBoundingCircle(geometry).getCircle();
    }
}
