package imagereader;
// From Aarne Hovi's program

public class HyytialaCoordinateConversion {

    /* Same equations as in KUVAMITT */
    public Point3D kkjToUtm(Point3D kkjPoint) {

        double utmPoint_x = -2471441.562 + 0.9987798071 * kkjPoint.getX() + 0.04612734592 * kkjPoint.getY();
        double utmPoint_y = 124518.3273 - 0.04613846192 * kkjPoint.getX() + 0.9987750048 * kkjPoint.getY();
        double utmPoint_z = kkjPoint.getZ() + 18.67;
        Point3D utmPoint = new Point3D(utmPoint_x, utmPoint_y, utmPoint_z);
        return utmPoint;

    }


    /* Same equations as in KUVAMITT */
    public Point3D utmToKkj(Point3D utmPoint) {

        double kkjPoint_x = -0.04614190132 * utmPoint.getY() + 0.9990901664 * utmPoint.getX() + 2474938.474;
        double kkjPoint_y = 0.9990949702 * utmPoint.getY() + 0.04615302083 * utmPoint.getX() - 10341.1406;
        double kkjPoint_z = utmPoint.getZ() - 18.67;
        Point3D kkjPoint = new Point3D(kkjPoint_x, kkjPoint_y, kkjPoint_z);
        return kkjPoint;

    }
}
