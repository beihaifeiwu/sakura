package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import lombok.experimental.UtilityClass;

/**
 * Generate a Google Map link URL based on the center of the bounding box of the input geometry.
 * Optional arguments :
 * (1) specify the layer type  m (normal  map) , k (satellite), h (hybrid), p (terrain).
 * (2) set a zoom level between 1 and 19.
 * Default values are m and 19.
 *
 * @author Erwan Bocher
 */
@UtilityClass
public class ST_GoogleMapLink {

    /**
     * Generate a Google Map link URL based on the center of the bounding box of the input geometry
     *
     * @param geom
     * @return
     */
    public static String generateGMLink(Geometry geom) {
        return generateGMLink(geom, "m", 19);
    }

    /**
     * Generate a Google Map link URL based on the center of the bounding box of the input geometry
     * and set the layer type
     *
     * @param geom
     * @param layerType
     * @return
     */
    public static String generateGMLink(Geometry geom, String layerType) {
        return generateGMLink(geom, layerType, 19);
    }

    /**
     * Generate a Google Map link URL based on the center of the bounding box of the input geometry.
     * Set the layer type and the zoom level.
     *
     * @param geom
     * @param layerType
     * @param zoom
     * @return
     */
    public static String generateGMLink(Geometry geom, String layerType, int zoom) {
        if (geom == null) {
            return null;
        }
        try {
            LayerType layer = LayerType.valueOf(layerType.toLowerCase());
            Coordinate centre = geom.getEnvelopeInternal().centre();
            StringBuilder sb = new StringBuilder("https://maps.google.com/maps?ll=");
            sb.append(centre.y);
            sb.append(",");
            sb.append(centre.x);
            sb.append("&z=");
            sb.append(zoom);
            sb.append("&t=");
            sb.append(layer.name());
            return sb.toString();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Layer type supported are m (normal  map) , k (satellite), h (hybrid), p (terrain)", ex);
        }
    }

    /**
     * List of supported layers for Google Map
     */
    public enum LayerType {
        m,
        //– normal  map
        k,
        //– satellite
        h,
        //– hybrid
        p; //– terrain
    }

}
