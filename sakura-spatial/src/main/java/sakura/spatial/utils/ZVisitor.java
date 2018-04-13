package sakura.spatial.utils;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;

/**
 * A visitor that checks if there is a Z coordinate.
 */
public class ZVisitor implements CoordinateSequenceFilter {
    private boolean foundZ;

    public boolean isFoundZ() {
        return foundZ;
    }

    @Override
    public void filter(CoordinateSequence coordinateSequence, int i) {
        if (!Double.isNaN(coordinateSequence.getOrdinate(i, 2))) {
            foundZ = true;
        }
    }

    @Override
    public boolean isDone() {
        return foundZ;
    }

    @Override
    public boolean isGeometryChanged() {
        return false;
    }

}