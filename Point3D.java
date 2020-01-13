
package imagereader;

public class Point3D {
    private double x;
    private double y;
    private double z;
    
    public Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    double getZ(){
        return this.z;
    }
    double getY(){
        return this.y;
    }
    double getX(){
        return this.x;
    }
    
}
