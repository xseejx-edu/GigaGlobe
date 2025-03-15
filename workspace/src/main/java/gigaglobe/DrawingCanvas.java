package gigaglobe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class DrawingCanvas extends JComponent {
    public static int w;
    public static int h;
    public Point mouse = new Point(0,0);
    Humanoid ball;
    Enemy enemy;
    Baseplate baseplate = new Baseplate(1000, 1000);

    public DrawingCanvas(int wi, int he) {
        w = wi;
        h = he;
    }
    public void mouseEvent(){
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.x = e.getX();
                mouse.y = e.getY();

                //System.out.println("Ball X: " + ball.global_x + ", Ball Y: " + ball.global_y);
            }
        });
    }
    public void update(){
        Timer timer = new Timer(50, new ActionListener() { // ~60 FPS (1000ms / 16 ≈ 60) --> 1000/FPS
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                //repaint();
                System.out.println("Ball X: " + ball.global_x + ", Ball Y: " + ball.global_y);
                if(mouse.x>w/2){
                    ball.global_x+=(ball.global_x<baseplate.w) ? ball.speed:0;
                }else if(mouse.x<w/2){
                    ball.global_x-=(ball.global_x>0) ? ball.speed:0;
                }

                if(mouse.y<h/2){
                    ball.global_y-=(ball.global_y>0) ? ball.speed:0;
                }else if(mouse.y>h/2){
                    ball.global_y+=(ball.global_y<baseplate.h) ? ball.speed:0;
                }
                baseplate.moveLocally(ball.global_x, ball.global_y);
            }
        });
        timer.start(); // Start the timer
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Random random = new Random();
        Graphics2D g2d = (Graphics2D) g;
        // Background
        Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1000, 1000);
        g2d.setColor(new Color(100, 149, 237));
        g2d.fill(r);

        // Init Ball
        ball = new Humanoid((w/2)-25, (h/2)-25, 50, 50); // x and y are local -- globals are set to 0 0 default
        enemy = new Enemy(700, 500, 50, 50); 
        Ellipse2D.Double user = new Ellipse2D.Double(ball.x, ball.y, ball.w, ball.h);// users are declared with local x and y

        Ellipse2D.Double bot = new Ellipse2D.Double(enemy.global_x, enemy.global_y, enemy.w, enemy.h);// Bots are declared with global x and y
        
        /*
         Rectangle2D.Double test = new Rectangle2D.Double(-ball.x, -ball.y, w, h);
        g2d.setColor(new Color(255, 0, 0));
        g2d.fill(test);
         */
        

        g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))); // R G B
        g2d.fill(user);
        g2d.fill(bot);




        baseplate.createUser(user);
        baseplate.createEntity(bot);

        mouseEvent();
        update();
    }
}