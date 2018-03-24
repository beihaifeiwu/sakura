package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryMetaData;

import java.io.IOException;

/**
 * Retrieve the SRID from an EWKB encoded geometry.
 */
public class ST_SRID {

    /**
     * @param geometry Geometry instance or null
     * @return SRID value or 0 if input geometry does not have one.
     * @throws IOException
     */
    public static Integer getSRID(byte[] geometry) throws IOException {
        if (geometry == null) {
            return 0;
        }
        return GeometryMetaData.getMetaDataFromWKB(geometry).getSrid();
    }
}
