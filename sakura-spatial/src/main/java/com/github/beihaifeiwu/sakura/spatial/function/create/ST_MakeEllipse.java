package com.github.beihaifeiwu.sakura.spatial.function.create;

import com.github.beihaifeiwu.sakura.spatial.SpatialException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;


/**
 * ST_MakeEllipse constructs an elliptical POLYGON with the given width and
 * height centered at the given point. Each ellipse contains 100 line segments.
 */
public class ST_MakeEllipse {

    private static final GeometricShapeFactory GSF = new GeometricShapeFactory();

    /**
     * Make an ellipse centered at the given point with the given width and
     * height.
     *
     * @param p      Point
     * @param width  Width
     * @param height Height
     * @return An ellipse centered at the given point with the given width and height
     * @throws SpatialException if the width or height is non-positive
     */
    public static Polygon makeEllipse(Point p, double width, double height) {
        if (p == null) {
            return null;
        }
        if (height < 0 || width < 0) {
            throw new SpatialException("Both width and height must be positive.");
        } else {
            GSF.setCentre(new Coordinate(p.getX(), p.getY()));
            GSF.setWidth(width);
            GSF.setHeight(height);
            return GSF.createEllipse();
        }
    }
}
