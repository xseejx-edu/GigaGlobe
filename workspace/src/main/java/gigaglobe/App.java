package gigaglobe;

import javax.swing.*;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        int w = 640;
        int h = 480;
        JFrame frame = new JFrame();

        frame.setSize(w, h);
        frame.setTitle("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
