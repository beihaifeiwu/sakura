package com.github.beihaifeiwu.sakura.spatial.function.crs;

import com.vividsolutions.jts.geom.Geometry;


/**
 * Return a new geometry with a replaced spatial reference id.
 *
 * @author Nicolas Fortin
 */
public class ST_SetSRID {

    /**
     * Set a new SRID to the geometry
     *
     * @param geometry
     * @param srid
     * @return
     * @throws IllegalArgumentException
     */
    public static Geometry setSRID(Geometry geometry, Integer srid) throws IllegalArgumentException {
        if (geometry == null) {
            return null;
        }
        if (srid == null) {
            throw new IllegalArgumentException("The SRID code cannot be null.");
        }
        Geometry geom = (Geometry) geometry.clone();
        geom.setSRID(srid);
        return geom;
    }
}
