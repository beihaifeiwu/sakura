package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.*;

/**
 * Replace the start and end z values of a linestring or multilinestring.
 * By default the other z values are interpolated according the length of the line.
 * Set false if you want to update only the start and end z values.
 */
public class ST_ZUpdateLineExtremities {

    public static Geometry updateZExtremities(Geometry geometry, double startZ, double endZ) {
        return updateZExtremities(geometry, startZ, endZ, true);

    }

    /**
     * Update the start and end Z values. If the interpolate is true the
     * vertices are interpolated according the start and end z values.
     *
     * @param geometry
     * @param startZ
     * @param endZ
     * @param interpolate
     * @return
     */
    public static Geometry updateZExtremities(Geometry geometry, double startZ, double endZ, boolean interpolate) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof LineString) {
            return force3DStartEnd((LineString) geometry, startZ, endZ, interpolate);
        } else if (geometry instanceof MultiLineString) {
            int nbGeom = geometry.getNumGeometries();
            LineString[] lines = new LineString[nbGeom];
            for (int i = 0; i < nbGeom; i++) {
                LineString subGeom = (LineString) geometry.getGeometryN(i);
                lines[i] = (LineString) force3DStartEnd(subGeom, startZ, endZ, interpolate);
            }
            return GeometryFactories.default_().createMultiLineString(lines);
        } else {
            return null;
        }
    }

    /**
     * Updates all z values by a new value using the specified first and the
     * last coordinates.
     *
     * @param lineString
     * @param startZ
     * @param endZ
     * @param interpolate is true the z value of the vertices are interpolate
     *                    according the length of the line.
     * @return
     */
    private static Geometry force3DStartEnd(LineString lineString, final double startZ,
                                            final double endZ, final boolean interpolate) {
        final double bigD = lineString.getLength();
        final double z = endZ - startZ;
        final Coordinate coordEnd = lineString.getCoordinates()[lineString.getCoordinates().length - 1];
        lineString.apply(new CoordinateSequenceFilter() {
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
                double x = seq.getX(i);
                double y = seq.getY(i);
                if (i == 0) {
                    seq.setOrdinate(i, 0, x);
                    seq.setOrdinate(i, 1, y);
                    seq.setOrdinate(i, 2, startZ);
                } else if (i == seq.size() - 1) {
                    seq.setOrdinate(i, 0, x);
                    seq.setOrdinate(i, 1, y);
                    seq.setOrdinate(i, 2, endZ);
                } else {
                    if (interpolate) {
                        double smallD = seq.getCoordinate(i).distance(coordEnd);
                        double factor = smallD / bigD;
                        seq.setOrdinate(i, 0, x);
                        seq.setOrdinate(i, 1, y);
                        seq.setOrdinate(i, 2, startZ + (factor * z));
                    }
                }
                if (i == seq.size()) {
                    done = true;
                }
            }
        });
        return lineString;
    }
}
