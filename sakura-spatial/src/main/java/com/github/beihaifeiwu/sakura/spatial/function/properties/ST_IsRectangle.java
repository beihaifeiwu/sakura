package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;

/**
 * ST_IsRectangle returns true if the given geometry is a rectangle.
 */
public class ST_IsRectangle {

    /**
     * Returns true if the given geometry is a rectangle.
     *
     * @param geometry Geometry
     * @return True if the given geometry is a rectangle
     */
    public static Boolean isRectangle(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return geometry.isRectangle();
    }
}
