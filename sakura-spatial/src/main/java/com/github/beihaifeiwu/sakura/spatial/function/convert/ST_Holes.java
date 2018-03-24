package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;

/**
 * ST_Holes returns the given geometry or geometry collection's holes as a
 * GeometryCollection. Returns GEOMETRYCOLLECTION EMPTY for geometries of
 * dimension less than 2.
 */
@UtilityClass
public class ST_Holes {

    /**
     * Returns the given geometry's holes as a GeometryCollection.
     *
     * @param geom Geometry
     * @return The geometry's holes
     */
    public static GeometryCollection getHoles(Geometry geom) {
        if (geom != null) {
            GeometryFactory gf = GeometryFactories.default_();
            if (geom.getDimension() >= 2) {
                ArrayList<Geometry> holes = new ArrayList<Geometry>();
                for (int i = 0; i < geom.getNumGeometries(); i++) {
                    Geometry subgeom = geom.getGeometryN(i);
                    if (subgeom instanceof Polygon) {
                        Polygon polygon = (Polygon) subgeom;
                        for (int j = 0; j < polygon.getNumInteriorRing(); j++) {
                            holes.add(gf.createPolygon(
                                    gf.createLinearRing(
                                            polygon.getInteriorRingN(j).getCoordinates()), null));
                        }
                    }
                }
                return gf.createGeometryCollection(
                        holes.toArray(new Geometry[holes.size()]));
            } else {
                return gf.createGeometryCollection(null);
            }
        }
        return null;
    }
}
