package com.github.beihaifeiwu.sakura.spatial.function.create;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.OctagonalEnvelope;

/**
 * Computes the octogonal envelope of a geometry.
 *
 * @author Erwan Bocher
 * @see OctagonalEnvelope
 */
public class ST_OctogonalEnvelope {

    public static Geometry computeOctogonalEnvelope(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        return new OctagonalEnvelope(geometry).toGeometry(GeometryFactories.default_());
    }
}
