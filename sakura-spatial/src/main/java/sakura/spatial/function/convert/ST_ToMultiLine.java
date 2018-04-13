package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.*;
import sakura.spatial.utils.GeometryFactories;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * ST_ToMultiLine constructs a MultiLineString from the given geometry's
 * coordinates. Returns MULTILINESTRING EMPTY for geometries of dimension 0.
 */
public class ST_ToMultiLine {

    /**
     * Constructs a MultiLineString from the given geometry's coordinates.
     *
     * @param geom Geometry
     * @return A MultiLineString constructed from the given geometry's coordinates
     * @throws SQLException
     */
    public static MultiLineString createMultiLineString(Geometry geom) throws SQLException {
        if (geom != null) {
            GeometryFactory gf = GeometryFactories.default_();
            if (geom.getDimension() > 0) {
                final List<LineString> lineStrings = new LinkedList<LineString>();
                toMultiLineString(geom, lineStrings);
                return gf.createMultiLineString(
                        lineStrings.toArray(new LineString[lineStrings.size()]));
            } else {
                return gf.createMultiLineString(null);
            }
        } else {
            return null;
        }
    }

    private static void toMultiLineString(final Geometry geometry,
                                          final List<LineString> lineStrings) throws SQLException {
        if ((geometry instanceof Point) || (geometry instanceof MultiPoint)) {
            throw new SQLException("Found a point! Cannot create a MultiLineString.");
        } else if (geometry instanceof LineString) {
            toMultiLineString((LineString) geometry, lineStrings);
        } else if (geometry instanceof Polygon) {
            toMultiLineString((Polygon) geometry, lineStrings);
        } else if (geometry instanceof GeometryCollection) {
            toMultiLineString((GeometryCollection) geometry, lineStrings);
        }
    }

    private static void toMultiLineString(final LineString lineString,
                                          final List<LineString> lineStrings) {
        lineStrings.add(lineString);
    }

    private static void toMultiLineString(final Polygon polygon,
                                          final List<LineString> lineStrings) {
        lineStrings.add(polygon.getExteriorRing());
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            lineStrings.add(polygon.getInteriorRingN(i));
        }
    }

    private static void toMultiLineString(final GeometryCollection geometryCollection,
                                          final List<LineString> lineStrings) throws SQLException {
        for (int i = 0; i < geometryCollection.getNumGeometries(); i++) {
            toMultiLineString(geometryCollection.getGeometryN(i), lineStrings);
        }
    }
}
