package gigaglobe;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;

public class Enemy {
    public double global_x;
    public double global_y;
    public double w;
    public double h;
    
    public double speed;
    public Color color;


    public Enemy target;
    public Point2D.Double[] path;
    
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

    public void move(double x, double y){   // Deprecated
        global_x += x;
        global_y += y;
    }
    
    public void buildPath(){
        // The path is a line from the enemy to the target with a gap for each point of 10 pixels
        // The path is built so that the enemy avoids meeting other enemies that are bigger than him
    }

    public void move2NextPoint(){
        // Move the enemy to the next point in the path
    }

}
