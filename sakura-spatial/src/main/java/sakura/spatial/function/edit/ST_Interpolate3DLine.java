package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.*;
import sakura.spatial.utils.GeometryFactories;

/**
 * Interpolate a 1 dimension geometry according its start and end z values.
 * <p>
 * Interpolate the z values of a linestring or multilinestring based on
 * the start and the end z values. If the z values are equal to NaN return the
 * input geometry.
 *
 * @author Erwan Bocher
 */
public class ST_Interpolate3DLine {

    /**
     * @param geometry
     * @return
     */
    public static Geometry interpolateLine(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof LineString) {
            return linearZInterpolation((LineString) geometry);
        } else if (geometry instanceof MultiLineString) {
            return linearZInterpolation((MultiLineString) geometry);
        }
        return null;
    }

    /**
     * Interpolate a linestring according the start and the end coordinates z
     * value. If the start or the end z is NaN return the input linestring
     *
     * @param lineString
     * @return
     */
    private static LineString linearZInterpolation(LineString lineString) {
        double startz = lineString.getStartPoint().getCoordinate().z;
        double endz = lineString.getEndPoint().getCoordinate().z;
        if (Double.isNaN(startz) || Double.isNaN(endz)) {
            return lineString;
        } else {
            double length = lineString.getLength();
            lineString.apply(new LinearZInterpolationFilter(startz, endz, length));
            return lineString;
        }
    }

    /**
     * Interpolate each linestring of the multilinestring.
     *
     * @param multiLineString
     * @return
     */
    private static MultiLineString linearZInterpolation(MultiLineString multiLineString) {
        int nbGeom = multiLineString.getNumGeometries();
        LineString[] lines = new LineString[nbGeom];
        for (int i = 0; i < nbGeom; i++) {
            LineString subGeom = (LineString) multiLineString.getGeometryN(i);
            double startz = subGeom.getStartPoint().getCoordinates()[0].z;
            double endz = subGeom.getEndPoint().getCoordinates()[0].z;
            double length = subGeom.getLength();
            subGeom.apply(new LinearZInterpolationFilter(startz, endz, length));
            lines[i] = subGeom;

        }
        return GeometryFactories.default_().createMultiLineString(lines);
    }

    /**
     * Interpolate the z values according a start and a end z values.
     */
    private static class LinearZInterpolationFilter implements CoordinateSequenceFilter {

        private boolean done = false;
        private double startZ = 0;
        private double endZ = 0;
        private double dZ = 0;
        private final double length;
        private int seqSize = 0;
        private double sumLenght = 0;

        LinearZInterpolationFilter(double startZ, double endZ, double length) {
            this.startZ = startZ;
            this.endZ = endZ;
            this.length = length;

        }

        @Override
        public void filter(CoordinateSequence seq, int i) {
            if (i == 0) {
                seqSize = seq.size();
                dZ = endZ - startZ;
            } else if (i == seqSize) {
                done = true;
            } else {
                Coordinate coord = seq.getCoordinate(i);
                Coordinate previousCoord = seq.getCoordinate(i - 1);
                sumLenght += coord.distance(previousCoord);
                seq.setOrdinate(i, 2, startZ + dZ * sumLenght / length);
            }

        }

        @Override
        public boolean isGeometryChanged() {
            return true;
        }

        @Override
        public boolean isDone() {
            return done;
        }
    }
}
