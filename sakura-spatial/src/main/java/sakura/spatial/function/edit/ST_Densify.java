package sakura.spatial.function.edit;

import com.vividsolutions.jts.densify.Densifier;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Densifies a geometry using the given distance tolerance.
 *
 * @see Densifier
 */
public class ST_Densify {

    /**
     * Densify a geometry using the given distance tolerance.
     *
     * @param geometry  Geometry
     * @param tolerance Distance tolerance
     * @return Densified geometry
     */
    public static Geometry densify(Geometry geometry, double tolerance) {
        if (geometry == null) {
            return null;
        }
        return Densifier.densify(geometry, tolerance);
    }
}
