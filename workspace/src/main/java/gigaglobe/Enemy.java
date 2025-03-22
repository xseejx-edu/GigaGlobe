package gigaglobe;

import java.awt.Color;
import java.util.Random;

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

    public void setRandomposition(int wi, int he){
        Random random = new Random();
        global_x = random.nextInt(wi-100)+50;
        global_y = random.nextInt(he-100)+50;
    }
}
