package sakura.spatial.function.properties;

import sakura.spatial.utils.GeometryMetaData;
import sakura.spatial.utils.GeometryTypeCodes;

import java.io.IOException;

/**
 * Returns the OGC SFS {@link GeometryTypeCodes} of a Geometry.
 * This function does not take account of Z nor M.
 * This function is not part of SFS; see {@link ST_GeometryType}
 * It is used in constraints.
 */
public class ST_GeometryTypeCode {

    /**
     * @param geometry Geometry WKB.
     * @return Returns the OGC SFS {@link sakura.spatial.utils.GeometryTypeCodes} of a Geometry.
     * This function does not take account of Z nor M.
     * @throws IOException WKB is not valid.
     */
    public static Integer getTypeCode(byte[] geometry) throws IOException {
        if (geometry == null) {
            return null;
        }
        return GeometryMetaData.getMetaDataFromWKB(geometry).getGeometryType();
    }
}
