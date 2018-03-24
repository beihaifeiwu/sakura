package com.github.beihaifeiwu.sakura.spatial.function.affine_transformations;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import lombok.experimental.UtilityClass;

/**
 * ST_Scale scales the given geometry by multiplying the coordinates by the
 * indicated scale factors.
 */
@UtilityClass
public class ST_Scale {
    /**
     * Scales the given geometry by multiplying the coordinates by the
     * indicated x and y scale factors, leaving the z-coordinate untouched.
     *
     * @param geom    Geometry
     * @param xFactor x scale factor
     * @param yFactor y scale factor
     * @return The geometry scaled by the given x and y scale factors
     */
    public static Geometry scale(Geometry geom, double xFactor, double yFactor) {
        return scale(geom, xFactor, yFactor, 1.0);
    }

    /**
     * Scales the given geometry by multiplying the coordinates by the
     * indicated x, y and z scale factors.
     *
     * @param geom    Geometry
     * @param xFactor x scale factor
     * @param yFactor y scale factor
     * @param zFactor z scale factor
     * @return The geometry scaled by the given x, y and z scale factors
     */
    public static Geometry scale(Geometry geom, double xFactor, double yFactor, double zFactor) {
        if (geom != null) {
            Geometry scaledGeom = (Geometry) geom.clone();
            for (Coordinate c : scaledGeom.getCoordinates()) {
                c.setOrdinate(Coordinate.X, c.getOrdinate(Coordinate.X) * xFactor);
                c.setOrdinate(Coordinate.Y, c.getOrdinate(Coordinate.Y) * yFactor);
                c.setOrdinate(Coordinate.Z, c.getOrdinate(Coordinate.Z) * zFactor);
            }
            return scaledGeom;
        } else {
            return null;
        }
    }
}
