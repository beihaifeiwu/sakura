package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 * ST_3DPerimeter returns the 3D perimeter of a polygon or a multipolygon.
 * In the case of a 2D geometry, ST_3DPerimeter returns the same value as ST_Perimeter.
 */
public class ST_3DPerimeter {

    /**
     * Compute the 3D perimeter of a polygon or a multipolygon.
     *
     * @param geometry
     * @return
     */
    public static Double st3Dperimeter(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry.getDimension() < 2) {
            return 0d;
        }
        return compute3DPerimeter(geometry);
    }

    /**
     * Compute the 3D perimeter
     *
     * @param geometry
     * @return
     */
    private static double compute3DPerimeter(Geometry geometry) {
        double sum = 0;
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry subGeom = geometry.getGeometryN(i);
            if (subGeom instanceof Polygon) {
                sum += ST_3DLength.length3D(((Polygon) subGeom).getExteriorRing());
            }
        }
        return sum;
    }
}
