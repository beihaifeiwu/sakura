package sakura.spatial.utils;

import lombok.experimental.UtilityClass;

/**
 * Geometry type codes as defined in OGC SFS 1.2.1
 *
 * @author Nicolas Fortin
 */
@UtilityClass
public class GeometryTypeCodes {
    public static final int GEOMETRY = 0;
    public static final int POINT = 1;
    public static final int LINESTRING = 2;
    public static final int POLYGON = 3;
    public static final int MULTIPOINT = 4;
    public static final int MULTILINESTRING = 5;
    public static final int MULTIPOLYGON = 6;
    public static final int GEOMCOLLECTION = 7;
    public static final int CURVE = 13;
    public static final int SURFACE = 14;
    public static final int POLYHEDRALSURFACE = 15;
    public static final int GEOMETRYZ = 1000;
    public static final int POINTZ = POINT + 1000;
    public static final int LINESTRINGZ = LINESTRING + 1000;
    public static final int POLYGONZ = POLYGON + 1000;
    public static final int MULTIPOINTZ = MULTIPOINT + 1000;
    public static final int MULTILINESTRINGZ = MULTILINESTRING + 1000;
    public static final int MULTIPOLYGONZ = MULTIPOLYGON + 1000;
    public static final int GEOMCOLLECTIONZ = GEOMCOLLECTION + 1000;
    public static final int CURVEZ = CURVE + 1000;
    public static final int SURFACEZ = SURFACE + 1000;
    public static final int POLYHEDRALSURFACEZ = POLYHEDRALSURFACE + 1000;
    public static final int GEOMETRYM = 2000;
    public static final int POINTM = POINT + 2000;
    public static final int LINESTRINGM = LINESTRING + 2000;
    public static final int POLYGONM = POLYGON + 2000;
    public static final int MULTIPOINTM = MULTIPOINT + 2000;
    public static final int MULTILINESTRINGM = MULTILINESTRING + 2000;
    public static final int MULTIPOLYGONM = MULTIPOLYGON + 2000;
    public static final int GEOMCOLLECTIONM = GEOMCOLLECTION + 2000;
    public static final int CURVEM = CURVE + 2000;
    public static final int SURFACEM = SURFACE + 2000;
    public static final int POLYHEDRALSURFACEM = POLYHEDRALSURFACE + 2000;
    public static final int GEOMETRYZM = 3000;
    public static final int POINTZM = POINT + 3000;
    public static final int LINESTRINGZM = LINESTRING + 3000;
}