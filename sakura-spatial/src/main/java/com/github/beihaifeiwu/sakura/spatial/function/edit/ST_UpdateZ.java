package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;


/**
 * This function replace the z component of (each vertex of) the geometric
 * parameter to the corresponding value given by a field.
 */
public class ST_UpdateZ {

    /**
     * Replace the z with same value. NaN values are also updated.
     *
     * @param geometry
     * @param z
     * @return
     */
    public static Geometry updateZ(Geometry geometry, double z) {
        return updateZ(geometry, z, 1);
    }

    /**
     * Replace the z value depending on the condition.
     *
     * @param geometry
     * @param z
     * @param updateCondition set if the NaN value must be updated or not
     * @return
     */
    public static Geometry updateZ(Geometry geometry, double z, int updateCondition) {
        if (geometry == null) {
            return null;
        }
        if (updateCondition == 1 || updateCondition == 2 || updateCondition == 3) {
            geometry.apply(new UpdateZCoordinateSequenceFilter(z, updateCondition));
            return geometry;
        } else {
            throw new SpatialException("Available values are 1, 2 or 3.\n"
                    + "Please read the description of the function to use it.");
        }
    }

    /**
     * Replaces the z value to each vertex of the Geometry.
     * If the condition is equal to 1, replace all z.
     * If the consition is equal to 2 replace only not NaN z.
     * If the condition is equal to 3 replace only NaN z.
     */
    public static class UpdateZCoordinateSequenceFilter implements CoordinateSequenceFilter {

        private boolean done = false;
        private final double z;
        private final int condition;

        public UpdateZCoordinateSequenceFilter(double z, int condition) {
            this.z = z;
            this.condition = condition;
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
            if (condition == 1) {
                seq.setOrdinate(i, 2, z);
            } else if (condition == 2) {
                Coordinate coord = seq.getCoordinate(i);
                double currentZ = coord.z;
                if (!Double.isNaN(currentZ)) {
                    seq.setOrdinate(i, 2, z);
                }
            } else if (condition == 3) {
                Coordinate coord = seq.getCoordinate(i);
                double currentZ = coord.z;
                if (Double.isNaN(currentZ)) {
                    seq.setOrdinate(i, 2, z);
                }
            } else {
                done = true;
            }
            if (i == seq.size()) {
                done = true;
            }
        }
    }
}
