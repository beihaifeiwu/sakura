package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Convert a Geometry value into a Well Known Text value.
 */
public class ST_AsWKT {

    /**
     * Convert a Geometry value into a Well Known Text value.
     *
     * @param geometry Geometry instance
     * @return The String representation
     */
    public static String asWKT(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        WKTWriter wktWriter = new WKTWriter();
        return wktWriter.write(geometry);
    }
}
