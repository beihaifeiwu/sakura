
package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Test if the provided geometry is simple.
 */
public class ST_IsSimple {

    /**
     * @param geometry Geometry instance
     * @return True if the provided geometry has no points of self-tangency, self-intersection or other anomalous points.
     */
    public static Boolean isSimple(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.isSimple();
    }
}
