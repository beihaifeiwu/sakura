package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Flip the X and Y coordinates of the geometry
 * Returns a version of the given geometry with X and Y axis flipped. Useful for people who have built
 * latitude/longitude features and need to fix them.
 *
 * @author Erwan Bocher
 */
public class ST_FlipCoordinates {

    public static Geometry flipCoordinates(Geometry geom) {
        if (geom != null) {
            geom.apply(new FlipCoordinateSequenceFilter());
            return geom;
        }
        return null;

    }

    /**
     * Returns a version of the given geometry with X and Y axis flipped.
     */
    public static class FlipCoordinateSequenceFilter implements CoordinateSequenceFilter {

        private boolean done = false;

        @Override
        public void filter(CoordinateSequence seq, int i) {
            double x = seq.getOrdinate(i, 0);
            double y = seq.getOrdinate(i, 1);
            seq.setOrdinate(i, 0, y);
            seq.setOrdinate(i, 1, x);
            if (i == seq.size()) {
                done = true;
            }
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public boolean isGeometryChanged() {
            return true;
        }
    }
}
