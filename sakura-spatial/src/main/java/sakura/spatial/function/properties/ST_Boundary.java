package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get geometry boundary as geometry.
 */
public class ST_Boundary {

    /**
     * @param geometry Geometry instance
     * @return Geometry envelope
     */
    public static Geometry getBoundary(Geometry geometry, int srid) {
        if (geometry == null) {
            return null;
        }
        Geometry geometryEnvelope = geometry.getBoundary();
        geometryEnvelope.setSRID(srid);
        return geometryEnvelope;
    }

    /**
     * @param geometry Geometry instance
     * @return Geometry envelope
     */
    public static Geometry getBoundary(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getBoundary();
    }
}
