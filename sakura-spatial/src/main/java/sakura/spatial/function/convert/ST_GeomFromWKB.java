package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import lombok.experimental.UtilityClass;
import sakura.spatial.SpatialException;

/**
 * Convert a WKB object to a geometry
 *
 * @author Erwan Bocher
 */
@UtilityClass
public class ST_GeomFromWKB {

    /**
     * Convert a WKB representation to a geometry
     *
     * @param bytes the input WKB object
     * @param srid  the input SRID
     * @return
     */
    public static Geometry toGeometry(byte[] bytes, int srid) {
        if (bytes == null) {
            return null;
        }
        WKBReader wkbReader = new WKBReader();
        try {
            Geometry geometry = wkbReader.read(bytes);
            geometry.setSRID(srid);
            return geometry;
        } catch (ParseException ex) {
            throw new SpatialException("Cannot parse the input bytes", ex);
        }
    }

    /**
     * Convert a WKB representation to a geometry without specify a SRID.
     *
     * @param bytes
     * @return
     */
    public static Geometry toGeometry(byte[] bytes) {
        return toGeometry(bytes, 0);
    }

}
