package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import sakura.spatial.utils.GeometryFactories;

/**
 * ST_ToMultiPoint constructs a MultiPoint from the given geometry's coordinates.
 */
public class ST_ToMultiPoint {

    /**
     * Constructs a MultiPoint from the given geometry's coordinates.
     *
     * @param geom Geometry
     * @return A MultiPoint constructed from the given geometry's coordinates
     */
    public static MultiPoint createMultiPoint(Geometry geom) {
        if (geom != null) {
            return GeometryFactories.default_().createMultiPoint(geom.getCoordinates());
        } else {
            return null;
        }
    }
}
