package sakura.spatial.function.create;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.OctagonalEnvelope;
import sakura.spatial.utils.GeometryFactories;

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
