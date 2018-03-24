package com.github.beihaifeiwu.sakura.spatial.function.edit;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.prep.PreparedPolygon;
import com.vividsolutions.jts.geom.util.GeometryEditor;

/**
 * Remove all points on a geometry that are located within a polygon.
 */
public class ST_RemovePoints {

    /**
     * Remove all vertices that are located within a polygon
     *
     * @param geometry
     * @param polygon
     * @return
     */
    public static Geometry removePoint(Geometry geometry, Polygon polygon) {
        if (geometry == null) {
            return null;
        }
        GeometryEditor localGeometryEditor = new GeometryEditor();
        PolygonDeleteVertexOperation localBoxDeleteVertexOperation =
                new PolygonDeleteVertexOperation(geometry.getFactory(), new PreparedPolygon(polygon));
        Geometry localGeometry = localGeometryEditor.edit(geometry, localBoxDeleteVertexOperation);
        if (localGeometry.isEmpty()) {
            return null;
        }
        return localGeometry;
    }


    /**
     * This class is used to remove vertexes that are contained into a polygon.
     */
    private static class PolygonDeleteVertexOperation extends GeometryEditor.CoordinateOperation {

        private final GeometryFactory gf;
        //This polygon used to select the coordinates to removed
        private PreparedPolygon polygon;

        PolygonDeleteVertexOperation(GeometryFactory gf, PreparedPolygon polygon) {
            this.polygon = polygon;
            this.gf = gf;
        }

        @Override
        public Coordinate[] edit(Coordinate[] paramArrayOfCoordinate, Geometry paramGeometry) {
            if (!this.polygon.intersects(paramGeometry)) {
                return paramArrayOfCoordinate;
            }
            Coordinate[] arrayOfCoordinate1 = new Coordinate[paramArrayOfCoordinate.length];
            int j = 0;
            for (Coordinate coordinate : paramArrayOfCoordinate) {
                if (!this.polygon.contains(gf.createPoint(coordinate))) {
                    arrayOfCoordinate1[(j++)] = coordinate;
                }
            }

            Coordinate[] arrayOfCoordinate2 = CoordinateArrays.removeNull(arrayOfCoordinate1);
            Coordinate[] localObject = arrayOfCoordinate2;
            if (((paramGeometry instanceof LinearRing)) && (arrayOfCoordinate2.length > 1)
                    && (!arrayOfCoordinate2[(arrayOfCoordinate2.length - 1)].equals2D(arrayOfCoordinate2[0]))) {
                Coordinate[] arrayOfCoordinate3 = new Coordinate[arrayOfCoordinate2.length + 1];
                CoordinateArrays.copyDeep(arrayOfCoordinate2, 0, arrayOfCoordinate3, 0, arrayOfCoordinate2.length);
                arrayOfCoordinate3[(arrayOfCoordinate3.length - 1)] = new Coordinate(arrayOfCoordinate3[0]);
                localObject = arrayOfCoordinate3;
            }
            return localObject;
        }
    }
}
