package gigaglobe;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.geom.Rectangle2D;


public class Baseplate {
    public int w;
    public int h;
    ArrayList<Point2D> entities = new ArrayList<>();  // x & y of map's object
    protected Point2D.Double userPos;
    public Baseplate(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void moveLocally(int x, int y){
        // we get the local map which is the window size
        int w = DrawingCanvas.w;// this is the screen width
        int h = DrawingCanvas.h;// this is the screen height
        double globalx_camera = -((w/2)-25);
        double globaly_camera = -(h/2)-25;
                                                //          x y
        Rectangle2D.Double camera = new Rectangle2D.Double(globalx_camera, globaly_camera, w, h);

    }

    public void createEntity(Ellipse2D.Double entity){
        entities.add(new Point2D.Double(entity.x, entity.y));// the x and y are global
    }

    public void createUser(Double user) {
        userPos = new Point2D.Double(user.x, user.y);
    }
}
