package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;


/**
 * This function add a z value to the z component of (each vertex of) the
 * geometric parameter to the corresponding value given by a field.
 */
public class ST_AddZ {

    /**
     * Add a z with to the existing value (do the sum). NaN values are not
     * updated.
     *
     * @param geometry
     * @param z
     * @return
     */
    public static Geometry addZ(Geometry geometry, double z) {
        if (geometry == null) {
            return null;
        }
        geometry.apply(new AddZCoordinateSequenceFilter(z));
        return geometry;
    }

    /**
     * Add a z value to each vertex of the Geometry.
     */
    public static class AddZCoordinateSequenceFilter implements CoordinateSequenceFilter {

        private boolean done = false;
        private final double z;

        public AddZCoordinateSequenceFilter(double z) {
            this.z = z;
        }

        @Override
        public boolean isGeometryChanged() {
            return true;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public void filter(CoordinateSequence seq, int i) {
            Coordinate coord = seq.getCoordinate(i);
            double currentZ = coord.z;
            if (!Double.isNaN(currentZ)) {
                seq.setOrdinate(i, 2, currentZ + z);
            }
            if (i == seq.size()) {
                done = true;
            }
        }
    }
}
