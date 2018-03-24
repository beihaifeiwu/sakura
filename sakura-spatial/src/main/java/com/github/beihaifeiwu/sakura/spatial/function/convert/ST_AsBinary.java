package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.utils.ZVisitor;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import lombok.experimental.UtilityClass;

/**
 * Convert a geometry into Well Known Binary..
 */
@UtilityClass
public class ST_AsBinary {

    /**
     * Convert a geometry into a binary value.
     *
     * @param geometry Geometry instance
     * @return Well Known Binary
     */
    public static byte[] toBytes(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return convertToWKB(geometry);
    }

    private static byte[] convertToWKB(Geometry g) {
        boolean includeSRID = g.getSRID() != 0;
        int dimensionCount = getDimensionCount(g);
        WKBWriter writer = new WKBWriter(dimensionCount, includeSRID);
        return writer.write(g);
    }

    private static int getDimensionCount(Geometry geometry) {
        ZVisitor finder = new ZVisitor();
        geometry.apply(finder);
        return finder.isFoundZ() ? 3 : 2;
    }
}
