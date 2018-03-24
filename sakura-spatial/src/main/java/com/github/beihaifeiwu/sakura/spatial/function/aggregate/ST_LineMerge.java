package com.github.beihaifeiwu.sakura.spatial.function.aggregate;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import lombok.experimental.UtilityClass;

import java.util.Collection;

/**
 * Merges a collection of linear components to form maximal-length linestrings.
 */
@UtilityClass
public class ST_LineMerge {

    @SuppressWarnings("unchecked")
    public static Geometry merge(Geometry geometry) {
        if (geometry == null) {
            return null;
        }
        if (geometry.getDimension() != 1) {
            return geometry.getFactory().createMultiLineString(new LineString[0]);
        }
        LineMerger lineMerger = new LineMerger();
        lineMerger.add(geometry);
        Collection coll = lineMerger.getMergedLineStrings();
        return geometry.getFactory().createMultiLineString((LineString[]) coll.toArray(new LineString[coll.size()]));
    }

}
