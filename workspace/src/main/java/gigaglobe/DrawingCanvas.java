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
    private int score = 0;
    public static int w; // Windows width
    public static int h; // Windows height
    public Point mouse = new Point(0, 0);
    Humanoid ball;
    Enemy enemy;
    Baseplate baseplate = new Baseplate(1000, 1000);
    Ellipse2D.Double user;
    Ellipse2D.Double bot;
    Ellipse2D.Double mouseBall;
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

        mouseBall = new Ellipse2D.Double(mouse.x, mouse.y, 20, 20);

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
                // Update mouseBall position, adjusting for camera offset
                mouseBall.x = mouse.x + ball.camera.x - 10; // Center the ball on the mouse pointer
                mouseBall.y = mouse.y + ball.camera.y - 10; // Center the ball on the mouse pointer
            }
        });
    }

    private Point getRandomDirection() {
        int angle = random.nextInt(360);
        double radians = Math.toRadians(angle);
        int x = (int) (Math.cos(radians) * ball.speed);
        int y = (int) (Math.sin(radians) * ball.speed);
        return new Point(x, y);
    }

    // Update function which works as game-clock
    public void update() {
        Timer timer = new Timer(8, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Update mouseBall position, adjusting for camera offset
                mouseBall.x = mouse.x + ball.camera.x - 10; // Center the ball on the mouse pointer
                mouseBall.y = mouse.y + ball.camera.y - 10; // Center the ball on the mouse pointer

                // Make the ball follow the mouseBall trajectory
                double dx = mouseBall.x - ball.global_x;// Distance x
                if (dx >= 0 && (ball.global_x + ball.width > baseplate.w)) {
                    dx = 0;
                } else if (dx < 0 && ball.global_x < 0) {
                    dx = 0;
                }
                double dy = mouseBall.y - ball.global_y;// Distance y
                if (dy >= 0 && (ball.global_y + ball.height >= baseplate.h)) {
                    dy = 0;
                } else if (dy < 0 && ball.global_y <= 0) {
                    dy = 0;
                }

                double distance = Math.sqrt(dx * dx + dy * dy);// Distance between the ball and the mouseBall
                if (distance > ball.width / 3) {
                    ball.global_x += (dx / distance) * ball.speed;// Move the ball at a certian speed decided by the
                                                                  // distance
                    ball.global_y += (dy / distance) * ball.speed;// Move the ball at a certian speed decided by the
                                                                  // distance
                } else {
                    Point direction = getRandomDirection();
                    ball.global_x += direction.x;
                    ball.global_y += direction.y;

                    // Ensure the ball stays within the boundaries
                    if (ball.global_x < 0)
                        ball.global_x = 0;
                    if (ball.global_y < 0)
                        ball.global_y = 0;
                    if (ball.global_x + ball.width > baseplate.w)
                        ball.global_x = baseplate.w - ball.width;
                    if (ball.global_y + ball.height > baseplate.h)
                        ball.global_y = baseplate.h - ball.height;

                    if (checkIfEating(ball, enemy)) {
                        enemy.riposiziona();
                        score++;
                    }

                }
                // Move the camera based on the ball position
                baseplate.moveLocally(ball);

                repaint();
            }
        });
        timer.start(); // Start the timer
    }

    public boolean checkIfEating(Humanoid ball, Enemy target) {
        // Calcola il centro della palla
        double bX = ball.global_x + (ball.width / 2.0);
        double bY = ball.global_y + (ball.height / 2.0);

        // Calcola il centro della palla target
        double tX = target.global_x + (target.w / 2.0);
        double tY = target.global_y + (target.h / 2.0);

        if (Math.abs(tX - bX) >= ball.width / 2.0 && Math.abs(tY - bY) >= ball.height / 2.0) {
            return false;
        }
        return true;
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

        // Draw the mouse-following ball
        // g2d.setColor(Color.RED); // Set the color for the mouse-following ball
        // g2d.fill(mouseBall);

        g2d.setTransform(oldTransform); // Ripristina la trasformazione per disegnare il punteggio
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Score: " + score, 20, 30); // Disegna il punteggio in alto a sinistra

        g2d.setTransform(oldTransform);

        // System.out.println("Ball X: "+ball.global_x+" Ball Y: "+ball.global_y+"\n
        // Mouse X: "+mouse.x+" Mosue Y: "+mouse.y);
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