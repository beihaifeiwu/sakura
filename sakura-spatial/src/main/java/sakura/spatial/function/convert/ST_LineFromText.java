package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Geometry;
import sakura.spatial.SpatialException;

/**
 * Convert a WKT String into a LINESTRING.
 *
 * @author Nicolas Fortin
 */
public class ST_LineFromText {

    /**
     * @param wKT WellKnown text value
     * @return Geometry
     * @throws SpatialException Invalid argument or the geometry type is wrong.
     */
    public static Geometry toGeometry(String wKT) {
        return toGeometry(wKT, 0);
    }


    /**
     * @param wKT  WellKnown text value
     * @param srid Valid SRID
     * @return Geometry
     * @throws SpatialException Invalid argument or the geometry type is wrong.
     */
    public static Geometry toGeometry(String wKT, int srid) {
        if (wKT == null) {
            return null;
        }
        Geometry geometry = ST_GeomFromText.toGeometry(wKT, srid);
        if (!geometry.getGeometryType().equalsIgnoreCase("linestring")) {
            throw new SpatialException("The provided WKT Geometry is not a LINESTRING.");
        }
        return geometry;
    }
}