package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryMetaData;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryTypeCodes;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import java.io.IOException;

/**
 * Convert Well Known Binary into a LINESTRING.
 */
public class ST_LineFromWKB {

    /**
     * Convert WKT into a LinearRing
     *
     * @param bytes Byte array
     * @return LineString instance of null if bytes null
     * @throws IOException
     */
    public static Geometry toLineString(byte[] bytes) throws IOException {
        return toLineString(bytes, 0);
    }


    /**
     * Convert WKT into a LinearRing
     *
     * @param bytes Byte array
     * @param srid  SRID
     * @return LineString instance of null if bytes null
     * @throws IOException
     */
    public static Geometry toLineString(byte[] bytes, int srid) throws IOException {
        if (bytes == null) {
            return null;
        }
        WKBReader wkbReader = new WKBReader();
        try {
            if (GeometryMetaData.getMetaDataFromWKB(bytes).getGeometryType() != GeometryTypeCodes.LINESTRING) {
                throw new SpatialException("Provided WKB is not a LINESTRING.");
            }
            Geometry geometry = wkbReader.read(bytes);
            geometry.setSRID(srid);
            return geometry;
        } catch (ParseException ex) {
            throw new SpatialException("ParseException while evaluating ST_LineFromWKB", ex);
        }
    }
}
