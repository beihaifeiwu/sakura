package com.github.beihaifeiwu.sakura.spatial.function.create;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Creates a rectangular POLYGON formed from the given x and y minima.
 * The user may specify an SRID; if no SRID is specified the unknown spatial reference system is assumed.
 */
public class ST_MakeEnvelope {

    /**
     * Creates a rectangular Polygon formed from the minima and maxima by the
     * given shell.
     *
     * @param xmin X min
     * @param ymin Y min
     * @param xmax X max
     * @param ymax Y max
     * @return Envelope as a POLYGON
     */
    public static Polygon makeEnvelope(double xmin, double ymin, double xmax, double ymax) {
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(xmin, ymin),
                new Coordinate(xmax, ymin),
                new Coordinate(xmax, ymax),
                new Coordinate(xmin, ymax),
                new Coordinate(xmin, ymin)
        };
        GeometryFactory gf = GeometryFactories.default_();
        return gf.createPolygon(gf.createLinearRing(coordinates), null);
    }

    /**
     * Creates a rectangular Polygon formed from the minima and maxima by the
     * given shell.
     * The user can set a srid.
     *
     * @param xmin X min
     * @param ymin Y min
     * @param xmax X max
     * @param ymax Y max
     * @param srid SRID
     * @return Envelope as a POLYGON
     */
    public static Polygon makeEnvelope(double xmin, double ymin, double xmax, double ymax, int srid) {
        Polygon geom = makeEnvelope(xmin, ymin, xmax, ymax);
        geom.setSRID(srid);
        return geom;
    }
}
