package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryMetaData;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryTypeCodes;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import java.io.IOException;

/**
 * Convert Well Known Binary into Geometry then check that it is a Polygon
 */
public class ST_PolyFromWKB {

    /**
     * @param bytes WKB
     * @return Geometry instance of null if bytes are null
     * @throws SpatialException Wkb parse exception
     */
    public static Geometry toPolygon(byte[] bytes) throws IOException {
        return toPolygon(bytes, 0);
    }

    /**
     * @param bytes WKB
     * @param srid  SRID
     * @return Geometry instance of null if bytes are null
     * @throws SpatialException Wkb parse exception
     */
    public static Geometry toPolygon(byte[] bytes, int srid) throws IOException {
        if (bytes == null) {
            return null;
        }
        WKBReader wkbReader = new WKBReader();
        try {
            if (GeometryMetaData.getMetaDataFromWKB(bytes).getGeometryType() != GeometryTypeCodes.POLYGON) {
                throw new SpatialException("Provided WKB is not a Polygon.");
            }
            Geometry geometry = wkbReader.read(bytes);
            geometry.setSRID(srid);
            return geometry;
        } catch (ParseException ex) {
            throw new SpatialException("ParseException while evaluating ST_PolyFromWKB", ex);
        }
    }
}
