package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Convert a WKT String into a MULTILINESTRING.
 */
public class ST_MLineFromText {

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
        Geometry geometry = ST_GeomFromText.toGeometry(wKT, srid);
        if (!geometry.getGeometryType().equalsIgnoreCase("MULTILINESTRING")) {
            throw new SpatialException("The provided WKT Geometry is not a MULTILINESTRING");
        }
        return geometry;
    }
}
