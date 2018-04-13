package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;

/**
 * Common utilities used by the edit functions
 */
public class EditUtilities {

    /**
     * Gets the coordinate of a Geometry that is the nearest of a given Point,
     * with a distance tolerance.
     *
     * @param g
     * @param p
     * @param tolerance
     * @return
     */
    public static GeometryLocation getVertexToSnap(Geometry g, Point p, double tolerance) {
        DistanceOp distanceOp = new DistanceOp(g, p);
        GeometryLocation snapedPoint = distanceOp.nearestLocations()[0];
        if (tolerance == 0 || snapedPoint.getCoordinate().distance(p.getCoordinate()) <= tolerance) {
            return snapedPoint;
        }
        return null;

    }
}
