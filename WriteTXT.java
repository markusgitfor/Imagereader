package imagereader;

import java.io.PrintWriter;
import java.util.*;

/**
 *
 * @author Markus
 */
public class WriteTXT {

    public void writeTxt(String filename, String lidarname) throws Exception {

        LidarReader n = new LidarReader("052_098.bin");
        ArrayList<LidarInfo> lista = n.getLidar();

        PrintWriter file = new PrintWriter(filename);

        HyytialaCoordinateConversion m = new HyytialaCoordinateConversion();

        file.println("X,Y,Z");
        for (LidarInfo point : lista) {
            Point3D p = point.returns[3];
            Point3D pointUTM = m.kkjToUtm(p);
            file.println(pointUTM.getX() + "," + pointUTM.getY() + "," + pointUTM.getZ());

        }
        file.close();
    }
}
