package sakura.spatial.function.create;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Class to create a polygon
 */
public class ST_MakePolygon {

    /**
     * Creates a Polygon formed by the given shell.
     *
     * @param shell
     * @return
     */
    public static Polygon makePolygon(Geometry shell) throws IllegalArgumentException {
        if (shell == null) {
            return null;
        }
        LinearRing outerLine = checkLineString(shell);
        return shell.getFactory().createPolygon(outerLine, null);
    }

    /**
     * Creates a Polygon formed by the given shell and holes.
     *
     * @param shell
     * @param holes
     * @return
     */
    public static Polygon makePolygon(Geometry shell, Geometry... holes) throws IllegalArgumentException {
        if (shell == null) {
            return null;
        }
        LinearRing outerLine = checkLineString(shell);
        LinearRing[] interiorlinestrings = new LinearRing[holes.length];
        for (int i = 0; i < holes.length; i++) {
            interiorlinestrings[i] = checkLineString(holes[i]);
        }
        return shell.getFactory().createPolygon(outerLine, interiorlinestrings);
    }

    /**
     * Check if a geometry is a linestring and if its closed.
     *
     * @param geometry
     * @return
     * @throws IllegalArgumentException
     */
    private static LinearRing checkLineString(Geometry geometry) throws IllegalArgumentException {
        if (geometry instanceof LinearRing) {
            return (LinearRing) geometry;

        } else if (geometry instanceof LineString) {
            LineString lineString = (LineString) geometry;
            if (lineString.isClosed()) {
                return geometry.getFactory().createLinearRing(lineString.getCoordinateSequence());
            } else {
                throw new IllegalArgumentException("The linestring must be closed.");
            }
        } else {
            throw new IllegalArgumentException("Only support linestring.");
        }
    }
}
