
package com.github.beihaifeiwu.sakura.spatial.function.properties;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.github.beihaifeiwu.sakura.spatial.utils.CRS;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.cts.crs.CoordinateReferenceSystem;

/**
 * For geometry type returns minimum distance in meters between two geometries
 *
 * @author Michael MATUR
 */
public class ST_DistanceSphere {

    /**
     * @param a Geometry instance or null
     * @param b Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    public static Double distanceSphere(Geometry a, Geometry b) {
        if (a == null || b == null || (a.getSRID() != b.getSRID())) {
            return null;
        }

        int srid = a.getSRID();
        if (srid <= 0) {
            srid = 4326;
        }
        CoordinateReferenceSystem crs = CRS.getCRS(srid);

        if (!CoordinateReferenceSystem.Type.GEOGRAPHIC2D.equals(crs.getType())) {
            throw new SpatialException("ERROR: only lon/lag coordinate system are supported in geography");
        }

        Double radius = (2.0 * crs.getDatum().getEllipsoid().getSemiMajorAxis()
                + crs.getDatum().getEllipsoid().getSemiMinorAxis()) / 3.0;
        Double distance = distanceBetweenTwoGeometries(a, b);

        if (distance < 0.0) {
            return null;
        }

        return distance * radius;
    }

    /**
     * @param g1 Geometry instance or null
     * @param g2 Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    private static Double distanceBetweenTwoGeometries(Geometry g1, Geometry g2) {

        if ((g1 instanceof Point) && (g2 instanceof Point)) {
            return distancePointToPoint((Point) g1, (Point) g2);
        }

        if ((g1 instanceof Point && g2 instanceof LineString) ||
                (g2 instanceof Point && g1 instanceof LineString)) {
            return distancePointLine(g1, g2);
        }

        if (g1 instanceof LineString && g2 instanceof LineString) {
            return distanceLineLine(g1, g2);
        }

        if ((g1 instanceof Point && g2 instanceof Polygon) ||
                (g2 instanceof Point && g1 instanceof Polygon)) {
            return distancePointPolygon(g1, g2);
        }

        if ((g1 instanceof Polygon && g2 instanceof LineString) ||
                (g2 instanceof Polygon && g1 instanceof LineString)) {
            return distancePolygonLineString(g1, g2);
        }

        if (g1 instanceof Polygon && g2 instanceof Polygon) {
            return distancePolygonPolygon(g1, g2);
        }

        if (g1 instanceof GeometryCollection) {
            return distanceGeometryCollection(g1, g2);
        }

        if (g2 instanceof GeometryCollection) {
            return distanceGeometryCollection(g1, g2);

        }
        return -1.0;
    }

    /**
     * @param g1 Geometry instance or null
     * @param g2 Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    private static Double distanceLineLine(Geometry g1, Geometry g2) {
        Double distance = Double.MAX_VALUE;
        Double distancePoint;

        for (int i = 0; i < g1.getNumPoints(); i++) {
            for (int j = 0; j < g2.getNumPoints(); j++) {
                distancePoint = distancePointToPoint(((LineString) g1).getPointN(i), ((LineString) g2).getPointN(j));
                if (distancePoint < distance) {
                    distance = distancePoint;
                }
            }
        }
        return distance;
    }

    /**
     * @param g1 Geometry instance or null
     * @param g2 Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    private static Double distancePointPolygon(Geometry g1, Geometry g2) {
        Double distance = Double.MAX_VALUE;
        Double ringDistance;

        Point point;
        Polygon polygon;
        if (g1 instanceof Polygon) {
            point = (Point) g2;
            polygon = (Polygon) g1;
        } else {
            point = (Point) g1;
            polygon = (Polygon) g2;
        }
        if (polygon.covers(point)) {
            return 0.0;
        }

        for (int i = 0; i < polygon.getExteriorRing().getNumPoints(); i++) {
            ringDistance = distancePointToPoint(polygon.getExteriorRing().getPointN(i), point);
            if (ringDistance < distance) {
                distance = ringDistance;
            }

        }
        return distance;
    }

    /**
     * @param g1 Geometry instance or null
     * @param g2 Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    private static Double distancePolygonPolygon(Geometry g1, Geometry g2) {
        Double distance = Double.MAX_VALUE;
        Double ringDistance;
        if (g1.covers(g2) || g2.covers(g1)) {
            return 0.0;
        }
        for (int i = 0; i < ((Polygon) g1).getExteriorRing().getNumPoints(); i++) {
            for (int j = 0; j < ((Polygon) g2).getExteriorRing().getNumPoints(); j++) {
                ringDistance = distancePointToPoint(((Polygon) g1).getExteriorRing().getPointN(i),
                        ((Polygon) g2).getExteriorRing().getPointN(j));
                if (ringDistance < distance) {
                    distance = ringDistance;
                }
            }
        }
        return distance;
    }

    /**
     * @param g1 Geometry instance or null
     * @param g2 Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    private static Double distanceGeometryCollection(Geometry g1, Geometry g2) {
        Double distance = Double.MAX_VALUE;
        Double geomDistance;
        for (int i = 0; i < g2.getNumGeometries(); i++) {
            geomDistance = distanceBetweenTwoGeometries(g1, g2.getGeometryN(i));
            if (geomDistance < distance) {
                distance = geomDistance;
            }
        }
        return distance;
    }

    /**
     * @param g1 Geometry instance or null
     * @param g2 Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    private static Double distancePolygonLineString(Geometry g1, Geometry g2) {
        Double distance = Double.MAX_VALUE;
        Double ringDistance;
        LineString lineString;
        Polygon polygon;
        if (g1 instanceof Polygon) {
            lineString = (LineString) g2;
            polygon = (Polygon) g1;
        } else {
            lineString = (LineString) g1;
            polygon = (Polygon) g2;
        }
        for (int i = 0; i < polygon.getExteriorRing().getNumPoints(); i++) {
            ringDistance = distancePointLine(polygon.getExteriorRing().getPointN(i), lineString);
            if (ringDistance < distance) {
                distance = ringDistance;
            }

        }
        return distance;
    }

    /**
     * @param g1 Geometry instance or null
     * @param g2 Geometry instance or null
     * @return minimum distance in meters between two geometries
     */
    private static Double distancePointLine(Geometry g1, Geometry g2) {
        Double distance = Double.MAX_VALUE;
        Double distancePoint;
        Point point;
        LineString lineString;
        if (g1 instanceof Point) {
            point = (Point) g1;
            lineString = (LineString) g2;
        } else {
            point = (Point) g2;
            lineString = (LineString) g1;
        }

        for (int i = 0; i < lineString.getNumPoints(); i++) {
            distancePoint = distancePointToPoint(point, lineString.getPointN(i));
            if (distancePoint < distance) {
                distance = distancePoint;
            }
        }
        return distance;
    }

