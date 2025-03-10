package gigaglobe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class DrawingCanvas extends JComponent {
    private int w;
    private int h;

    public DrawingCanvas(int w, int h) {
        this.w = w;
        this.h = h;

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                System.out.println("Mouse X: " + x + ", Mouse Y: " + y);
            }
        });
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Drawing Canvas");
        DrawingCanvas canvas = new DrawingCanvas(500, 500);
        frame.add(canvas);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}