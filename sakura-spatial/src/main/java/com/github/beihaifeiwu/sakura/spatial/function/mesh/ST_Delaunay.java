package com.github.beihaifeiwu.sakura.spatial.function.mesh;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;

import java.util.List;

/**
 * Returns polygons that represent a Delaunay triangulation constructed from a
 * collection of points. Note that the triangulation doesn't compute the
 * intersections between lines; it takes only existing coordinates.
 */
public class ST_Delaunay {

    /**
     * Build a delaunay triangulation based on all coordinates of the geometry
     *
     * @param geometry
     * @return a set of polygons (triangles)
     */
    public static GeometryCollection createDT(Geometry geometry) {
        return createDT(geometry, 0);
    }

    /**
     * Build a delaunay triangulation based on all coordinates of the geometry
     *
     * @param geometry
     * @param flag     for flag=0 (default flag) or a MULTILINESTRING for flag=1
     * @return a set of polygons (triangles)
     */
    public static GeometryCollection createDT(Geometry geometry, int flag) {
        if (geometry != null) {
            DelaunayTriangulationBuilder triangulationBuilder = new DelaunayTriangulationBuilder();
            triangulationBuilder.setSites(geometry);
            if (flag == 0) {
                return getTriangles(geometry.getFactory(), triangulationBuilder);
            } else {
                return (GeometryCollection) triangulationBuilder.getEdges(geometry.getFactory());
            }
        }
        return null;
    }

    private static GeometryCollection getTriangles(GeometryFactory geomFact,
                                                   DelaunayTriangulationBuilder delaunayTriangulationBuilder) {
        QuadEdgeSubdivision subdiv = delaunayTriangulationBuilder.getSubdivision();
        List triPtsList = subdiv.getTriangleCoordinates(false);
        Polygon[] tris = new Polygon[triPtsList.size()];
        int i = 0;
        for (Object aTriPtsList : triPtsList) {
            Coordinate[] triPt = (Coordinate[]) aTriPtsList;
            tris[i++] = geomFact.createPolygon(geomFact.createLinearRing(triPt), null);
        }
        return geomFact.createMultiPolygon(tris);
    }
}
