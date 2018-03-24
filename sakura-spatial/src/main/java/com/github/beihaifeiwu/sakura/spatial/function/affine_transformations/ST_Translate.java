package com.github.beihaifeiwu.sakura.spatial.function.affine_transformations;

import com.github.beihaifeiwu.sakura.spatial.utils.CoordinateSequenceDimensionFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import lombok.experimental.UtilityClass;

/**
 * Translates a geometry using X, Y (and possibly Z) offsets.
 */
@UtilityClass
public class ST_Translate {

    public static final String MIXED_DIM_ERROR = "Cannot translate geometries of mixed dimension";

    /**
     * Translates a geometry using X and Y offsets.
     *
     * @param geom Geometry
     * @param x    X
     * @param y    Y
     * @return Translated geometry
     */
    public static Geometry translate(Geometry geom, double x, double y) {
        if (geom == null) {
            return null;
        }
        final CoordinateSequenceDimensionFilter filter = new CoordinateSequenceDimensionFilter();
        geom.apply(filter);
        checkMixed(filter);
        return AffineTransformation.translationInstance(x, y).transform(geom);
    }

    /**
     * Translates a geometry using X, Y and Z offsets.
     *
     * @param geom Geometry
     * @param x    X
     * @param y    Y
     * @param z    Z
     * @return Translated geometry
     */
    public static Geometry translate(Geometry geom, double x, double y, double z) {
        if (geom == null) {
            return null;
        }
        final CoordinateSequenceDimensionFilter filter = new CoordinateSequenceDimensionFilter();
        geom.apply(filter);
        checkMixed(filter);
        // For all 2D geometries, we only translate by (x, y).
        if (filter.is2D()) {
            return AffineTransformation.translationInstance(x, y).transform(geom);
        } else {
            final Geometry clone = (Geometry) geom.clone();
            clone.apply(new ZAffineTransformation(x, y, z));
            return clone;
        }
    }

    /**
     * Throws an exception if the geometry contains coordinates of mixed
     * dimension.
     *
     * @param filter Filter
     */
    private static void checkMixed(CoordinateSequenceDimensionFilter filter) {
        if (filter.isMixed()) {
            throw new IllegalArgumentException(MIXED_DIM_ERROR);
        }
    }
}
