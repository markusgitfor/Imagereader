package imagereader;

/*
Contains data structure from one LiDAR pulse
 */
public class LidarInfo {

    public double gpsTime;
    public byte pulseCount;
    public Point3D[] returns; // 0 = 4th echo; 1 = 3th echo; 2 = 2nd echo; 3 = 1st echo
    public int[] intensity; // 0 = 4th echo; 1 = 3th echo; 2 = 2nd echo; 3 = 1st echo
    public double[] range; // 0 = 4th echo; 1 = 3th echo; 2 = 2nd echo; 3 = 1st echo
    public double roll;
    public double pitch;
    public double heading;
    public double angle;
    public Point3D poslidar;
    public int stripNum;
    public byte syncBit;
    public short res1;
    public short res2;
    public short res3;
    public double dist;

    public LidarInfo() {

        this.returns = new Point3D[4];
        this.intensity = new int[4];
        this.range = new double[4];
        this.dist = 0;

    }

    public double getZ0() {
        double z = (this.returns[0].getZ());
        return z;
    }

}
