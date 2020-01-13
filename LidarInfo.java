package imagereader;

/*
Contains data structure from one LiDAR pulse
*/
public class LidarInfo{
    
  

    public double gpsTime;
    public byte pulseCount;
    public Point3D[] returns; // 0 = last; 1 = 3rd; 2 = 2nd; 3 = 1st
    public int[] intensity; // 0 = last; 1 = 3rd; 2 = 2nd; 3 = 1st
    public double[] range; // 0 = last; 1 = 3rd; 2 = 2nd; 3 = 1st
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

    @Override
    public String toString() {
        String textout = "";

        textout += gpsTime + "," + pulseCount + ","
                + returns[0].getX() + "," + returns[0].getY() + "," + returns[0].getZ() + ","
                + returns[1].getX() + "," + returns[1].getY() + "," + returns[1].getZ() + ","
                + returns[2].getX() + "," + returns[2].getY() + "," + returns[2].getZ() + ","
                + returns[3].getX() + "," + returns[3].getY() + "," + returns[3].getZ() + ","
                + intensity[0] + "," + intensity[1]
                + "," + intensity[2] + "," + intensity[3] + ","
                + range[0] + "," + range[1] + "," + range[2] + ","
                + range[3] + "," + roll + "," + pitch
                + "," + heading + "," + angle + "," + poslidar.getX() + "," + poslidar.getY()
                + "," + poslidar.getZ() + "," + stripNum + "," + syncBit
                + "," + res1 + "," + res2 + "," + res3;

        return textout;
    }
}
