package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/**
 * Returns the geometry with vertex order reversed.
 */
public class ST_Reverse {

    /**
     * Returns the geometry with vertex order reversed.
     *
     * @param geometry Geometry
     * @return geometry with vertex order reversed
     */
    public static Geometry reverse(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof MultiPoint) {
            return reverseMultiPoint((MultiPoint) geometry);
        }
        return geometry.reverse();
    }

    /**
     * Returns the MultiPoint with vertex order reversed. We do our own
     * implementation here because JTS does not handle it.
     *
     * @param mp MultiPoint
     * @return MultiPoint with vertex order reversed
     */
    public static Geometry reverseMultiPoint(MultiPoint mp) {
        int nPoints = mp.getNumGeometries();
        Point[] revPoints = new Point[nPoints];
        for (int i = 0; i < nPoints; i++) {
            revPoints[nPoints - 1 - i] = (Point) mp.getGeometryN(i).reverse();
        }
        return mp.getFactory().createMultiPoint(revPoints);
    }
}
