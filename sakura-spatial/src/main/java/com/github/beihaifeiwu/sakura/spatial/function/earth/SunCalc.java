package com.github.beihaifeiwu.sakura.spatial.function.earth;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Date;

/**
 * This class is a partial Java port of SunCalc. A BSD-licensed JavaScript library for
 * calculating sun position, sunlight phases (times for sunrise, sunset, dusk,
 * etc.), moon position and lunar phase for the given location and time, created
 * by Vladimir Agafonkin (@mourner) as a part of the SunCalc.net project.
 */
public final class SunCalc {

    /* Constants */
    private static final double RAD = Math.PI / 180;
    private static final double DAY_MS = 1000 * 60 * 60 * 24;
    private static final double J1970 = 2440588;
    private static final double J2000 = 2451545;
    private static final double M0 = RAD * 357.5291;
    private static final double M1 = RAD * 0.98560028;
    private static final double J0 = 0.0009;
    private static final double J1 = 0.0053;
    private static final double J2 = -0.0069;
    private static final double C1 = RAD * 1.9148;
    private static final double C2 = RAD * 0.0200;
    private static final double C3 = RAD * 0.0003;
    private static final double P = RAD * 102.9372;// perihelion of the Earth
    // obliquity of the Earth
    //The mean obliquity of the ecliptic is calculated by a formula of Laskar (1986),
    //given in Jean Meeus: "Astronomical Algorithms", p. 135.
    private static final double E = RAD * 23.4397;

    private static final double TH0 = RAD * 280.16;
    private static final double TH1 = RAD * 360.9856235;


    private SunCalc() {
    }

    private static double dateToJulianDate(Date date) {
        return date.getTime() / DAY_MS - 0.5 + J1970;
    }

    private static Date julianDateToDate(double j) {
        return new Date(Math.round((j + 0.5 - J1970) * DAY_MS));
    }

    // general sun calculations
    private static long getJulianCycle(double J, double lw) {
        return Math.round(J - J2000 - J0 - lw / (2 * Math.PI));
    }

    private static double getSolarMeanAnomaly(double Js) {
        return M0 + M1 * (Js - J2000);
    }

    private static double getEquationOfCenter(double M) {
        return C1 * Math.sin(M) + C2 * Math.sin(2 * M) + C3 * Math.sin(3 * M);
    }

    private static double getEclipticLongitude(double M, double C) {
        return M + P + C + Math.PI;
    }

    private static double getSunDeclination(double Ls) {
        return Math.asin(Math.sin(Ls) * Math.sin(E));
    }

    // calculations for sun times
    private static double getApproxTransit(double Ht, double lw, double n) {
        return J2000 + J0 + (Ht + lw) / (2 * Math.PI) + n;
    }

    private static double getSolarTransit(double Js, double M, double Ls) {
        return Js + (J1 * Math.sin(M)) + (J2 * Math.sin(2 * Ls));
    }

    private static double getHourAngle(double h, double phi, double d) {
        return Math.acos((Math.sin(h) - Math.sin(phi) * Math.sin(d))
                / (Math.cos(phi) * Math.cos(d)));
    }

    // calculations for sun position
    private static double getRightAscension(double Ls) {
        return Math.atan2(Math.sin(Ls) * Math.cos(E), Math.cos(Ls));
    }

    private static double getSiderealTime(double J, double lw) {
        return TH0 + TH1 * (J - J2000) - lw;
    }

    /**
     * Sun azimuth in radians (direction along the horizon, measured from north to east)
     * e.g. 0 is north
     *
     * @param H
     * @param phi
     * @param d
     * @return
     */
    private static double getAzimuth(double H, double phi, double d) {
        return Math.atan2(Math.sin(H),
                Math.cos(H) * Math.sin(phi) - Math.tan(d) * Math.cos(phi)) + Math.PI;
    }

    /**
     * Sun altitude above the horizon in radians.
     * e.g. 0 at the horizon and PI/2 at the zenith
     *
     * @param H
     * @param phi
     * @param d
     * @return
     */
    private static double getAltitude(double H, double phi, double d) {
        return Math.asin(Math.sin(phi) * Math.sin(d) + Math.cos(phi)
                * Math.cos(d) * Math.cos(H));
    }

    /**
     * Returns the sun position as a coordinate with the following properties
     * <p>
     * x: sun azimuth in radians (direction along the horizon, measured from south to
     * west), e.g. 0 is south and Math.PI * 3/4 is northwest.
     * y: sun altitude above the horizon in radians, e.g. 0 at the
     * horizon and PI/2 at the zenith (straight over your head).
     *
     * @param date
     * @param lat
     * @param lng
     * @return
     */
    public static Coordinate getPosition(Date date, double lat,
                                         double lng) {
        if (isGeographic(lat, lng)) {
            double lw = RAD * -lng;
            double phi = RAD * lat;
            double J = dateToJulianDate(date);
            double M = getSolarMeanAnomaly(J);
            double C = getEquationOfCenter(M);
            double Ls = getEclipticLongitude(M, C);
            double d = getSunDeclination(Ls);
            double a = getRightAscension(Ls);
            double th = getSiderealTime(J, lw);
            double H = th - a;
            return new Coordinate(getAzimuth(H, phi, d), getAltitude(H, phi, d));
        } else {
            throw new IllegalArgumentException("The coordinate of the point must in latitude and longitude.");
        }
    }


    /**
     * Test if the point has valid latitude and longitude coordinates.
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public static boolean isGeographic(double latitude,
                                       double longitude) {
        return latitude > -90 && latitude < 90
                && longitude > -180 && longitude < 180;
    }
}
