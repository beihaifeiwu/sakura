package com.github.beihaifeiwu.sakura.spatial.function.convert;

import com.github.beihaifeiwu.sakura.spatial.utils.GeometryFactories;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.gml2.GMLReader;
import lombok.experimental.UtilityClass;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Read a GML representation and convert it to a geometry.
 * This function supports only GML 2.1.2
 */
@UtilityClass
public class ST_GeomFromGML {

    /**
     * Read the GML representation
     *
     * @param gmlFile
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static Geometry toGeometry(String gmlFile) throws SAXException, IOException, ParserConfigurationException {
        return toGeometry(gmlFile, 0);
    }

    /**
     * Read the GML representation with a specified SRID
     *
     * @param gmlFile
     * @param srid
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static Geometry toGeometry(String gmlFile, int srid) throws SAXException, IOException, ParserConfigurationException {
        if (gmlFile == null) {
            return null;
        }
        GeometryFactory gf = GeometryFactories.factory(srid);
        GMLReader gMLReader = new GMLReader();
        return gMLReader.read(gmlFile, gf);
    }

}
