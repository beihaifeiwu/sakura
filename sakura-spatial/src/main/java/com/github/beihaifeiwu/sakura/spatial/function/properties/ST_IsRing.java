package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

/**
 * Return TRUE if the provided geometry is a closed and simple LINESTRING or
 * MULTILINESTRING; NULL otherwise.
 */
public class ST_IsRing {

    /**
     * @param geometry Geometry instance
     * @return True if the provided geometry is a ring; null otherwise
     */
    public static Boolean isRing(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof MultiLineString) {
            MultiLineString mString = ((MultiLineString) geometry);
            return mString.isClosed() && mString.isSimple();
        } else if (geometry instanceof LineString) {
            LineString line = (LineString) geometry;
            return line.isClosed() && geometry.isSimple();
        }
        return null;
    }
}
