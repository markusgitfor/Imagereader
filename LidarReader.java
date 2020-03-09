package imagereader;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

public class LidarReader {

    private String filepath;
    private ArrayList<LidarInfo> lidarLista;
    private ArrayList<Point3D> firstEchoes;

    public LidarReader(String filepath) {
        this.lidarLista = new ArrayList<>();
        this.filepath = filepath;
        this.firstEchoes = new ArrayList<>();

    }

    public void print() throws IOException {
        ArrayList<LidarInfo> lista = this.lidarLista;

        int i = 0;

        for (LidarInfo point : lista) {
            //System.out.println(point.returns[3].getX() + " " + point.returns[3].getY());
            double tulos = point.returns[3].getX();
            System.out.println(tulos);
            if (tulos > 10) {
                int p = 10 + 2;
            }
            i++;
        }
        System.out.println(i);
    }

    public double getPercentiles(double percentile) throws IOException {

        ArrayList<Double> number_list_1 = new ArrayList<>();

        ArrayList<LidarInfo> number_list_2 = this.lidarLista;

        for (LidarInfo point : number_list_2) {
            double res = point.returns[3].getZ() - point.returns[0].getZ();
            //System.out.println(res);
            if (res > 0) {
                number_list_1.add(point.returns[3].getZ() - point.returns[0].getZ());
                //System.out.println(res);
            }
        }
        Collections.sort(number_list_1);
        int rank = (int) Math.floor((percentile * 100 / 100 * (number_list_1.size())));

        return number_list_1.get(rank);
    }

    public void readLidarBinFile() throws IOException {

        try {

            File in = new File(this.filepath);
            FileInputStream fis;

            fis = new FileInputStream(in);
            int len = fis.available();
            byte[] first = new byte[len];

            fis.read(first);
            ByteBuffer wrapped = ByteBuffer.wrap(first);
            wrapped.order(ByteOrder.LITTLE_ENDIAN);

            int pulseCount = wrapped.getInt();
            //System.out.println(pulseCount);
            this.lidarLista.ensureCapacity(pulseCount);
            for (int i = 0; i < pulseCount; i++) {
                this.lidarLista.add(readLidarRecord(wrapped));
            }
            fis.close();

        } catch (Exception e) {
            System.out.println("Virhe: " + e.getMessage());
        }
    }

    public ArrayList<LidarInfo> getLidar() {
        return this.lidarLista;
    }

    public void getFirstEchoes() {

        ArrayList<Point3D> firstEchoes = new ArrayList<>();

        int n = this.lidarLista.size();
        for (int i = 0; i < n; i++) {
            this.firstEchoes.add(this.lidarLista.get(i).returns[3]);
        }
        System.out.println(this.firstEchoes.size());

    }

    static class SortbyDist implements Comparator<LidarInfo> {

        @Override
        public int compare(LidarInfo x, LidarInfo y) {
            return Double.compare(x.dist, y.dist);

        }
    }

    public double getNClosest(double x, double y, int nn, int echo) {

        int n = this.lidarLista.size();

        for (int i = 0; i < n; i++) {
            double x_lidar = this.lidarLista.get(i).returns[echo].getX();
            double y_lidar = this.lidarLista.get(i).returns[echo].getY();
            double dist = Math.sqrt(Math.pow((x_lidar - x), 2) + Math.pow((y_lidar - y), 2));
            this.lidarLista.get(i).dist = dist;
        }

        //Collections.sort(lidarLista, new SortbyDist());
        double result = 0;
        for (int j = 0; j < nn; j++) {
            result += this.lidarLista.get(j).returns[echo].getZ();
        }

        return result / nn;
    }

    public double getFirstEchoesMinX() {

        int n = firstEchoes.size();
        double min_x = 100000000;
        for (int i = 0; i < n; i++) {
            if (firstEchoes.get(i).getX() < min_x) {
                min_x = firstEchoes.get(i).getX();
            }
        }

        return min_x;
    }

    public double getFirstEchoesMaxX() {

        int n = firstEchoes.size();
        double max_x = 0;
        for (int i = 0; i < n; i++) {
            if (firstEchoes.get(i).getX() > max_x) {
                max_x = firstEchoes.get(i).getX();
            }
        }

        return max_x;

    }

    public double getFirstEchoesMinY() {

        int n = firstEchoes.size();
        double min_y = 1000000000;
        for (int i = 0; i < n; i++) {
            if (firstEchoes.get(i).getY() < min_y) {
                min_y = firstEchoes.get(i).getY();
            }
        }

        return min_y;
    }

    public double getFirstEchoesMaxY() {

        int n = firstEchoes.size();
        double max_y = 0;
        for (int i = 0; i < n; i++) {
            if (firstEchoes.get(i).getY() > max_y) {
                max_y = firstEchoes.get(i).getY();
            }
        }

        return max_y;
    }

    public static LidarInfo readLidarRecord(ByteBuffer in) throws IOException {

        LidarInfo l = new LidarInfo();
        l.gpsTime = in.getDouble();
        l.pulseCount = in.get();
        for (int i = 0; i < 4; i++) {
            l.returns[i] = readPoint3D(in);
        }
        for (int i = 0; i < 4; i++) {
            l.intensity[i] = in.getShort();
        }
        for (int i = 0; i < 4; i++) {
            l.range[i] = in.getDouble();
        }
        l.roll = in.getDouble();
        l.pitch = in.getDouble();
        l.heading = in.getDouble();
        l.angle = in.getDouble();
        l.poslidar = readPoint3D(in);
        l.stripNum = in.getShort();
        l.syncBit = in.get();
        l.res1 = in.get();
        l.res2 = in.get();
        l.res3 = in.get();
        return l;

    }

    public static Point3D readPoint3D(ByteBuffer in) throws IOException {

        double point_x = in.getDouble();
        double point_y = in.getDouble();
        double point_z = in.getDouble();
        Point3D point = new Point3D(point_x, point_y, point_z);

        return point;

    }

}
