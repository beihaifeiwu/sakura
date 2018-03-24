package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * Convert a Well Known Text String into a Geometry instance.
 */
public class ST_GeomFromText {

    /**
     * Convert well known text parameter into a Geometry
     *
     * @param wkt Well known text
     * @return Geometry instance or null if parameter is null
     * @throws ParseException If wkt is invalid
     */
    public static Geometry toGeometry(String wkt) {
        if (wkt == null) {
            return null;
        }
        WKTReader wktReader = new WKTReader();
        try {
            return wktReader.read(wkt);
        } catch (ParseException ex) {
            throw new SpatialException("Cannot parse the WKT.", ex);
        }
    }

    /**
     * Convert well known text parameter into a Geometry
     *
     * @param wkt  Well known text
     * @param srid Geometry SRID
     * @return Geometry instance
     * @throws SpatialException If wkt is invalid
     */
    public static Geometry toGeometry(String wkt, int srid) {
        if (wkt == null) {
            return null;
        }
        try {
            WKTReader wktReaderSRID = new WKTReader(GeometryFactories.factory(srid));
            return wktReaderSRID.read(wkt);
        } catch (ParseException ex) {
            throw new SpatialException(ex);
        }
    }
}
