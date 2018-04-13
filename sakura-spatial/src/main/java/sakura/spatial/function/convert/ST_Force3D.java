package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Forces a Geometry into 3D mode by returning a copy with
 * {@link Double.NaN} z-coordinates set to 0 (other z-coordinates are left untouched).
 */
public class ST_Force3D {

    /**
     * Converts a XY geometry to XYZ. If a geometry has no Z component, then a 0
     * Z coordinate is tacked on.
     *
     * @param geom
     * @return
     */
    public static Geometry force3D(Geometry geom) {
        if (geom == null) {
            return null;
        }
        Geometry outPut = (Geometry) geom.clone();
        outPut.apply(new CoordinateSequenceFilter() {
            private boolean done = false;

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
                double z = coord.z;
                if (Double.isNaN(z)) {
                    seq.setOrdinate(i, 2, 0);
                }
                if (i == seq.size()) {
                    done = true;
                }
            }
        });
        return outPut;
    }
}
