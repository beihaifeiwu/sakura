package sakura.spatial.function.aggregate;

import com.vividsolutions.jts.geom.*;
import sakura.spatial.utils.GeometryFactories;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Construct an array of Geometries.
 */
public class ST_Accum {
    private List<Geometry> toUnite = new LinkedList<Geometry>();
    private int minDim = Integer.MAX_VALUE;
    private int maxDim = Integer.MIN_VALUE;

    private void feedDim(Geometry geometry) {
        final int geomDim = geometry.getDimension();
        maxDim = Math.max(maxDim, geomDim);
        minDim = Math.min(minDim, geomDim);
    }

    /**
     * Add geometry into an array to accumulate
     *
     * @param geom
     */
    private void addGeometry(Geometry geom) {
        if (geom != null) {
            if (geom instanceof GeometryCollection) {
                List<Geometry> toUnitTmp = new ArrayList<Geometry>(geom.getNumGeometries());
                for (int i = 0; i < geom.getNumGeometries(); i++) {
                    toUnitTmp.add(geom.getGeometryN(i));
                    feedDim(geom.getGeometryN(i));
                }
                toUnite.addAll(toUnitTmp);
            } else {
                toUnite.add(geom);
                feedDim(geom);
            }
        }
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    public GeometryCollection getResult() {
        GeometryFactory factory = GeometryFactories.default_();
        if (maxDim != minDim) {
            return factory.createGeometryCollection(toUnite.toArray(new Geometry[toUnite.size()]));
        } else {
            switch (maxDim) {
                case 0:
                    return factory.createMultiPoint(toUnite.toArray(new Point[toUnite.size()]));
                case 1:
                    return factory.createMultiLineString(toUnite.toArray(new LineString[toUnite.size()]));
                default:
                    return factory.createMultiPolygon(toUnite.toArray(new Polygon[toUnite.size()]));
            }
        }
    }

    /**
     * This aggregate function returns a GeometryCollection from a column of mixed dimension Geometries.
     * If there is only POINTs in the column of Geometries, a MULTIPOINT is returned.
     * Same process with LINESTRINGs and POLYGONs.
     */
    public static GeometryCollection accum(Geometry... geometries) {
        ST_Accum accum = new ST_Accum();
        for (Geometry geometry : geometries) {
            accum.addGeometry(geometry);
        }
        return accum.getResult();
    }
}
