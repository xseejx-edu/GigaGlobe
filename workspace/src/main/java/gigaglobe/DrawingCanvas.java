package gigaglobe;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class DrawingCanvas extends JComponent{
    private int w;
    private int h;

    public DrawingCanvas(int w, int h){
        this.w = w;
        this.h = h;
    }

    @Override
    protected void paintComponent(Graphics g){
        Random random = new Random();
        Graphics2D g2d = (Graphics2D) g;
        // Background
        Rectangle2D.Double r  = new Rectangle2D.Double(0,0,w,h);
        g2d.setColor(new Color(100,149,237));
        g2d.fill(r);

        // Ball
        Humanoid ball = new Humanoid(w/2-25,h/2-25,50,50);
        Ellipse2D.Double e = new Ellipse2D.Double(ball.w, ball.h, ball.x, ball.y);
        g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));//R G B
        g2d.fill(e);
    }
}