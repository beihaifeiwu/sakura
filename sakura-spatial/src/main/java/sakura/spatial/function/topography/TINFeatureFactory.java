package sakura.spatial.function.topography;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Triangle;
import lombok.experimental.UtilityClass;

/**
 * A factory used to create jDelaunay objects from JTS geometries.
 */
@UtilityClass
public final class TINFeatureFactory {
    public static final double EPSILON = 1e-12;

    /**
     * A factory to create a DTriangle from a Geometry
     *
     * @param geom
     * @return If the triangle can't be generated
     * @throws IllegalArgumentException If there are not exactly 3 coordinates in geom.
     */
    public static Triangle createTriangle(Geometry geom) throws IllegalArgumentException {

        Coordinate[] coordinates = geom.getCoordinates();
        if (coordinates.length != 4) {
            throw new IllegalArgumentException("The geometry must be a triangle");
        }
        return new Triangle(coordinates[0], coordinates[1], coordinates[2]);
    }

}
