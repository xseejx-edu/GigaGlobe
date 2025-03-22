package gigaglobe;

import java.awt.geom.Point2D;

public class Path {
    public double distance;
    public Point2D.Double position;
    public int id;
    public double width;
    public int dangerLevel = 0;
    public Point2D.Double[] dangerZone;// For turning around

    public Path(double distance, int id, Point2D.Double position, int dangerLevel, double width, Point2D.Double[] dangerZone) {
        this.position = position;
        this.distance = distance;
        this.id = id;
        this.dangerLevel = dangerLevel;
        this.width = width;
        this.dangerZone = dangerZone;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public double getDistance() {
        return distance;
    }

    public double getWidth() {
        return width;
    }
    
}
