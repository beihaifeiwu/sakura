package sakura.spatial.function.mesh;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import sakura.spatial.SpatialException;


/**
 * Returns polygons or lines that represent a Delaunay triangulation constructed
 * from a geometry. Note that the triangulation computes
 * the intersections between lines.
 */
public class ST_ConstrainedDelaunay {

    /**
     * Build a constrained delaunay triangulation based on a geometry
     * (point, line, polygon)
     *
     * @param geometry
     * @return a set of polygons (triangles)
     */
    public static GeometryCollection createCDT(Geometry geometry) {
        return createCDT(geometry, 0);
    }

    /**
     * Build a constrained delaunay triangulation based on a geometry
     * (point, line, polygon)
     *
     * @param geometry
     * @param flag
     * @return a set of polygons (triangles)
     */
    public static GeometryCollection createCDT(Geometry geometry, int flag) {
        if (geometry != null) {
            DelaunayData delaunayData = new DelaunayData();
            delaunayData.put(geometry, DelaunayData.MODE.CONSTRAINED);
            delaunayData.triangulate();
            if (flag == 0) {
                return delaunayData.getTriangles();
            } else if (flag == 1) {
                return delaunayData.getTrianglesSides();
            } else {
                throw new SpatialException("Only flag 0 or 1 is supported.");
            }
        }
        return null;
    }
}
