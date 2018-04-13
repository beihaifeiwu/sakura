package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * ST_Perimeter returns the perimeter of a polygon or a multipolygon.
 * <p>
 * Returns the length measurement of the boundary of a Polygon or a MultiPolygon.
 * Distance units are those of the geometry spatial reference system.
 */
public class ST_Perimeter {

    /**
     * Compute the perimeter of a polygon or a multipolygon.
     *
     * @param geometry
     * @return
     */
    public static Double perimeter(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry.getDimension() < 2) {
            return 0d;
        }
        return computePerimeter(geometry);
    }

    /**
     * Compute the perimeter
     *
     * @param geometry
     * @return
     */
    private static double computePerimeter(Geometry geometry) {
        double sum = 0;
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if (subGeom instanceof Polygon) {
                sum += ((Polygon) subGeom).getExteriorRing().getLength();
            }
        }
        return sum;
    }
}
