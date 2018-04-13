/**
 * H2GIS is a library that brings spatial support to the H2 Database Engine
 * <http://www.h2database.com>. H2GIS is developed by CNRS
 * <http://www.cnrs.fr/>.
 * <p>
 * This code is part of the H2GIS project. H2GIS is free software;
 * you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * version 3.0 of the License.
 * <p>
 * H2GIS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details <http://www.gnu.org/licenses/>.
 * <p>
 * <p>
 * For more information, please consult: <http://www.h2gis.org/>
 * or contact directly: info_at_h2gis.org
 */

package sakura.spatial.utils;

import com.vividsolutions.jts.io.ByteArrayInStream;
import com.vividsolutions.jts.io.ByteOrderDataInStream;
import com.vividsolutions.jts.io.ByteOrderValues;
import com.vividsolutions.jts.io.WKBConstants;
import lombok.Getter;

import java.io.IOException;

/**
 * Extract Geometry MetaData from WKB.
 * WKB Conversion source from {@link com.vividsolutions.jts.io.WKBReader}
 */
@Getter
public final class GeometryMetaData {
    /**
     * If SRID is available
     */
    private final boolean hasSRID;
    /**
     * If Z Component is available
     */
    private final boolean hasZ;
    /**
     * Geometry type code
     */
    private final int geometryType;
    /**
     * Geometry dimension 2 or 3
     */
    private final int dimension;
    /**
     * Projection code
     */
    private final int srid;

    private GeometryMetaData(int dimension, boolean hasSRID, boolean hasZ, int geometryType, int srid) {
        this.dimension = dimension;
        this.hasSRID = hasSRID;
        this.hasZ = hasZ;
        this.geometryType = geometryType;
        this.srid = srid;
    }

    /**
     * Read the first bytes of Geometry WKB.
     *
     * @param bytes WKB Bytes
     * @return Geometry MetaData
     * @throws IOException If WKB meta is invalid (do not check the Geometry)
     */
    public static GeometryMetaData getMetaDataFromWKB(byte[] bytes) throws IOException {
        ByteOrderDataInStream dis = new ByteOrderDataInStream();
        dis.setInStream(new ByteArrayInStream(bytes));
        // determine byte order
        byte byteOrderWKB = dis.readByte();
        // always set byte order, since it may change from geometry to geometry
        int byteOrder = byteOrderWKB == WKBConstants.wkbNDR ? ByteOrderValues.LITTLE_ENDIAN : ByteOrderValues.BIG_ENDIAN;
        dis.setOrder(byteOrder);

        int typeInt = dis.readInt();
        int geometryType = typeInt & 0xff;
        // determine if Z values are present
        boolean hasZ = (typeInt & 0x80000000) != 0;
        int inputDimension = hasZ ? 3 : 2;
        // determine if SRIDs are present
        boolean hasSRID = (typeInt & 0x20000000) != 0;

        int SRID = 0;
        if (hasSRID) {
            SRID = dis.readInt();
        }
        return new GeometryMetaData(inputDimension, hasSRID, hasZ, geometryType, SRID);
    }
}
