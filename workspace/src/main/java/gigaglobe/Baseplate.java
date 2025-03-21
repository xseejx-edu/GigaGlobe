package gigaglobe;

import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Point2D;
import java.util.ArrayList;



public class Baseplate {
    public int w;
    public int h;
    public ArrayList<Enemy> entities = new ArrayList<>();  // x & y of map's object
    protected Point2D.Double userPos;
    public Baseplate(int w, int h) {
        this.w = w;
        this.h = h;
    }
                            
    // Moves the camera in the map
    public void moveLocally(Humanoid ball, int score){
        // Get the local map which is the window size
        int w = DrawingCanvas.w;// this is the screen width
        int h = DrawingCanvas.h;// this is the screen height
        
        ball.camera.x = ball.global_x + ball.width / 2 - DrawingCanvas.w / 2;
        ball.camera.y = ball.global_y + ball.height / 2 - DrawingCanvas.h / 2;
        ball.camera.width  =w;
        ball.camera.height  =h;
    }

    public void createEntity(Enemy entity){
        entities.add(entity);
    }

    public void createUser(Double user) {
        userPos = new Point2D.Double(user.x, user.y);
    }
}
