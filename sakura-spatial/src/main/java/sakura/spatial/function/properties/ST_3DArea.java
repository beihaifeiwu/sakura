package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import sakura.spatial.function.mesh.DelaunayData;

/**
 * Compute the 3D area of a polygon or a multiolygon.
 */
public class ST_3DArea {

    /**
     * Compute the 3D area of a polygon or a multipolygon derived from a 3D triangular decomposition.
     * Distance units are those of the geometry spatial reference system.
     *
     * @param geometry
     * @return
     */
    public static Double st3darea(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        double area = 0;
        for (int idPoly = 0; idPoly < geometry.getNumGeometries(); idPoly++) {
            Geometry subGeom = geometry.getGeometryN(idPoly);
            if (subGeom instanceof Polygon) {
                area += compute3DArea((Polygon) subGeom);
            }
        }
        return area;
    }

    /**
     * Compute the 3D area of a polygon
     *
     * @param geometry
     * @return
     */
    private static Double compute3DArea(Polygon geometry) {
        DelaunayData delaunayData = new DelaunayData();
        delaunayData.put(geometry, DelaunayData.MODE.TESSELLATION);
        // Do triangulation
        delaunayData.triangulate();
        return delaunayData.get3DArea();
    }
}
