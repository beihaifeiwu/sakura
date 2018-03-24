package com.github.beihaifeiwu.sakura.spatial.function.predicates;

import com.github.beihaifeiwu.sakura.spatial.function.convert.ST_AsBinary;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Arrays;

/**
 * ST_OrderingEquals compares two geometries and t (TRUE) if the geometries are equal
 * and the coordinates are in the same order; otherwise it returns f (FALSE).
 * <p>
 * This method implements the SQL/MM specification: SQL-MM 3: 5.1.43
 *
 * @author Erwan Bocher
 */
public class ST_OrderingEquals {

    /**
     * Returns true if the given geometries represent the same
     * geometry and points are in the same directional order.
     *
     * @param geomA
     * @param geomB
     * @return
     */
    public static boolean orderingEquals(Geometry geomA, Geometry geomB) {
        return Arrays.equals(ST_AsBinary.toBytes(geomA), ST_AsBinary.toBytes(geomB));
    }
}
