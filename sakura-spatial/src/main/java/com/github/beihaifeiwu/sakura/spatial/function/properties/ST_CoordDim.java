package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryMetaData;

import java.io.IOException;

/**
 * ST_CoordDim returns the dimension of the coordinates of the given geometry.
 * Implements the SQL/MM Part 3: Spatial 5.1.3
 */
public class ST_CoordDim {

    /**
     * Returns the dimension of the coordinates of the given geometry.
     *
     * @param geom Geometry
     * @return The dimension of the coordinates of the given geometry
     * @throws IOException
     */
    public static Integer getCoordinateDimension(byte[] geom) throws IOException {
        if (geom == null) {
            return null;
        }
        return GeometryMetaData.getMetaDataFromWKB(geom).getDimension();
    }
}
