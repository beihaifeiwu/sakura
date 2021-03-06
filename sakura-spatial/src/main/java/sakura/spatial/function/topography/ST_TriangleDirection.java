package sakura.spatial.function.topography;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.math.Vector3D;
import sakura.spatial.utils.CoordinateUtils;
import sakura.spatial.utils.GeometryFactories;
import sakura.spatial.utils.TriMarkers;


/**
 * This function is used to compute the main slope direction on a triangle.
 *
 * @author Erwan Bocher
 */
public class ST_TriangleDirection {

    /**
     * Compute the main slope direction
     *
     * @param geometry
     * @return
     * @throws IllegalArgumentException
     */
    public static LineString computeDirection(Geometry geometry) throws IllegalArgumentException {
        if (geometry == null) {
            return null;
        }
        // Convert geometry into triangle
        Triangle triangle = TINFeatureFactory.createTriangle(geometry);
        // Compute slope vector
        Vector3D normal = TriMarkers.getNormalVector(triangle);
        Vector3D vector = new Vector3D(normal.getX(), normal.getY(), 0).normalize();
        // Compute equidistant point of triangle's sides
        Coordinate inCenter = triangle.centroid();
        // Interpolate Z value
        inCenter.setOrdinate(2, Triangle.interpolateZ(inCenter, triangle.p0, triangle.p1, triangle.p2));
        // Project slope from triangle center to triangle borders
        final LineSegment[] sides = new LineSegment[]{new LineSegment(triangle.p0, triangle.p1),
                new LineSegment(triangle.p1, triangle.p2), new LineSegment(triangle.p2, triangle.p0)};
        Coordinate pointIntersection = null;
        double nearestIntersection = Double.MAX_VALUE;
        for (LineSegment side : sides) {
            Coordinate intersection = CoordinateUtils.vectorIntersection(inCenter, vector, side.p0,
                    new Vector3D(side.p0, side.p1).normalize());
            double distInters = intersection == null ? Double.MAX_VALUE : side.distance(intersection);
            if (intersection != null && distInters < nearestIntersection) {
                pointIntersection = new Coordinate(
                        intersection.x,
                        intersection.y,
                        Triangle.interpolateZ(intersection, triangle.p0, triangle.p1, triangle.p2)
                );
                nearestIntersection = distInters;
            }
        }
        if (pointIntersection != null) {
            return GeometryFactories.default_().createLineString(new Coordinate[]{inCenter, pointIntersection});
        }
        return GeometryFactories.default_().createLineString(new Coordinate[]{});
    }

}
