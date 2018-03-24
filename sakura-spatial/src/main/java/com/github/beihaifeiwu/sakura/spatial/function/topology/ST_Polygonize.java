package com.github.beihaifeiwu.sakura.spatial.function.topology;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

import java.util.Collection;

/**
 * Polygonizes a set of Geometry which contain linework that represents the edges of a planar graph
 */
public class ST_Polygonize {

    /**
     * Creates a GeometryCollection containing possible polygons formed
     * from the constituent linework of a set of geometries.
     *
     * @param geometry
     * @return
     */
    public static Geometry polygonize(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(geometry);
        Collection pols = polygonizer.getPolygons();
        if (pols.isEmpty()) {
            return null;
        }
        return GeometryFactories.default_().createMultiPolygon(GeometryFactory.toPolygonArray(pols));
    }
}
