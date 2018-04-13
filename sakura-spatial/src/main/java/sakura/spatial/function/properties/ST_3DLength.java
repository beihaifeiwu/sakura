package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.*;

/**
 * ST_3DLength returns the 3D length (of a LineString) or the 3D perimeter (of a Polygon).
 * In the case of a 2D geometry, ST_3DLength returns the same value as ST_Length.
 */
public class ST_3DLength {

    /**
     * Returns the 3D length (of a LineString or MultiLineString) ortherwise 0.
     *
     * @param geom Geometry
     * @return The 3D length (of a LineString or MultiLineString) ortherwise 0
     */
    public static Double stLength3D(Geometry geom) {
        if (geom == null) {
            return null;
        }
        if (geom instanceof LineString || geom instanceof MultiLineString) {
            return length3D(geom);
        }
        return 0.0D;
    }

    /**
     * Returns the 3D length of the given geometry.
     *
     * @param geom Geometry
     * @return The 3D length of the given geometry
     */
    public static double length3D(Geometry geom) {
        double sum = 0;
        for (int i = 0; i < geom.getNumGeometries(); i++) {
            sum += length3D((LineString) geom.getGeometryN(i));

        }
        return sum;
    }

    /**
     * Returns the 3D perimeter of the given polygon.
     *
     * @param polygon Polygon
     * @return The 3D perimeter of the given polygon
     */
    public static double length3D(Polygon polygon) {
        double length = 0.0;
        length += length3D(polygon.getExteriorRing().getCoordinateSequence());
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            length += length3D(polygon.getInteriorRingN(i));
        }
        return length;
    }

    /**
     * Returns the 3D perimeter of the given LineString.
     *
     * @param lineString LineString
     * @return The 3D perimeter of the given LineString
     */
    public static double length3D(LineString lineString) {
        return length3D(lineString.getCoordinateSequence());
    }

    /**
     * Computes the length of a LineString specified by a sequence of
     * coordinates.
     *
     * @param points The coordinate sequence
     * @return The length of the corresponding LineString
     */
    public static double length3D(CoordinateSequence points) {
        // optimized for processing CoordinateSequences

        int numberOfCoords = points.size();
        // Points have no length.
        if (numberOfCoords < 2) {
            return 0.0;
        }

        Coordinate currentCoord = new Coordinate();
        points.getCoordinate(0, currentCoord);
        double x0 = currentCoord.x;
        double y0 = currentCoord.y;
        double z0 = currentCoord.z;

        double length = 0.0;
        for (int i = 1; i < numberOfCoords; i++) {
            points.getCoordinate(i, currentCoord);

            double x1 = currentCoord.x;
            double y1 = currentCoord.y;
            double z1 = currentCoord.z;

            double dx = x1 - x0;
            double dy = y1 - y0;
            double dz;

            // For 2D geometries, we want to return the 2D length.
            if (Double.isNaN(z0) || Double.isNaN(z1)) {
                dz = 0.0;
            } else {
                dz = z1 - z0;
            }

            length += Math.sqrt(dx * dx + dy * dy + dz * dz);
            x0 = x1;
            y0 = y1;
            z0 = z1;
        }
        return length;
    }
}
