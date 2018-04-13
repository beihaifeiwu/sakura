package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Geometry;
import sakura.spatial.SpatialException;

/**
 * Convert a WKT String into a Point.
 */
public class ST_PointFromText {

    public static final String TYPE_ERROR =
            "The provided WKT Geometry is not a POINT. Type: ";

    /**
     * Convert the WKT String to a Geometry with the given SRID.
     *
     * @param wKT Well Known Text value
     * @return Geometry
     * @throws SpatialException Invalid argument or the geometry type is wrong.
     */
    public static Geometry toGeometry(String wKT) {
        return toGeometry(wKT, 0);
    }

    /**
     * Convert the WKT String to a Geometry with the given SRID.
     *
     * @param wKT  Well Known Text value
     * @param srid Valid SRID
     * @return Geometry
     * @throws SpatialException Invalid argument or the geometry type is wrong.
     */
    public static Geometry toGeometry(String wKT, int srid) {
        if (wKT == null) {
            return null;
        }
        Geometry geometry = ST_GeomFromText.toGeometry(wKT, srid);
        final String geometryType = geometry.getGeometryType();
        if (!"POINT".equalsIgnoreCase(geometryType)) {
            throw new SpatialException(TYPE_ERROR + geometryType);
        }
        return geometry;
    }
}
