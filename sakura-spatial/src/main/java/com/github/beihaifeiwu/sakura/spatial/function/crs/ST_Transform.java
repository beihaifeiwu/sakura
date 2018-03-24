package com.github.beihaifeiwu.sakura.spatial.function.crs;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.utils.CRS;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import lombok.experimental.UtilityClass;
import org.cts.IllegalCoordinateException;
import org.cts.op.CoordinateOperation;

import java.util.Objects;


/**
 * This class is used to transform a geometry from one CRS to another.
 * Only integer codes available in the EPSGRegistry are allowed.
 * The default source CRS is the input geometry's internal CRS.
 */
@UtilityClass
public class ST_Transform {

    /**
     * Returns a new geometry transformed to the SRID referenced by the integer
     * parameter available in the spatial_ref_sys table
     *
     * @param geom
     * @param targetSRID
     * @return
     */
    public static Geometry transform(Geometry geom, Integer srcSRID, Integer targetSRID) {
        if (geom == null) {
            return null;
        }
        if (targetSRID == null) {
            throw new IllegalArgumentException("The SRID code cannot be null.");
        }
        if (srcSRID == null) {
            srcSRID = geom.getSRID();
        }
        if (srcSRID == 0) {
            throw new SpatialException("Cannot find srid in " + geom);
        } else {
            if (Objects.equals(srcSRID, targetSRID)) {
                return geom;
            }
            CoordinateOperation op = CRS.getCOP(srcSRID, targetSRID);
            if (op != null) {
                Geometry outPutGeom = (Geometry) geom.clone();
                outPutGeom.apply(new CRSTransformFilter(op));
                outPutGeom.setSRID(targetSRID);
                return outPutGeom;
            } else {
                throw new SpatialException("The transformation from "
                        + srcSRID + " to " + targetSRID + " is not yet supported.");
            }
        }
    }

    public static Geometry transform(Geometry geom, Integer targetSRID) {
        return transform(geom, null, targetSRID);
    }

    /**
     * This method is used to apply a {@link CoordinateOperation} to a geometry.
     * The transformation loops on each coordinate.
     */
    public static class CRSTransformFilter implements CoordinateFilter {

        private final CoordinateOperation coordinateOperation;

        public CRSTransformFilter(final CoordinateOperation coordinateOperation) {
            this.coordinateOperation = coordinateOperation;
        }

        @Override
        public void filter(Coordinate coord) {
            try {
                boolean zIsNaN = Double.isNaN(coord.z);
                if (zIsNaN) {
                    coord.z = 0;
                }
                double[] xyz = coordinateOperation.transform(new double[]{ coord.x, coord.y, coord.z });
                coord.x = xyz[0];
                coord.y = xyz[1];
                if (xyz.length > 2 && !zIsNaN) {
                    coord.z = xyz[2];
                } else {
                    coord.z = Double.NaN;
                }
            } catch (IllegalCoordinateException ice) {
                throw new SpatialException("Cannot transform the coordinate" + coord.toString(), ice);
            }

        }


    }
}