package gigaglobe;

public class Humanoid {
    public int x;
    public int y;
    public int w;
    public int h;

    
    public int global_x;
    public int global_y;
    public int speed;
    
    public Humanoid(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        speed = 1;
        global_y = 0;
        global_x = 0;
    }
}
