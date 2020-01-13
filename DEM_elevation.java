package imagereader;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DEM_elevation {
    private double [] [] dem;

    public DEM_elevation() {

    }

    public void readRasterIsenburg(String hdrFile) throws FileNotFoundException, IOException {

        double llCornerX, llCornerY, noDataVal, pixelSize;
        int width, height;
        String dataFile;

        Scanner hdrIn = new Scanner(new File(hdrFile));
        width = Integer.parseInt(hdrIn.next().trim());
        height = Integer.parseInt(hdrIn.next().trim());
        llCornerX = Double.parseDouble(hdrIn.next().trim());
        llCornerY = Double.parseDouble(hdrIn.next().trim());
        pixelSize = Double.parseDouble(hdrIn.next().trim());
        noDataVal = Double.parseDouble(hdrIn.next().trim());
        
        //dataFile = hdrIn.next().trim();
        dataFile = "hyytiala_DEM_2010.bin";
        hdrIn.close();

        File file = new File(dataFile);
        FileInputStream fis = new FileInputStream(file);
        byte[] first = new byte[81795090];

        fis.read(first);
        ByteBuffer wrapped = ByteBuffer.wrap(first);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);

        double[][] data = new double[width][height];
        for (int j = height - 1; j > 0; j--) {
            for (int i = 0; i < width; i++) {
                data[i][j] = (double) 0.01 * wrapped.getShort();
                if (data[i][j] < 0) {
                    data[i][j] = -99;
                }
            }
        }
        this.dem = data;

    }

    public double getElevation(double x_kkj, double y_kkj) throws IOException{

        double[] xy = coordinate_transform(x_kkj, y_kkj);

        int dem_x = (int) Math.floor((xy[0] - 355133.5) / 1);
        int dem_y = (int) Math.floor(((xy[1] - 6855143.5) / 1));

        return this.dem[dem_x][dem_y] - 18.67 + 0.32;
    }

    public static double[] coordinate_transform(double x_kkj, double y_kkj) {

        double utm_x = -2471441.562 + 0.9987798071 * x_kkj + 0.04612734592 * y_kkj;
        double utm_y = 124518.3273 - 0.04613846192 * x_kkj + 0.9987750048 * y_kkj;

        return new double[]{utm_x, utm_y};
    }

}
