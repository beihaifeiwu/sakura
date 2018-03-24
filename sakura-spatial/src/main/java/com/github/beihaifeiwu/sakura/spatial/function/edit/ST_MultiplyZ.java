package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This function do a multiplication with the z value of (each vertex of) the
 * geometric parameter to the corresponding value given by a field.
 */
public class ST_MultiplyZ {

    /**
     * Multiply the z values of the geometry by another double value. NaN values
     * are not updated.
     *
     * @param geometry
     * @param z
     * @return
     */
    public static Geometry multiplyZ(Geometry geometry, double z) {
        if (geometry == null) {
            return null;
        }
        geometry.apply(new MultiplyZCoordinateSequenceFilter(z));
        return geometry;
    }

    /**
     * Multiply the z value of each vertex of the Geometry by a double value.
     */
    public static class MultiplyZCoordinateSequenceFilter implements CoordinateSequenceFilter {

        private boolean done = false;
        private final double z;

        public MultiplyZCoordinateSequenceFilter(double z) {
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
                seq.setOrdinate(i, 2, currentZ * z);
            }
            if (i == seq.size()) {
                done = true;
            }
        }
    }
}
