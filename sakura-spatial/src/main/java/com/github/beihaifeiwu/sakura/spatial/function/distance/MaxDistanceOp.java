package com.github.beihaifeiwu.sakura.spatial.function.distance;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Arrays;
import java.util.HashSet;

/**
 * An operation to compute the maximum distance between two geometries.
 * If the geometry 1 and geometry 2 is the same geometry the operation will
 * return the distance between the two vertices most far from each other in that geometry.
 *
 * @author Erwan Bocher
 */
public class MaxDistanceOp {

    private final Geometry geomA;
    private final Geometry geomB;
    private MaxDistanceFilter maxDistanceFilter;
    private boolean sameGeom = false;

    public MaxDistanceOp(Geometry geomA, Geometry geomB) {
        this.geomA = geomA;
        this.geomB = geomB;
    }

    /**
     * Compute the max distance
     */
    private void computeMaxDistance() {
        HashSet<Coordinate> coordinatesA = new HashSet<Coordinate>();
        coordinatesA.addAll(Arrays.asList(geomA.convexHull().getCoordinates()));
        Geometry fullHull = geomA.getFactory().createGeometryCollection(new Geometry[]{ geomA, geomB }).convexHull();
        maxDistanceFilter = new MaxDistanceFilter(coordinatesA);
        fullHull.apply(maxDistanceFilter);
    }

    /**
     * Return the max distance
     *
     * @return
     */
    public Double getDistance() {
        if (geomA == null || geomB == null) {
            return null;
        }
        if (geomA.isEmpty() || geomB.isEmpty()) {
            return 0.0;
        }
        if (geomA.equals(geomB)) {
            sameGeom = true;
        }
        if (maxDistanceFilter == null) {
            computeMaxDistance();
        }
        return maxDistanceFilter.getDistance();

    }

    /**
     * Return the two coordinates to build the max distance line
     *
     * @return
     */
    public Coordinate[] getCoordinatesDistance() {
        if (geomA == null || geomB == null) {
            return null;
        }
        if (geomA.isEmpty() || geomB.isEmpty()) {
            return null;
        }
        if (geomA.equals(geomB)) {
            sameGeom = true;
        }

        if (maxDistanceFilter == null) {
            computeMaxDistance();
        }
        return maxDistanceFilter.getCoordinatesDistance();

    }

    public class MaxDistanceFilter implements CoordinateFilter {

        private double distance = Double.MIN_VALUE;
        private final HashSet<Coordinate> coordsToExclude;
        private Coordinate startCoord = null;
        private Coordinate endCoord = null;

        /**
         * Compute the max distance between two geometries
         *
         * @param coordsToExclude
         */
        public MaxDistanceFilter(HashSet<Coordinate> coordsToExclude) {
            this.coordsToExclude = coordsToExclude;
        }

        @Override
        public void filter(Coordinate coord) {
            if (sameGeom) {
                coordsToExclude.remove(coord);
                updateDistance(coord);
            } else {
                if (!coordsToExclude.contains(coord)) {
                    updateDistance(coord);
                }
            }
        }

        /**
         * Update the distance and the coordinates
         *
         * @param coord
         */
        private void updateDistance(Coordinate coord) {
            for (Coordinate coordinate : coordsToExclude) {
                double currentDistance = coord.distance(coordinate);
                if (currentDistance > distance) {
                    distance = currentDistance;
                    startCoord = coordinate;
                    endCoord = coord;
                }
            }
        }

        /**
         * Return the maximum distance
         *
         * @return
         */
        public double getDistance() {
            return distance;
        }

        /**
         * Return the maximum distance as two coordinates.
         * Usefull to draw it as a line
         *
         * @return
         */
        public Coordinate[] getCoordinatesDistance() {
            if (startCoord == null || endCoord == null) {
                return null;
            }
            return new Coordinate[]{ startCoord, endCoord };
        }
    }

}