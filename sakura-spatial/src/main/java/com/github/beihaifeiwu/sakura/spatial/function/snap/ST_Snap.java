package com.github.beihaifeiwu.sakura.spatial.function.snap;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.overlay.snap.GeometrySnapper;

/**
 * Snaps two geometries together with a given tolerance
 */
public class ST_Snap {

    /**
     * Snaps two geometries together with a given tolerance
     *
     * @param geometryA a geometry to snap
     * @param geometryB a geometry to snap
     * @param distance  the tolerance to use
     * @return the snapped geometries
     */
    public static Geometry snap(Geometry geometryA, Geometry geometryB, double distance) {
        if (geometryA == null || geometryB == null) {
            return null;
        }
        Geometry[] snapped = GeometrySnapper.snap(geometryA, geometryB, distance);
        return snapped[0];
    }
}
