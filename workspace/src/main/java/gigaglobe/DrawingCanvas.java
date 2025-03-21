package gigaglobe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class DrawingCanvas extends JComponent {
    public static int w; // Windows width
    public static int h; // Windows height
    public Point mouse = new Point(0, 0);
    Humanoid ball;
    Enemy enemy;
    Baseplate baseplate = new Baseplate(1000, 1000);
    Ellipse2D.Double user;
    Ellipse2D.Double bot;
    Random random = new Random();

    public DrawingCanvas(int wi, int he) {
        w = wi;
        h = he;

        // User
        ball = new Humanoid((w / 2) - 25, (h / 2) - 25, 50, 50,
                new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        user = new Ellipse2D.Double(ball.x, ball.y, ball.width, ball.height);// Instance of ball but with the purpose to
                                                                             // be drawed

        // Enemies
        enemy = new Enemy(700, 500, 50, 50, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        bot = new Ellipse2D.Double(enemy.global_x, enemy.global_y, enemy.w, enemy.h);// Instance of enemy but with the
                                                                                     // purpose to be drawed

        // Append entities to the logical baseplate
        baseplate.createUser(user);
        baseplate.createEntity(bot);

        // Start events
        mouseEvent();
        update();
    }

    // Get mouse X and Y
    public void mouseEvent() {
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.x = e.getX();
                mouse.y = e.getY();
            }
        });
    }

    // Update function which works as game-clock
    public void update() {
        Timer timer = new Timer(4, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Calcola la differenza tra la posizione del mouse e il centro della finestra
                double dx = mouse.x - (w / 2);
                double dy = mouse.y - (h / 2 + 50);
    
                // Calcola la distanza tra il mouse e il centro
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
    
                // Se il mouse è abbastanza vicino al centro, ferma la palla
                if (distance < 5) { // Soglia di prossimità
                    dx = 0;
                    dy = 0;
                } else {
                    // Normalizza la direzione (per avere un movimento uniforme)
                    dx /= distance;
                    dy /= distance;
                }
    
                // Muovi la palla in base alla direzione calcolata
                ball.global_x += dx * ball.speed;
                ball.global_y += dy * ball.speed;
    
                // Assicurati che la palla rimanga all'interno dei limiti del baseplate
                ball.global_x = Math.max(0, Math.min(baseplate.w - ball.width, ball.global_x));
                ball.global_y = Math.max(0, Math.min(baseplate.h - ball.height, ball.global_y));
    
                // Move the camera based on the ball position
                baseplate.moveLocally(ball);
    
                repaint();
            }
        });
        timer.start(); // Start the timer
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform oldTransform = g2d.getTransform();

        g2d.setClip(0, 0, DrawingCanvas.w, DrawingCanvas.h);
        g2d.translate(-ball.camera.x, -ball.camera.y);

        // Background
        Rectangle2D.Double r = new Rectangle2D.Double(0, 0, baseplate.w, baseplate.h);
        g2d.setColor(new Color(100, 149, 237));
        g2d.fill(r);

        drawLines(g2d, 50);

        // Instance of ball but updated
        user.x = ball.global_x;
        user.y = ball.global_y;
        user.width = ball.width;
        user.height = ball.height;

        // Instance of enemy but updated
        bot.x = enemy.global_x;
        bot.y = enemy.global_y;
        bot.width = enemy.w;
        bot.height = enemy.h;

        // Draw enteties -- with ArrayList<Ellipse2D.Double> store all the entities and
        // then store them from the smallest to the greatest
        // -- and then with a foreach draw the
        g2d.setColor(ball.color); // R G B
        g2d.fill(user);
        g2d.setColor(enemy.color); // R G B
        g2d.fill(bot);

        g2d.setTransform(oldTransform);

        System.out.println("Ball X: " + ball.global_x + " Ball Y: " + ball.global_y + "\n Mouse X: " + mouse.x
                + " Mosue Y: " + mouse.y);
    }

    // Method to draw lines every x pixels
    private void drawLines(Graphics2D g2d, int spacing) {
        g2d.setColor(Color.GRAY);
        for (int x = 0; x < baseplate.w; x += spacing) {
            g2d.drawLine(x, 0, x, baseplate.h);
        }
        for (int y = 0; y < baseplate.h; y += spacing) {
            g2d.drawLine(0, y, baseplate.w, y);
        }
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5)); // Set thickness to 5 pixels
        g2d.drawLine(0, 0, 0, baseplate.h); // Left border
        g2d.drawLine(baseplate.w, 0, baseplate.w, baseplate.h); // Right border
        g2d.drawLine(0, 0, baseplate.w, 0); // Top border
        g2d.drawLine(0, baseplate.h, baseplate.w, baseplate.h); // Bottom border
    }

}