package gigaglobe;

public class Humanoid {
    public int w;
    public int h;
    public int x;
    public int y;

    
    public int global_x;
    public int global_y;
    
    public Humanoid(int w, int h, int x, int y) {
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
    }
    
    public void setGlobal_x(int global_x) {
        this.global_x = global_x;
    }
    public void setGlobal_y(int global_y) {
        this.global_y = global_y;
    }

}
