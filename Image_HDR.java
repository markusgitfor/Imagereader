package imagereader;
/*
2012 Hyytiälä imageheaders information and collinear equations to locate
points in image with kkj coordinates or other way around
*/

import java.io.File;
import java.util.Scanner;
import org.opencv.core.Mat;

public class Image_HDR {
    
    private String source;
    private String imagetype;
    private int imagecode;
    private int sub_width;
    private int sub_height;
    private String filename;
    private int color;
    private int c_col; private int c_row;
    private int o_col; private int o_row;
    private int width;
    private int height;
    private double constant;
    private double x_ps; private double y_ps;
    private double lambda;
    private double alpha;
    private double mean_x; private double mean_y;
    private double x_mean;
    private double y_mean;
    private double a;
    private double b;
    private double c;
    private double d;
    private double e;
    private double f;
    private double omega;
    private double phi;
    private double kappa;
    private double xo;
    private double yo;
    private double zo;
    private double sun_azimuth;
    private double sun_elevation;
    private String start_of;
    private int num_of_addit_expos;
    private int addittype;
    private int additfilename;
    private int additwidth;
    private int additheight;

   
 //HDR-file object
    public Image_HDR(String filename) {
        this.source = filename;

    }

    public void readHDR(String headername) {

        try (Scanner tiedostonLukija = new Scanner(new File(headername))) {

            this.imagetype = tiedostonLukija.nextLine();
            this.imagecode = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.sub_width = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.sub_height = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.filename = tiedostonLukija.nextLine();
            this.color = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.c_col = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.c_row = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.o_col = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.o_row = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.width = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.height = Integer.parseInt(tiedostonLukija.nextLine().trim());
            this.constant = Double.parseDouble(tiedostonLukija.nextLine().trim());
            this.x_ps = Double.parseDouble(tiedostonLukija.nextLine().trim());
            this.y_ps = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.lambda = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.alpha = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.mean_x = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.mean_y = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.x_mean = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.y_mean = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.a = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.b = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.c = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.d = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.e = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.f = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.omega = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.phi = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.kappa = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.xo = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.yo = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.zo = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.sun_azimuth = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.sun_elevation = Double.valueOf(tiedostonLukija.nextLine().trim());
            this.start_of = tiedostonLukija.nextLine();
            this.num_of_addit_expos = Integer.parseInt(tiedostonLukija.nextLine().trim());

            tiedostonLukija.close();
        } catch (Exception e) {
            System.out.println("Virhe: " + e.getMessage());
        }
    }
    
   
    
//Affine tranformation from imagecoordinates to cameracoordinates
    public double[] a_transform_affine1(int direction, int i, double x, double y) {
        
       double p_x = (-this.e * this.c + this.e * x + this.f * this.b - y * this.b) /
                (this.a * this.e - this.b * this.d);
        double p_y = -(this.a * this.f - this.a * y - this.c * this.d + x * this.d) /
                (this.a * this.e - this.b * this.d);
        return new double[]{p_x, p_y};

    }
    // returns images sun_azimuth in degrees
    public double getAzimuth() {
        return this.sun_azimuth * 180 / Math.PI; 
    }
    // returns images sun_elevation in degrees
    public double getSunElevation() {
        return this.sun_elevation * 180 / Math.PI; 
    }

    public double getCartoAzimuth(double x, double y) {
        return 180 / Math.PI * Math.atan2(y - this.yo, x - this.xo);
    }
    //Calculates zenith angle to a point in image, needs pixel size in meters as parameter
    public double getZenithAngle(int x, int y, double px_size){
        double xc = this.width/2;
        double yc = this.height/2;
        double res1 = ((Math.abs(xc-x))*(Math.abs(xc-x)) +
                ((Math.abs(yc-y))*Math.abs(yc-y)));
        double res2 = Math.sqrt(res1)*0.14;
        double res3 = res2/1500;
        double result = Math.toDegrees(res3);
        
        
        return result;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }
    public String getFile(){
        return this.filename;
    }

