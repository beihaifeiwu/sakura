package com.github.beihaifeiwu.sakura.spatial.function.aggregate;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

/**
 * Construct an array of Geometries.
 */
public class ST_Collect extends ST_Accum {

    public static GeometryCollection collect(Geometry... geometries) {
        return accum(geometries);
    }
}
