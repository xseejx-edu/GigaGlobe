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
    public static int w;    // Windows width 
    public static int h;    // Windows height 
    public Point mouse = new Point(0,0);
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
        ball = new Humanoid((w/2)-25, (h/2)-25, 50, 50, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        user = new Ellipse2D.Double(ball.x, ball.y, ball.width, ball.height);// Instance of ball but with the purpose to be drawed

        // Enemies
        enemy = new Enemy(700, 500, 50, 50, new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        bot = new Ellipse2D.Double(enemy.global_x, enemy.global_y, enemy.w, enemy.h);// Instance of enemy but with the purpose to be drawed 
        
        // Append entities to the logical baseplate
        baseplate.createUser(user);
        baseplate.createEntity(bot);

        // Start events
        mouseEvent();
        update();
    }

    // Get mouse X and Y 
    public void mouseEvent(){
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.x = e.getX();
                mouse.y = e.getY();
            }
        });
    }

    // Update function which works as game-clock
    public void update(){
        Timer timer = new Timer(32, new ActionListener() { // ~60 FPS (1000ms / 16 ≈ 60) --> 1000/FPS
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if(mouse.x>w/2){
                    ball.global_x+=(ball.global_x<baseplate.w-ball.width) ? ball.speed:0;
                }else if(mouse.x<w/2){
                    ball.global_x-=(ball.global_x>ball.width-ball.width) ? ball.speed:0;
                }

                if(mouse.y<h/2){
                    ball.global_y-=(ball.global_y>0) ? ball.speed:0;
                }else if(mouse.y>h/2){
                    ball.global_y+=(ball.global_y<baseplate.h-ball.width) ? ball.speed:0;
                }

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
        Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1000, 1000);
        g2d.setColor(new Color(100, 149, 237));
        g2d.fill(r);
        drawLines(g2d, 500);
         
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
        
        // Draw enteties        -- with ArrayList<Ellipse2D.Double> store all the entities and then store them from the smallest to the greatest
        //                      -- and then with a foreach draw the
        g2d.setColor(ball.color); // R G B
        g2d.fill(user);
        g2d.setColor(enemy.color); // R G B
        g2d.fill(bot);


        g2d.setTransform(oldTransform);


        System.out.println("Ball X: "+ball.global_x+" Ball Y: "+ball.global_y+"\n Mouse X: "+mouse.x+" Mosue Y: "+mouse.y);            
    }

    // Method to draw lines every x pixels
    private void drawLines(Graphics2D g2d, int spacing) {
        g2d.setColor(Color.GRAY);
        for (int x = 0; x < w; x += spacing) {
            g2d.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += spacing) {
            g2d.drawLine(0, y, w, y);
        }
    }

}