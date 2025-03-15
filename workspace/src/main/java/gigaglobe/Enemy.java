package gigaglobe;

import java.awt.Color;

public class Enemy {
    public double global_x;
    public double global_y;
    public double w;
    public double h;
    
    public double speed;
    public Color color;
    
    public Enemy(double x, double y, double w, double h, Color c) {
        this.global_x = x;
        this.global_y = y;
        this.w = w;
        this.h = h;
        speed = 1;

        color = new Color(c.getRGB());
    }
}
