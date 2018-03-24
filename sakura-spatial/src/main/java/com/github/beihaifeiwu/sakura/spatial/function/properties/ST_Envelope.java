package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Get geometry envelope as geometry.
 */
public class ST_Envelope {

    /**
     * @param geometry Geometry instance
     * @param srid     input SRID
     * @return Geometry envelope
     */
    public static Geometry getEnvelope(Geometry geometry, int srid) {
        if (geometry == null) {
            return null;
        }
        Geometry geometryEnvelope = geometry.getEnvelope();
        geometryEnvelope.setSRID(srid);
        return geometryEnvelope;
    }

    /**
     * @param geometry Geometry instance
     * @return Geometry envelope
     */
    public static Geometry getEnvelope(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.getEnvelope();
    }
}
