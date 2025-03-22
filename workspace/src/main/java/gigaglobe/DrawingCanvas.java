package gigaglobe;

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Random;

public class DrawingCanvas extends JComponent {
    private int score = 0;
    private static final int ENEMIES = 10;
    public static int w; // Windows width
    public static int h; // Windows height
    public Point mouse = new Point(0, 0);
    Humanoid ball;
    Enemy enemy;
    Baseplate baseplate = new Baseplate(1000, 1000);
    Ellipse2D.Double user;
    Ellipse2D.Double bot;
    Ellipse2D.Double mouseBall;
    double zoomFactor = 1;
    Random random = new Random();

    public DrawingCanvas(int wi, int he) {
        w = wi;
        h = he;

        // User
        ball = new Humanoid((w / 2) - 25, (h / 2) - 25, 50, 50,
                new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        user = new Ellipse2D.Double(ball.x, ball.y, ball.width, ball.height);// Instance of ball but with the purpose to
                                                                             // be drawed
        // Append entities to the logical baseplate
        baseplate.createUser(user);


        // Non-Static Enemies (Enemies that move)
        for (int i = 0; i < ENEMIES; i++) {
            double temp_w = random.nextDouble(200) + 10;// Random width and height
            Enemy e = new Enemy(random.nextDouble(baseplate.w - temp_w)+temp_w/2, // x
                                random.nextDouble(baseplate.h - temp_w), // y
                                temp_w, temp_w,
                                new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)), // RGB
                                EnemyType.PREDATOR,
                                i   // id
                                );
            baseplate.createEntity(e);
        }

        // Static Enemies (Enemies that don't move)


        // Mouse-following ball
        mouseBall = new Ellipse2D.Double(mouse.x, mouse.y, 20, 20);

        // Start events
        mouseEvent();
        update();
        moveEnemy();
    }

    // Get mouse X and Y
    public void mouseEvent() {
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.x = e.getX();
                mouse.y = e.getY();
                // Update mouseBall position, adjusting for camera offset
                mouseBall.x = (mouse.x / zoomFactor) + ball.camera.x - 10; // Center the ball on the mouse pointer
                mouseBall.y = (mouse.y / zoomFactor) + ball.camera.y - 10; // Center the ball on the mouse pointer

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

    // Shrinkign Ball Animation
    private void shrinkBall() {
        Timer shrinkTimer = new Timer(16, new ActionListener() { // ~60 FPS (1000ms / 16 ≈ 60)
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (ball.width > 0 && ball.height > 0) {
                    ball.width -= 0.1;
                    ball.height -= 0.1;
                    if (ball.width < 0) ball.width = 0;
                    if (ball.height < 0) ball.height = 0;
                    repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        shrinkTimer.start();
    }
    
    // Update function which works as game-clock
    public void update() {
        Timer timer = new Timer(8, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // Update mouseBall position, adjusting for camera offset
                mouseBall.x = (mouse.x / zoomFactor) + ball.camera.x - 10; // Center the ball on the mouse pointer
                mouseBall.y = (mouse.y / zoomFactor) + ball.camera.y - 10; // Center the ball on the mouse pointer
                ball.width -= 0.001;
                ball.height -= 0.001;
                if (ball.width < 25) {                    
                    shrinkBall();
                    return; // Game Over
                }
                // Make the ball follow the mouseBall trajectory
                double dx = mouseBall.x - (ball.global_x + ball.width /2);// Distance x
                if (dx >= 0 && (ball.global_x + ball.width > baseplate.w)) {
                    dx = 0;
                } else if (dx < 0 && ball.global_x < 0) {
                    dx = 0;
                }
                double dy = mouseBall.y - (ball.global_y + ball.height / 2);// Distance y
                if (dy >= 0 && (ball.global_y + ball.height >= baseplate.h)) {
                    dy = 0;
                } else if (dy < 0 && ball.global_y <= 0) {
                    dy = 0;
                }
                double distance = Math.sqrt(dx * dx + dy * dy);// Distance between the ball and the mouseBall
                if (distance > ball.width / 2) {
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
                }
                
                // Check for collision with enemies
                // Using Iterator to avoid ConcurrentModificationException
                for (Iterator<Enemy> iterator = baseplate.entities.iterator(); iterator.hasNext();) {
                    Enemy i = iterator.next();
                    try {
                        if (checkCollision(ball, i) && ball.width > i.w) {
                            ball.getBigger(i.w / 5);
                            iterator.remove(); // Use iterator to remove the element
                            score++;
                        }
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }

                // Move the camera based on the ball position
                baseplate.moveLocally(ball, score);
                repaint();
            }
        });
        timer.start(); // Start the timer
    }

    public void moveEnemy(){
        Timer timer = new Timer(8, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                for (Enemy i : baseplate.entities) {
                    try{
                        // The enemy will make a path which are a vector of points
                        // The points will be modify to the selected target that the enemy wants to reach
                        // If another smaller enemy is near it it will change the target and path                    
                        Point direction = getRandomDirection();
                        // Move the enemy only when its width is greater than 25 else he will become a unmoving target
                        i.move(direction.x, direction.y);
                        i.w -= 0.001;
                        i.h -= 0.001;
                    }catch (Exception ex){
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
                repaint();
            }
        });
        timer.start();
    }

    public boolean checkCollision(Humanoid ball, Enemy target) {
        // Calculate center of ball
        double bX = ball.global_x + (ball.width / 2.0);
        double bY = ball.global_y + (ball.height / 2.0);

        // Calculate center of target
        double tX = target.global_x + (target.w / 2.0);
        double tY = target.global_y + (target.h / 2.0);
        if (Math.abs(tX - bX) <= ball.width / 2.0 && Math.abs(tY - bY) <= ball.height / 2.0) {
            return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        AffineTransform oldTransform = g2d.getTransform();

        g2d.setClip(0, 0, DrawingCanvas.w, DrawingCanvas.h);
        // Scale -- greater = zoomed in and smaller = zoomed out
        zoomFactor = Math.max(0.3, 1.0 - (ball.width / baseplate.w )); // Adjust the zoom based on the ball size and baseplate
        ball.speed = Math.max(1, 3 * zoomFactor); // Adjust the speed based on the zoom factor
        g2d.scale(zoomFactor, zoomFactor);  // Applay the scale

        // Adjust the camera for the scale
        ball.camera.x = ball.global_x + ball.width / 2 - (DrawingCanvas.w / zoomFactor) / 2;
        ball.camera.y = ball.global_y + ball.height / 2 - (DrawingCanvas.h / zoomFactor) / 2;
        ball.camera.width = DrawingCanvas.w / zoomFactor;
        ball.camera.height = DrawingCanvas.h / zoomFactor;

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


        // Draw the user
        g2d.setColor(ball.color); // R G B
        g2d.fill(user);

        // Draw enteties -- with ArrayList<enemy> store all the entities and
        // then store them from the smallest to the greatest
        
        baseplate.entities.sort((a, b) -> {return (int)(a.w) - (int)(b.w);});// will sort the entities from the smallest to the greatest
        
        for (Enemy i : baseplate.entities) {
            try {
                g2d.setColor(i.color);
                g2d.fill(new Ellipse2D.Double(i.global_x, i.global_y, i.w, i.h));
            }catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }            
        }
        // Draw the mouse-following ball
        g2d.setColor(Color.RED); // Set the color for the mouse-following ball
        g2d.fill(mouseBall);

        g2d.setTransform(oldTransform);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("Score: " + score, 20, 30);
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