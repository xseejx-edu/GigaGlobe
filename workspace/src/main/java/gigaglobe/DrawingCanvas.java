package gigaglobe;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class DrawingCanvas extends JComponent{
    private int w;
    private int h;

    public DrawingCanvas(int w, int h){
        this.w = w;
        this.h = h;
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        Rectangle2D.Double r  = new Rectangle2D.Double(0,0,w,h);
        g2d.setColor(new Color(100,149,237));
        g2d.fill(r);
    }
}