    /**
     * @param p1 Point instance
     * @param p2 Point instance
     * @return minimum distance between two points
     */
    private static Double distancePointToPoint(Point p1, Point p2) {
        Double p1X = longitudeRadiansNormalize(Math.toRadians(p1.getX()));
        Double p1Y = latitudeRadiansNormalize(Math.toRadians(p1.getY()));
        Double p2X = longitudeRadiansNormalize(Math.toRadians(p2.getX()));
        Double p2Y = latitudeRadiansNormalize(Math.toRadians(p2.getY()));

        Double dLon = p2X - p1X;
        Double cosDLon = Math.cos(dLon);
        Double cosLatP2 = Math.cos(p2Y);
        Double sinLatP2 = Math.sin(p2Y);
        Double cosLatP1 = Math.cos(p1Y);
        Double sinLatP1 = Math.sin(p1Y);

        Double a1 = Math.pow(cosLatP2 * Math.sin(dLon), 2);
        Double a2 = Math.pow(cosLatP1 * sinLatP2 - sinLatP1 * cosLatP2 * cosDLon, 2);
        Double a = Math.sqrt(a1 + a2);
        Double b = sinLatP1 * sinLatP2 + cosLatP1 * cosLatP2 * cosDLon;
        return Math.atan2(a, b);
    }

    /**
     * @param lon Double instance
     * @return normalized longitude
     */
    private static double longitudeRadiansNormalize(Double lon) {
        if (lon == -1.0 * Math.PI) {
            lon = Math.PI;
        }
        if (lon == -2.0 * Math.PI) {
            lon = 0.0;
        }
        if (lon > 2.0 * Math.PI) {
            lon = lon % (2.0 * Math.PI);
        }
        if (lon < -2.0 * Math.PI) {
            lon = lon % (-2.0 * Math.PI);
        }
        if (lon > Math.PI) {
            lon = -2.0 * Math.PI + lon;
        }
        if (lon < -1.0 * Math.PI) {
            lon = 2.0 * Math.PI + lon;
        }
        if (lon == -2.0 * Math.PI) {
            lon *= -1.0;
        }
        return lon;
    }

    /**
     * @param lat Double instance
     * @return normalized latitude
     */
    private static double latitudeRadiansNormalize(double lat) {
        if (lat > 2.0 * Math.PI) {
            lat = lat % (2.0 * Math.PI);
        }
        if (lat < -2.0 * Math.PI) {
            lat = lat % (-2.0 * Math.PI);
        }
        if (lat > Math.PI) {
            lat = Math.PI - lat;
        }
        if (lat < -1.0 * Math.PI) {
            lat = -1.0 * Math.PI - lat;
        }
        if (lat > (Math.PI / 2)) {
            lat = Math.PI - lat;
        }
        if (lat < -1.0 * (Math.PI / 2)) {
            lat = -1.0 * Math.PI - lat;
        }
        return lat;
    }
}
