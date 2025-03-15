package gigaglobe;

import javax.swing.*;

public class App 
{
    public static void main( String[] args )
    {

        int w = 640;
        int h = 480;
        JFrame frame = new JFrame();
        DrawingCanvas d = new DrawingCanvas(w, h);

        frame.setSize(w, h);
        frame.setTitle("Main");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Components 

        frame.add(d);

    }
}
