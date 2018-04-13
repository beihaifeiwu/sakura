/**
 * H2GIS is a library that brings spatial support to the H2 Database Engine
 * <http://www.h2database.com>. H2GIS is developed by CNRS
 * <http://www.cnrs.fr/>.
 * <p>
 * This code is part of the H2GIS project. H2GIS is free software;
 * you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * version 3.0 of the License.
 * <p>
 * H2GIS is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details <http://www.gnu.org/licenses/>.
 * <p>
 * <p>
 * For more information, please consult: <http://www.h2gis.org/>
 * or contact directly: info_at_h2gis.org
 */

package sakura.spatial.function.convert;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Forces a Geometry into 2D mode by returning a copy with
 * its z-coordinate set to {@link Double.NaN}.
 */
public class ST_Force2D {

    /**
     * Converts a XYZ geometry to XY.
     *
     * @param geom
     * @return
     */
    public static Geometry force2D(Geometry geom) {
        if (geom == null) {
            return null;
        }
        Geometry outPut = (Geometry) geom.clone();
        outPut.apply(new CoordinateSequenceFilter() {
            private boolean done = false;

            @Override
            public boolean isGeometryChanged() {
                return true;
            }

            @Override
            public boolean isDone() {
                return done;
            }

            @Override
            public void filter(CoordinateSequence seq, int i) {
                seq.setOrdinate(i, 2, Double.NaN);
                if (i == seq.size()) {
                    done = true;
                }
            }
        });
        return outPut;
    }
}
