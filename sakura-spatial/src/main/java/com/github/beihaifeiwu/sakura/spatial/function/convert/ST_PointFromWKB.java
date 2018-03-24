package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryMetaData;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryTypeCodes;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;

import java.io.IOException;

/**
 * Convert Well Known Binary into a POINT.
 *
 * @author Erwan Bocher
 */
public class ST_PointFromWKB {

    /**
     * Convert WKT into a Point
     *
     * @param bytes Byte array
     * @return Point instance of null if bytes null
     * @throws SpatialException WKB Parse error
     */
    public static Geometry toPoint(byte[] bytes) throws IOException {
        return toPoint(bytes, 0);
    }

    /**
     * Convert WKT into a Point
     *
     * @param bytes Byte array
     * @param srid  SRID
     * @return Point instance of null if bytes null
     * @throws SpatialException WKB Parse error
     */
    public static Geometry toPoint(byte[] bytes, int srid) throws IOException {
        if (bytes == null) {
            return null;
        }
        WKBReader wkbReader = new WKBReader();
        try {
            if (GeometryMetaData.getMetaDataFromWKB(bytes).getGeometryType() != GeometryTypeCodes.POINT) {
                throw new SpatialException("Provided WKB is not a POINT.");
            }
            Geometry geometry = wkbReader.read(bytes);
            geometry.setSRID(srid);
            return geometry;
        } catch (ParseException ex) {
            throw new SpatialException("ParseException while evaluating ST_PointFromWKB", ex);
        }
    }

}
