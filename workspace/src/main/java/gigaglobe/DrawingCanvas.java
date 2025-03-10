package gigaglobe;

import javax.swing.*;

import javafx.event.ActionEvent;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class DrawingCanvas extends JComponent {
    private int w;
    private int h;
    public Point mouse = new Point(0,0);

    public DrawingCanvas(int w, int h) {
        this.w = w;
        this.h = h;
        mouseEvent();
        update();
    }
    public void mouseEvent(){
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.x = e.getX();
                mouse.y = e.getY();

                System.out.println("Mouse X: " + mouse.x + ", Mouse Y: " + mouse.y);
            }
        });
    }
    public void update(){
        Timer timer = new Timer(16, new ActionListener() { // ~60 FPS (1000ms / 16 ≈ 60)
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                //repaint();
                
            }
        });
        timer.start(); // Start the timer
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Random random = new Random();
        Graphics2D g2d = (Graphics2D) g;
        // Background
        Rectangle2D.Double r = new Rectangle2D.Double(0, 0, w, h);
        g2d.setColor(new Color(100, 149, 237));
        g2d.fill(r);

        // Ball
        Humanoid ball = new Humanoid(w / 2 - 25, h / 2 - 25, 50, 50);
        Ellipse2D.Double e = new Ellipse2D.Double(ball.w, ball.h, ball.x, ball.y);
        g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))); // R G B
        g2d.fill(e);
    }
}