package gigaglobe;

import java.awt.geom.Point2D;

public class Target {
    public Double distance;
    public Point2D.Double position;
    public double width;
    public int id;

    public Target(Double distance, Point2D.Double position, double width, int id) {
        this.distance = distance;
        this.position = position;// x and y
        this.width = width;
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }
    public double getWidth() {
        return width;
    }
}
