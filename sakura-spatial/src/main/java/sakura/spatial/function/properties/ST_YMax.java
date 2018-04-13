package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_YMax returns the maximal y-value of the given geometry.
 */
public class ST_YMax {

    /**
     * Returns the maximal y-value of the given geometry.
     *
     * @param geom Geometry
     * @return The maximal y-value of the given geometry, or null if the geometry is null.
     */
    public static Double getMaxY(Geometry geom) {
        if (geom != null) {
            return geom.getEnvelopeInternal().getMaxY();
        } else {
            return null;
        }
    }
}
