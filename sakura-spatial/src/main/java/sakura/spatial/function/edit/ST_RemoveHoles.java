package sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.*;
import sakura.spatial.utils.GeometryFactories;

/**
 * Removes any holes from a polygon or multipolygon
 */
public class ST_RemoveHoles {

    /**
     * Remove any holes from the geometry. If the geometry doesn't contain any
     * holes, return it unchanged.
     *
     * @param geometry Geometry
     * @return Geometry with no holes *
     */
    @SuppressWarnings("ConstantConditions")
    public static Geometry removeHoles(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry instanceof Polygon) {
            return removeHolesPolygon((Polygon) geometry);
        } else if (geometry instanceof MultiPolygon) {
            return removeHolesMultiPolygon((MultiPolygon) geometry);
        } else if (geometry instanceof GeometryCollection) {
            Geometry[] geometries = new Geometry[geometry.getNumGeometries()];
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                Geometry geom = geometry.getGeometryN(i);
                if (geometry instanceof Polygon) {
                    geometries[i] = removeHolesPolygon((Polygon) geom);
                } else if (geometry instanceof MultiPolygon) {
                    geometries[i] = removeHolesMultiPolygon((MultiPolygon) geom);
                } else {
                    geometries[i] = geom;
                }
            }
            return GeometryFactories.default_().createGeometryCollection(geometries);
        }
        return null;
    }

    /**
     * Create a new multiPolygon without hole.
     *
     * @param multiPolygon
     * @return
     */
    public static MultiPolygon removeHolesMultiPolygon(MultiPolygon multiPolygon) {
        int num = multiPolygon.getNumGeometries();
        Polygon[] polygons = new Polygon[num];
        for (int i = 0; i < num; i++) {
            polygons[i] = removeHolesPolygon((Polygon) multiPolygon.getGeometryN(i));
        }
        return multiPolygon.getFactory().createMultiPolygon(polygons);
    }

    /**
     * Create a new polygon without hole.
     *
     * @param polygon
     * @return
     */
    public static Polygon removeHolesPolygon(Polygon polygon) {
        return new Polygon((LinearRing) polygon.getExteriorRing(), null, polygon.getFactory());
    }
}
