package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get a Point that lie on the surface of a Surface Geometry.
 * The returned point is always the same for the same geometry.
 */
public class ST_PointOnSurface {

    /**
     * @param geometry Valid Geometry instance
     * @return A Point that lie on the surface or null if input geometry is not a surface.
     */
    public static Geometry getInteriorPoint(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getInteriorPoint();
    }
}
