package sakura.spatial.function.clean;

import com.vividsolutions.jts.geom.Geometry;
import lombok.experimental.UtilityClass;


/**
 * Function to make a geometry valid.
 * <p>
 * Repair an invalid geometry.
 * If preserveGeomDim is true, makeValid will remove degenerated geometries from
 * the result, i.e geometries which dimension is lower than the input geometry
 * A multi-geometry will always produce a multi-geometry (eventually empty or made of a single component).
 * A simple geometry may produce a multi-geometry (ex. polygon with self-intersection will generally produce a multi-polygon).
 * In this case, it is up to the client to explode multi-geometries if he needs to. If preserveGeomDim is off,
 * it is up to the client to filter degenerate geometries.
 * WARNING : for geometries of dimension 1 (linear), duplicate coordinates are preserved as much as possible.
 * For geometries of dimension 2 (areal), duplicate coordinates are generally removed due to the use of overlay operations.
 */
@UtilityClass
public class ST_MakeValid {

    public static Geometry validGeom(Geometry geometry) {
        return validGeom(geometry, true, true, true);
    }


    public static Geometry validGeom(Geometry geometry, boolean preserveGeomDim) {
        return validGeom(geometry, preserveGeomDim, true, true);
    }

    public static Geometry validGeom(Geometry geometry, boolean preserveGeomDim, boolean preserveDuplicateCoord) {
        return validGeom(geometry, preserveGeomDim, preserveDuplicateCoord, true);
    }

    public static Geometry validGeom(Geometry geometry, boolean preserveGeomDim, boolean preserveDuplicateCoord, boolean preserveCoordDim) {
        if (geometry == null) {
            return null;
        }

        if (geometry.isEmpty()) {
            return geometry;
        }

        MakeValidOp op = new MakeValidOp();
        op.setPreserveGeomDim(preserveGeomDim);
        op.setPreserveDuplicateCoord(preserveDuplicateCoord);
        op.setPreserveCoordDim(preserveCoordDim);
        return op.makeValid(geometry);
    }
}
