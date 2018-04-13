package sakura.spatial.function.properties;

import sakura.spatial.utils.GeometryMetaData;

import java.io.IOException;

/**
 * Returns 1 if a geometry has a z-coordinate, otherwise 0.
 * <p>
 * Implements the SQL/MM Part 3: Spatial 5.1.3
 */
public class ST_Is3D {

    /**
     * Returns 1 if a geometry has a z-coordinate, otherwise 0.
     *
     * @param geom
     * @return
     * @throws IOException
     */
    public static int is3D(byte[] geom) throws IOException {
        if (geom == null) {
            return 0;
        }
        return GeometryMetaData.getMetaDataFromWKB(geom).isHasZ() ? 1 : 0;
    }

}
