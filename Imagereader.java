package imagereader;

import java.io.File;
import java.nio.file.*;
import java.util.stream.*;
import java.io.FileInputStream;
import java.nio.*;
import java.nio.ByteOrder;
import java.util.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Imagereader {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws IOException, Exception {
        
        fileNames();
        
        create_ortho2("/media/markus/Ubuntu_Markus/Kuvat/Modified_headers_for_CIR_LVL3/2012_001_10375.hdr","", "052_098.bin", "Hyytiala_DEM_2010.hdr");
        writeTxt("lidardata.txt", "");

        System.exit(0);

    }

    public static void writeTxt(String filename, String lidarname) throws Exception {

        LidarReader n = new LidarReader("052_098.bin");
        n.readLidarBinFile();
        ArrayList<LidarInfo> lista = n.getLidar();

        PrintWriter file = new PrintWriter(filename);

        HyytialaCoordinateConversion m = new HyytialaCoordinateConversion();

        file.println("X,Y,Z");
        for (LidarInfo point : lista) {
            Point3D p = point.returns[0];
            Point3D pointUTM = m.kkjToUtm(p);
            file.println(pointUTM.getX() + "," + pointUTM.getY() + "," + pointUTM.getZ());

        }
        file.close();
    }

   
      

    //Checks all the images if it contains asked kkj coordinates. Path is to HD.
    public static List<String> fileNames() {
        String end = ".hdr";
        List<String> result = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get("/media/markus/Ubuntu_Markus/Kuvat/Modified_headers_for_CIR_LVL3/"))) {

            result = walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(end)).collect(Collectors.toList());

            result.forEach(System.out::println);
            System.out.println(result.size());
            return result;
        } catch (IOException e) {
            System.out.println("Error");
        }
        return result;
    }
    
    
    // Loops all trees and extracts data
    public void loop() throws Exception{
        
         List<String> result = fileNames();
         
         List<TreeAttributes> lista = new ArrayList<>(); // tähän funktio joka lukee tiedoston .csv
         
         PrintWriter file = new PrintWriter("output.txt");
         
         
         for(int i = 0; i < lista.size(); i++){
             
             for(String name : result){
              Image_HDR header = new Image_HDR(name);
              header.readHDR(name);
              Mat img = Imgcodecs.imread(header.getFile());
              //double[] xy = header.r_transform_ground_to_pixel(x, y, z);
          //    double row = xy[1];
          //    double col = xy[0];
              file.println("tiedot puista");
             
         }
             
         }
         file.close();
         
         
     

       
    }

    // Transforms kkj coordinates to utm, because of the DEM-coordinates
    public static double[] coordinate_transform(double x_kkj, double y_kkj) {

        double utm_x = -2471441.562 + 0.9987798071 * x_kkj + 0.04612734592 * y_kkj;
        double utm_y = 124518.3273 - 0.04613846192 * x_kkj + 0.9987750048 * y_kkj;

        return new double[]{utm_x, utm_y};
    }

    // Returns DEM-coordinates from kkj-coordinates
    public static int[] dem_coordinates(double x_kkj, double y_kkj) {
        double[] xy = coordinate_transform(x_kkj, y_kkj);

        int dem_x = (int) Math.floor((xy[0] - 355133.5) / 1);
        int dem_y = (int) Math.floor(((xy[1] - 6855143.5) / 1));

        return new int[]{dem_x, dem_y};
    }

    public static void create_ortho2(String headerName, String imageName, String lidarName, String demName) throws IOException {

        double time1 = System.currentTimeMillis();

        Image_HDR header = new Image_HDR(headerName);
        header.readHDR(headerName);

        Mat img = Imgcodecs.imread(header.getFile());

        LidarReader lidar = new LidarReader(lidarName);
        lidar.readLidarBinFile();
        lidar.getFirstEchoes();
        double aa = lidar.getNClosest(lidar.getFirstEchoesMinX(), lidar.getFirstEchoesMinY(), 5, 3);
        System.out.println(aa);

        DEM_elevation dem = new DEM_elevation();
        dem.readRaster(demName);

        double start_x = lidar.getFirstEchoesMinX();
        double start_y = lidar.getFirstEchoesMaxY();

        double x_length = lidar.getFirstEchoesMaxX() - start_x;
        double y_length = start_y - lidar.getFirstEchoesMinY();

        System.out.println(x_length);
        System.out.println(y_length);

        double grid_size = 0.5;

        int pixel_columns = (int) Math.round(x_length / grid_size);
        System.out.println(pixel_columns);
        int pixel_rows = (int) Math.round(y_length / grid_size);
        System.out.println(pixel_rows);

        Mat ortho = new Mat(pixel_columns, pixel_rows, img.type());
        int rows = ortho.rows();
        int cols = ortho.cols();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = start_x + ((j + 1) - 1) * grid_size;
                double y = start_y - ((i + 1) - 1) * grid_size;
                double z = dem.getElevation(x, y);
                //double z = lidar.getNClosest(x, y, 5, 3);
                //System.out.println(i);
                double[] xy = header.r_transform_ground_to_pixel(x, y, z);
                double row = xy[1];
                double col = xy[0];
                if (xy[0] > 1 && xy[1] > 1 && col < img.rows() + 1 && row < img.cols() + 1) {
                    double row_ceil = Math.ceil(row);
                    double row_floor = Math.ceil(row);
                    double col_ceil = Math.ceil(col);
                    double col_floor = Math.ceil(col);
                    double[] color = bilinearInterpolation(col, row, img.get((int) row_floor, (int) col_floor), img.get((int) row_floor, (int) col_ceil), img.get((int) row_ceil, (int) col_floor), img.get((int) row_ceil, (int) col_ceil));
                    ortho.put(i, j, color);
                } else {
                    double[] noCol = new double[]{0.0, 0.0, 0.0};
                    ortho.put(i, j, noCol);
                }

            }
        }

        double time2 = System.currentTimeMillis();
        System.out.println("Orthoimage is ready now");
        System.out.println("Process took: " + ((time2 - time1) / 1000) + " seconds");

        PrintWriter file = new PrintWriter("ortho2.tfw");
        HyytialaCoordinateConversion m = new HyytialaCoordinateConversion();
        Point3D point = new Point3D(start_x, start_y, 0.0);
        Point3D pointUTM = m.kkjToUtm(point);
        file.println(grid_size);
        file.println(0);
        file.println(0);
        file.println(-grid_size);
        file.println(pointUTM.getX());
        file.println(pointUTM.getY());
        file.close();

        Imgcodecs.imwrite("ortho2.tif", ortho);

    }

    public static double[] bilinearInterpolation(double x_image, double y_image, double[] col1, double[] col2, double[] col3, double[] col4) {

        double k = Math.floor(x_image);
        double l = Math.floor(y_image);
        double d_x = x_image - k;
        double d_y = y_image - l;

        int i = 0;
        double color1 = ((1 - d_x) * (1 - d_y) * col1[i]) + (d_x * (1 - d_y) * col2[i]) + ((1 - d_x) * d_y * col3[i]) + (d_x * d_y * col4[i]);
        i = 1;
        double color2 = ((1 - d_x) * (1 - d_y) * col1[i]) + (d_x * (1 - d_y) * col2[i]) + ((1 - d_x) * d_y * col3[i]) + (d_x * d_y * col4[i]);
        i = 2;
        double color3 = ((1 - d_x) * (1 - d_y) * col1[i]) + (d_x * (1 - d_y) * col2[i]) + ((1 - d_x) * d_y * col3[i]) + (d_x * d_y * col4[i]);

        double[] interpolatedColor = new double[3];
        interpolatedColor[0] = color1;
        interpolatedColor[1] = color2;
        interpolatedColor[2] = color3;

        return interpolatedColor;

    }

    // Returns array with RGB-values from image in 3D-space
    public static double[] getRGB(double x, double y, Image_HDR header, String filename, Mat img) throws IOException {

        DEM_elevation xx = new DEM_elevation();
        double z = xx.getElevation(x, y) - 18.67 + 0.32; //N60-ellipsoid height difference + N60-N2000 difference //(zgnd==N2000 elevation)
        double[] imagecoordinates = header.r_transform_ground_to_pixel(x, y, z);
        double column = imagecoordinates[0];
        int col = (int) column;
        double rows = imagecoordinates[1];
        int row = (int) rows;
        double[] rgb = img.get(row, col);

        return rgb;
    }

    public static double[] getRGB_2(double x, double y, Image_HDR header, String filename) throws IOException {

        Mat img = Imgcodecs.imread(filename);

        DEM_elevation xx = new DEM_elevation();
        double z = xx.getElevation(x, y) - 18.67 + 0.32; //N60-ellipsoid height difference + N60-N2000 difference //(zgnd==N2000 elevation)
        double[] imagecoordinates = header.r_transform_ground_to_pixel(x, y, z);
        double column = imagecoordinates[0];
        int col = (int) column;
        double rows = imagecoordinates[1];
        int row = (int) rows;
        double[] rgb = img.get(row, col);

        return rgb;
    }

    // Returns imagecoordinates in image in given kkj-coordinates with changeable z-value
    public static int[] getRGBIMG(double x, double y, Image_HDR header) {

        double[] imagecoordinates = header.r_transform_ground_to_pixel(x, y, 148.79);
        double column = imagecoordinates[0];
        int col = (int) column;
        double rows = imagecoordinates[1];
        int row = (int) rows;

        return new int[]{col, row};
    }

    // Text UI
    // Unfinished function to rotate image
    public static void rotateImage(Mat img) {
        // Rotating image with predefined rotations
        //Core.rotate(croppedImage, croppedImage, -1);
        //double radians = Math.toRadians(100);
        //double sin = Math.abs(Math.sin(radians));
        //double cos = Math.abs(Math.cos(radians));
        //int newWidth = (int) (img.width() * cos + img.height() * sin);
        //int newHeight = (int) (img.width() * sin + img.height() * cos);
        //Point center = new Point(col, row);
        //Mat rotMatrix = Imgproc.getRotationMatrix2D(center, 100, 1.0); //1.0 means 100 % scale
        //Size size = new Size(newWidth, newHeight);
        //Imgproc.warpAffine(img, img, rotMatrix, img.size());

    }

   

    // Return imagecoordinates in 3D-space with DEM, needs only xy-coordinate in kkj
    public static int[] getImageCoordinates(Image_HDR header, double x, double y) throws IOException {
        DEM_elevation xx = new DEM_elevation();
        double z = xx.getElevation(x, y) - 18.67 + 0.32; //N60-ellipsoid height difference + N60-N2000 difference //(zgnd==N2000 elevation)
        System.out.println(z);
        double[] imagecoordinates = header.r_transform_ground_to_pixel(x, y, z);
        double column = imagecoordinates[0];
        int col = (int) column;
        double rows = imagecoordinates[1];
        int row = (int) rows;

        return new int[]{col, row};

    }

    // Draws blue circle to given imagecoordinates point and image
    public static void drawCircle(Mat img, int col, int row) {

        Imgproc.circle(img, new Point(col, row), 3, new Scalar(255, 0, 0), 5);

    }

    // Crops image to smaller to be represented in the screen
    public static Mat cropImage(int col, int row, int ww, int wh, Mat img) {
        Rect rectCrop;
        rectCrop = new Rect(col - ww / 2, row - wh / 2, wh, ww);
        Mat croppedImage = new Mat(img, rectCrop);

        return croppedImage;
    }

    // Shows image on the screen needs, image in Mat and name for the window
    public static void printImage(Mat img, String name) {
        HighGui.imshow(name, img);

        HighGui.waitKey(0);
        HighGui.destroyAllWindows();

    }

    // Prints image to screen with RGB-data specified by parameters
    public static void point_to_frame_info(double x, double y, int ww, int wh,
            Image_HDR header, String filename, Mat img) throws IOException {

        int[] imagecoordinates = getImageCoordinates(header, x, y);
        int col = imagecoordinates[0];
        int row = imagecoordinates[1];

        double[] rgb = img.get(row, col);

        drawCircle(img, col, row);

        String sa1 = ("Red_band: " + rgb[0]);
        String sa2 = ("Green_band: " + rgb[1]);
        String sa3 = ("Blue_band: " + rgb[2]);
        String sa4 = ("Zenith: " + Math.round((header.getZenithAngle(col, row, 0.42))));
        String sa5 = ("Sun elevation: " + Math.round(header.getSunElevation()));
        Mat croppedImage = cropImage(col, row, ww, wh, img);
        
        //Package changed
        
        
        // Tekstin tulostaminen kuvan vasempaan yläreunaan
        // Imgproc.putText(croppedImage, sa1, new Point(30, 30),
        //          Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        //  Imgproc.putText(croppedImage, sa2, new Point(30, 60),
        //          Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        //  Imgproc.putText(croppedImage, sa3, new Point(30, 90),
        //         Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        //  Imgproc.putText(croppedImage, sa4, new Point(30, 120),
        //         Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        //  Imgproc.putText(croppedImage, sa5, new Point(30, 150),
        //         Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);

        String name = "Cropped image";

        printImage(croppedImage, name);

    }

    public static void point_to_frame_info2(double x, double y, int ww, int wh,
            Image_HDR header, String filename, Mat img) {

        //int[] imagecoordinates = getImageCoordinates(header, x, y, array);
        int col = 4513;
        int row = 1193;

        double[] rgb = img.get(row, col);

        drawCircle(img, col, row);

        String sa1 = ("Red_band: " + rgb[0]);
        String sa2 = ("Green_band: " + rgb[1]);
        String sa3 = ("Blue_band: " + rgb[2]);
        String sa4 = ("Zenith: " + Math.round((header.getZenithAngle(col, row, 0.42))));
        String sa5 = ("Sun elevation: " + Math.round(header.getSunElevation()));
        Mat croppedImage = cropImage(col, row, ww, wh, img);
        
        // Package changed
        
        //Imgproc.putText(croppedImage, sa1, new Point(30, 30),
        //    Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        // Imgproc.putText(croppedImage, sa2, new Point(30, 60),
        //    Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        // Imgproc.putText(croppedImage, sa3, new Point(30, 90),
        //   Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        // Imgproc.putText(croppedImage, sa4, new Point(30, 120),
        //   Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 0), 3);
        // Imgproc.putText(croppedImage, sa5, new Point(30, 150),
        //   Core.F, 1, new Scalar(0, 0, 0), 3);

        String name = "Cropped image";

        printImage(croppedImage, name);

    }
}
