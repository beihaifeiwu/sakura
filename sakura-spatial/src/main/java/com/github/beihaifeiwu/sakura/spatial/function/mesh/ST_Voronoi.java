package com.github.beihaifeiwu.sakura.spatial.function.mesh;

import com.github.beihaifeiwu.sakura.spatial.utils.Voronoi;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Triangle;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdge;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;
import com.vividsolutions.jts.triangulate.quadedge.TriangleVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Construct a voronoi diagram from a delaunay triangulation or a set of points.
 */
public class ST_Voronoi {
    private static final int DEFAULT_DIMENSION = 2;

    public static GeometryCollection voronoi(Geometry geomCollection) {
        return voronoi(geomCollection, DEFAULT_DIMENSION);
    }

    public static GeometryCollection voronoi(Geometry geomCollection, int outputDimension) {
        return voronoi(geomCollection, outputDimension, null);
    }

    private static GeometryCollection returnEmptyCollection(int outputDimension) {
        switch (outputDimension) {
            case 2:
                return new GeometryFactory().createMultiPolygon(new Polygon[0]);
            case 1:
                return new GeometryFactory().createMultiLineString(new LineString[0]);
            default:
                return new GeometryFactory().createMultiPoint(new Point[0]);
        }
    }

    public static GeometryCollection voronoi(Geometry geomCollection, int outputDimension, Geometry envelope) {
        if (geomCollection == null) {
            return returnEmptyCollection(outputDimension);
        }
        if (geomCollection instanceof MultiPoint || (geomCollection instanceof GeometryCollection &&
                geomCollection.getNumGeometries() > 0 && geomCollection.getGeometryN(0) instanceof Point)) {
            // From point set use JTS
            VoronoiDiagramBuilder diagramBuilder = new VoronoiDiagramBuilder();
            diagramBuilder.setSites(geomCollection);
            if (envelope != null) {
                diagramBuilder.setClipEnvelope(envelope.getEnvelopeInternal());
            }
            if (outputDimension == 2) {
                // Output directly the polygons
                return (GeometryCollection) diagramBuilder.getDiagram(geomCollection.getFactory());
            } else if (outputDimension == 1) {
                // Convert into lineStrings.
                return mergeTrianglesEdges((GeometryCollection) diagramBuilder.getDiagram(geomCollection.getFactory()));
            } else {
                // Extract triangles Circumcenter
                QuadEdgeSubdivision subdivision = diagramBuilder.getSubdivision();
                List<Coordinate> circumcenter = new ArrayList<Coordinate>(geomCollection.getNumGeometries());
                subdivision.visitTriangles(new TriangleVisitorCircumCenter(circumcenter), false);
                return geomCollection.getFactory().createMultiPoint(circumcenter.toArray(new Coordinate[circumcenter.size()]));
            }
        } else {
            if (Double.compare(geomCollection.getEnvelopeInternal().getArea(), 0d) == 0) {
                return returnEmptyCollection(outputDimension);
            }
            // Triangle input use internal method
            Voronoi voronoi = new Voronoi();
            if (envelope != null) {
                voronoi.setEnvelope(envelope.getEnvelopeInternal());
            }
            voronoi.generateTriangleNeighbors(geomCollection);
            return voronoi.generateVoronoi(outputDimension);
        }
    }

    private static MultiLineString mergeTrianglesEdges(GeometryCollection polygons) {
        GeometryFactory factory = polygons.getFactory();
        Set<LineSegment> segments = new HashSet<LineSegment>(polygons.getNumGeometries());
        SegmentMerge segmentMerge = new SegmentMerge(segments);
        for (int idGeom = 0; idGeom < polygons.getNumGeometries(); idGeom++) {
            Geometry polygonGeom = polygons.getGeometryN(idGeom);
            if (polygonGeom instanceof Polygon) {
                Polygon polygon = (Polygon) polygonGeom;
                segmentMerge.reset();
                polygon.getExteriorRing().apply(segmentMerge);
            }
        }
        // Convert segments into multilinestring
        LineString[] lineStrings = new LineString[segments.size()];
        int idLine = 0;
        for (LineSegment lineSegment : segments) {
            lineStrings[idLine++] = factory.createLineString(new Coordinate[]{ lineSegment.p0, lineSegment.p1 });
        }
        segments.clear();
        return factory.createMultiLineString(lineStrings);
    }

    private static class TriangleVisitorCircumCenter implements TriangleVisitor {
        private List<Coordinate> circumCenters;

        TriangleVisitorCircumCenter(List<Coordinate> circumCenters) {
            this.circumCenters = circumCenters;
        }

        @Override
        public void visit(QuadEdge[] triEdges) {
            Coordinate a = triEdges[0].orig().getCoordinate();
            Coordinate b = triEdges[1].orig().getCoordinate();
            Coordinate c = triEdges[2].orig().getCoordinate();
            circumCenters.add(Triangle.circumcentre(a, b, c));
        }
    }

    private static class SegmentMerge implements CoordinateFilter {
        private Set<LineSegment> segments;
        private Coordinate firstPt = null;

        SegmentMerge(Set<LineSegment> segments) {
            this.segments = segments;
        }

        @Override
        public void filter(Coordinate coord) {
            if (firstPt != null) {
                LineSegment segment = new LineSegment(firstPt, coord);
                segment.normalize();
                segments.add(segment);
            }
            firstPt = coord;
        }

        void reset() {
            firstPt = null;
        }
    }
}
