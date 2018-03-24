package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This class is used to generate an OSM map link URL from a geometry.
 */
public class ST_OSMMapLink {

    /**
     * Create the OSM map link based on the bounding box of the geometry.
     *
     * @param geom the input geometry.
     * @return
     */
    public static String generateLink(Geometry geom) {
        return generateLink(geom, false);
    }

    /**
     * Create the OSM map link based on the bounding box of the geometry.
     *
     * @param geom       the input geometry.
     * @param withMarker true to place a marker on the center of the BBox.
     * @return
     */
    public static String generateLink(Geometry geom, boolean withMarker) {
        if (geom == null) {
            return null;
        }
        Envelope env = geom.getEnvelopeInternal();
        StringBuilder sb = new StringBuilder("http://www.openstreetmap.org/?");
        sb.append("minlon=").append(env.getMinX());
        sb.append("&minlat=").append(env.getMinY());
        sb.append("&maxlon=").append(env.getMaxX());
        sb.append("&maxlat=").append(env.getMaxY());
        if (withMarker) {
            Coordinate centre = env.centre();
            sb.append("&mlat=").append(centre.y);
            sb.append("&mlon=").append(centre.x);
        }
        return sb.toString();
    }
}
