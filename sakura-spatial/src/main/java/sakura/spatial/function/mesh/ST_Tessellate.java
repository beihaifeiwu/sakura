package sakura.spatial.function.mesh;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.util.ArrayList;

/**
 * Tessellate a set of Polygon with adaptive triangles.
 */
public class ST_Tessellate {

    private static MultiPolygon tessellatePolygon(Polygon polygon) {
        DelaunayData delaunayData = new DelaunayData();
        delaunayData.put(polygon, DelaunayData.MODE.TESSELLATION);
        // Do triangulation
        delaunayData.triangulate();
        return delaunayData.getTriangles();
    }

    /**
     * Return the tessellation of a (multi)polygon surface with adaptive triangles
     *
     * @param geometry
     * @return
     * @throws IllegalArgumentException
     */
    public static MultiPolygon tessellate(Geometry geometry) throws IllegalArgumentException {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof Polygon) {
            return tessellatePolygon((Polygon) geometry);
        } else if (geometry instanceof MultiPolygon) {
            ArrayList<Polygon> polygons = new ArrayList<Polygon>(geometry.getNumGeometries() * 2);
            for (int idPoly = 0; idPoly < geometry.getNumGeometries(); idPoly++) {
                MultiPolygon triangles = tessellatePolygon((Polygon) geometry.getGeometryN(idPoly));
                polygons.ensureCapacity(triangles.getNumGeometries());
                for (int idTri = 0; idTri < triangles.getNumGeometries(); idTri++) {
                    polygons.add((Polygon) triangles.getGeometryN(idTri));
                }
            }
            return geometry.getFactory().createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
        } else {
            throw new IllegalArgumentException("ST_Tessellate accept only Polygon and MultiPolygon types not instance" +
                    " of " + geometry.getClass().getSimpleName());
        }
    }
}
