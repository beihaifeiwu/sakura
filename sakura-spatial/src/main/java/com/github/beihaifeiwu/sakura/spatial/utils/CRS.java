package com.github.beihaifeiwu.sakura.spatial.utils;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import lombok.experimental.UtilityClass;
import org.cts.CRSFactory;
import org.cts.crs.CRSException;
import org.cts.crs.CoordinateReferenceSystem;
import org.cts.crs.GeodeticCRS;
import org.cts.cs.CoordinateSystem;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationFactory;
import org.cts.registry.EPSGRegistry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liupin on 2017/1/4.
 */
@UtilityClass
public class CRS {

    public static final int WGS84 = 4326;
    public static final int WGS84_PSEUDO_MERCATOR = 3857;

    private static final CRSFactory CRS_FACTORY = new CRSFactory();
    private static final EPSGRegistry EPSG_REGISTRY = new EPSGRegistry();
    private static final Map<EPSGTuple, CoordinateOperation> COP_POOL = new CopCache(5);

    static {
        CRS_FACTORY.getRegistryManager().addRegistry(EPSG_REGISTRY);
    }

    public static CoordinateReferenceSystem getCRS(int srid) {
        try {
            return CRS_FACTORY.getCRS(EPSG_REGISTRY.getRegistryName() + ":" + String.valueOf(srid));
        } catch (CRSException e) {
            throw new SpatialException("Cannot create the CRS", e);
        }
    }

    public static CoordinateOperation getCOP(int inputSRID, int codeEpsg) {
        EPSGTuple epsg = new EPSGTuple(inputSRID, codeEpsg);
        CoordinateOperation op = COP_POOL.get(epsg);
        if (op == null) {
            CoordinateReferenceSystem inputCRS = getCRS(inputSRID);
            CoordinateReferenceSystem targetCRS = getCRS(codeEpsg);
            if (inputCRS instanceof GeodeticCRS && targetCRS instanceof GeodeticCRS) {
                List<CoordinateOperation> ops = CoordinateOperationFactory
                        .createCoordinateOperations((GeodeticCRS) inputCRS, (GeodeticCRS) targetCRS);
                if (!ops.isEmpty()) {
                    op = ops.get(0);
                    COP_POOL.put(epsg, op);
                }
            } else {
                throw new SpatialException("The transformation from "
                        + inputCRS + " to " + codeEpsg + " is not yet supported.");
            }
        }
        return op;
    }

    /**
     * A simple cache to manage {@link CoordinateOperation}
     */
    public static class CopCache extends LinkedHashMap<EPSGTuple, CoordinateOperation> {

        private final int limit;

        public CopCache(int limit) {
            super(16, 0.75f, true);
            this.limit = limit;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<EPSGTuple, CoordinateOperation> eldest) {
            return size() > limit;
        }
    }

    /**
     * A simple tuple to manage both input and output CRSes used to build a
     * {@link CoordinateOperation}
     *
     * @author Erwan Bocher
     */
    public static class EPSGTuple {

        private int intputEPSG;
        private int targetEPSG;

        /**
         * Create the tuple with the input and output epsg codes available in the
         * spatial_ref_sys table
         *
         * @param intputEPSG the epsg code for the input {@link CoordinateSystem}
         * @param targetEPSG the epsg code for the output {@link CoordinateSystem}
         */
        public EPSGTuple(int intputEPSG, int targetEPSG) {
            this.intputEPSG = intputEPSG;
            this.targetEPSG = targetEPSG;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + this.intputEPSG;
            hash = 67 * hash + this.targetEPSG;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            if (obj instanceof EPSGTuple) {
                final EPSGTuple other = (EPSGTuple) obj;
                if (this.intputEPSG != other.intputEPSG) {
                    return false;
                }
                return this.targetEPSG == other.targetEPSG;
            }
            return false;
        }
    }
}