    public double[] getCenter() {
        double x = this.xo;
        double y = this.yo;
        return new double[]{x, y};
    }
    //Affine tranformation from cameracoordinates to imagecoordinates
    public double[] a_transform_affine2(double x, double y) {

        //From camera coordinates to image coordinates -> pixels
        double p_x = this.a * x + this.b * y + this.c;
        double p_y = this.d * x + this.e * y + this.f;
        return new double[]{p_x, p_y};

    }
    //Rotation matrix from roll, yaw and pitch angles from .HDR file
    public double[][] r_transform_matrix() {
        double[][] matrix = new double[][]{{Math.cos(this.phi) * Math.cos(this.kappa),
            Math.cos(this.omega) * Math.sin(this.kappa) + Math.sin(this.omega) * Math.sin(this.phi) * Math.cos(this.kappa),
            Math.sin(this.omega) * Math.sin(this.kappa) - Math.cos(this.omega) * Math.sin(this.phi) * Math.cos(this.kappa)}, {-Math.cos(this.phi) * Math.sin(this.kappa),
            Math.cos(this.omega) * Math.cos(this.kappa) - Math.sin(this.omega) * Math.sin(this.phi) * Math.sin(this.kappa),
            Math.sin(this.omega) * Math.cos(this.kappa) + Math.cos(this.omega) * Math.sin(this.phi) * Math.sin(this.kappa)}, {Math.sin(this.phi),
            -Math.sin(this.omega) * Math.cos(this.phi),
            Math.cos(this.omega) * Math.cos(this.phi)}};

        return matrix;
    }

    public String getNimi() {
        return this.filename;
    }

    //Collinear equations with parts of rotation matrix to make orientation from kkj to 
    //imagecoordinates
    public double[] r_transform_3D(double x, double y, double z) {
        double p_x = 0;
        double p_y = 0;
        double k = ((Math.sin(this.phi)) * (x - this.xo) + (-Math.sin(this.omega) * Math.cos(this.phi))
                * (y - this.yo) + (Math.cos(this.omega) * Math.cos(this.phi)) * (z - this.zo));

        if (k != 0.0) {
            double camera_x = -this.constant * ((Math.cos(this.phi) * Math.cos(this.kappa))
                    * (x - this.xo) + (Math.cos(this.omega) * Math.sin(this.kappa) + Math.sin(this.omega) * Math.sin(this.phi) * Math.cos(this.kappa))
                    * (y - this.yo) + (Math.sin(this.omega) * Math.sin(this.kappa) - Math.cos(this.omega) * Math.sin(this.phi) * Math.cos(this.kappa)) * (z - this.zo)) / k;
            double camera_y = -this.constant * ((-Math.cos(this.phi) * Math.sin(this.kappa))
                    * (x - this.xo) + (Math.cos(this.omega) * Math.cos(this.kappa) - Math.sin(this.omega) * Math.sin(this.phi) * Math.sin(this.kappa))
                    * (y - this.yo) + (Math.sin(this.omega) * Math.cos(this.kappa) + Math.cos(this.omega) * Math.sin(this.phi) * Math.sin(this.kappa)) * (z - this.zo)) / k;
            return new double[]{camera_x, camera_y};
        } else {
            return null;

        }
    }

    //Algorithm that uses all the necessary subprograms to get the pixel coordinate, with XYZ from kkj
    public double[] r_transform_ground_to_pixel(double x, double y, double z) {

        double[] camera_xy = r_transform_3D(x, y, z);
        double camera_x = camera_xy[0];
        double camera_y = camera_xy[1];
        double[] pixel_xy = a_transform_affine2(camera_x, camera_y);//affine2 is from camera to pixel coordinates
        return new double[]{pixel_xy[0], this.height - pixel_xy[1]};

    }
 
}
