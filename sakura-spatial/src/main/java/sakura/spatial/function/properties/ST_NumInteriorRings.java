package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Return the number of holes in a geometry
 */
public class ST_NumInteriorRings {

    /**
     * Return the number of holes in a Polygon or MultiPolygon
     *
     * @param g Geometry instance
     * @return Number of holes or null if geometry is null
     */
    public static Integer getHoles(Geometry g) {
        if (g == null) {
            return null;
        }
        if (g instanceof MultiPolygon) {
            Polygon p = (Polygon) g.getGeometryN(0);
            if (p != null) {
                return p.getNumInteriorRing();
            }
        } else if (g instanceof Polygon) {
            return ((Polygon) g).getNumInteriorRing();
        }
        return null;
    }
}
