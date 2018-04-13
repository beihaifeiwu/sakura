package sakura.spatial.function.properties;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import sakura.spatial.SpatialException;


/**
 * Returns the <i>n</i>th point of a LINESTRING or a MULTILINESTRING containing
 * exactly one LINESTRING; NULL otherwise. As the OGC specifies, ST_PointN is
 * 1-N based.
 */
public class ST_PointN {

    private static final String OUT_OF_BOUNDS_ERR_MESSAGE =
            "Point index out of range. Must be between 1 and ST_NumPoints.";

    /**
     * @param geometry   Geometry instance
     * @param pointIndex Point index [1-NbPoints]
     * @return Returns the <i>n</i>th point of a LINESTRING or a
     * MULTILINESTRING containing exactly one LINESTRING; NULL otherwise. As
     * the OGC specifies, ST_PointN is 1-N based.
     * @throws SpatialException if index is out of bound.
     */
    public static Geometry getPointN(Geometry geometry, int pointIndex) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof MultiLineString) {
            if (geometry.getNumGeometries() == 1) {
                return getPointNFromLine((LineString) geometry.getGeometryN(0), pointIndex);
            }
        } else if (geometry instanceof LineString) {
            return getPointNFromLine((LineString) geometry, pointIndex);
        }
        return null;
    }

    private static Geometry getPointNFromLine(LineString line, int pointIndex) {
        if (pointIndex <= 0 || pointIndex <= line.getNumPoints()) {
            return line.getPointN(pointIndex - 1);
        } else {
            throw new SpatialException(OUT_OF_BOUNDS_ERR_MESSAGE);
        }
    }
}
