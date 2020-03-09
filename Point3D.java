
package imagereader;

public class Point3D {
    public double x;
    public double y;
    public double z;
    
    public Point3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Point3D(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        
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
