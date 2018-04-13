package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.gml2.GMLWriter;
import lombok.experimental.UtilityClass;

/**
 * Store a geometry as a GML representation, It supports OGC GML standard 2.1.2
 */
@UtilityClass
public class ST_AsGML {

    /**
     * Write the GML
     *
     * @param geom
     * @return
     */
    public static String toGML(Geometry geom) {
        if (geom == null) {
            return null;
        }
        int srid = geom.getSRID();
        GMLWriter gmlw = new GMLWriter();
        if (srid != -1 || srid != 0) {
            gmlw.setSrsName("EPSG:" + geom.getSRID());
        }
        return gmlw.write(geom);
    }

}
