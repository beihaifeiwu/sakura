
package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Test if the provided geometry is empty.
 */
public class ST_IsEmpty {

    /**
     * @param geometry Geometry instance
     * @return True if the provided geometry is empty
     */
    public static Boolean isEmpty(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.isEmpty();
    }
}
