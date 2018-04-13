package sakura.spatial.utils;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liupin on 2017/5/18.
 */
public class GeometryFactories {

    private static final Map<Integer, GeometryFactory> GF_POOL = new HashMap<>();

    public static GeometryFactory factory(int srid) {
        return GF_POOL.computeIfAbsent(srid, key -> new GeometryFactory(new PrecisionModel(), key));
    }

    public static GeometryFactory default_() {
        return factory(0);
    }

    public static GeometryFactory WGS84() {
        return factory(CRS.WGS84);
    }

    public static GeometryFactory pseudoMercator() {
        return factory(CRS.WGS84_PSEUDO_MERCATOR);
    }
}
