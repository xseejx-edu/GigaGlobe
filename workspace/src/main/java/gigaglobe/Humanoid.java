package gigaglobe;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class Humanoid {
    public int x;// Deprecated
    public int y;// Deprecated

    public double width;
    public double height;
    public Rectangle2D.Double camera = new Rectangle2D.Double();
    public Color color;

    
    public double global_x;
    public double global_y;
    public double speed;
    
    public Humanoid(double gx, double gy, double w, double h, Color c) {
        speed = 3;
        global_y = gy;
        global_x = gx;
        this.width = w;
        this.height = h;
        color = new Color(c.getRGB());
    }

    public void getBigger(double w){
        this.width += w;
        this.height = width;
    }
}
