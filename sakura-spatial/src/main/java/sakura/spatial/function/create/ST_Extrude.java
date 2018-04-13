package sakura.spatial.function.create;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import sakura.spatial.SpatialException;
import sakura.spatial.function.volume.GeometryExtrude;

/**
 * ST_Extrude takes a LINESTRING or POLYGON as input and extends it to a 3D
 * representation, returning  a geometry collection containing floor, ceiling
 * and wall geometries. In the case of a LINESTRING, the floor and ceiling are
 * LINESTRINGs; for a POLYGON, they are POLYGONs.
 */
public class ST_Extrude {

    /**
     * Extrudes a POLYGON or a LINESTRING into a GEOMETRYCOLLECTION containing
     * the floor (input geometry), walls and ceiling.
     * Note :  the NaN z value of the input geometry are replaced by a zero.
     *
     * @param geometry Input geometry
     * @param height   Desired height
     * @return Collection (floor, walls, ceiling)
     */
    public static GeometryCollection extrudeGeometry(Geometry geometry, double height) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof Polygon) {
            return GeometryExtrude.extrudePolygonAsGeometry((Polygon) geometry, height);
        } else if (geometry instanceof LineString) {
            return GeometryExtrude.extrudeLineStringAsGeometry((LineString) geometry, height);
        }
        throw new SpatialException("Only LINESTRING and POLYGON inputs are accepted.");
    }

    /**
     * Extrudes a POLYGON or a LINESTRING into a GEOMETRYCOLLECTION containing
     * the floor (input geometry), walls and ceiling.  A flag of 1 extracts
     * walls and a flag of 2 extracts the roof.
     *
     * @param geometry Input geometry
     * @param height   Desired height
     * @param flag     1 (walls), 2 (roof)
     * @return Walls or roof
     */
    public static Geometry extrudeGeometry(Geometry geometry, double height, int flag) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof Polygon) {
            if (flag == 1) {
                return GeometryExtrude.extractWalls((Polygon) geometry, height);
            } else if (flag == 2) {
                return GeometryExtrude.extractRoof((Polygon) geometry, height);
            } else {
                throw new SpatialException("Incorrect flag value. Please set 1 to extract walls "
                        + "or 2 to extract roof.");
            }
        } else if (geometry instanceof LineString) {
            if (flag == 1) {
                return GeometryExtrude.extractWalls((LineString) geometry, height);
            } else if (flag == 2) {
                return GeometryExtrude.extractRoof((LineString) geometry, height);
            } else {
                throw new SpatialException("Incorrect flag value. Please set 1 to extract walls "
                        + "or 2 to extract roof.");
            }
        }
        throw new SpatialException("Only LINESTRING and POLYGON inputs are accepted.");
    }
}
