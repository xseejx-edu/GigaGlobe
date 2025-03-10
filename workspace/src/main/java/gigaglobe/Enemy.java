package gigaglobe;

public class Enemy {
    public int global_x;
    public int global_y;
    public int w;
    public int h;
    
    public int speed;
    
    public Enemy(int x, int y, int w, int h) {
        this.global_x = x;
        this.global_y = y;
        this.w = w;
        this.h = h;
        speed = 1;
    }
}
