package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Geometry;

import java.sql.SQLException;

/**
 * Convert a WKT String into a POLYGON.
 */
public class ST_PolyFromText {

    /**
     * @param wKT WellKnown text value
     * @return Geometry
     * @throws SQLException Invalid argument or the geometry type is wrong.
     */
    public static Geometry toGeometry(String wKT) throws SQLException {
        return toGeometry(wKT, 0);
    }

    /**
     * @param wKT  WellKnown text value
     * @param srid Valid SRID
     * @return Geometry
     * @throws SQLException Invalid argument or the geometry type is wrong.
     */
    public static Geometry toGeometry(String wKT, int srid) throws SQLException {
        Geometry geometry = ST_GeomFromText.toGeometry(wKT, srid);
        if (!geometry.getGeometryType().equalsIgnoreCase("POLYGON")) {
            throw new SQLException("The provided WKT Geometry is not a POLYGON.");
        }
        return geometry;
    }
}
