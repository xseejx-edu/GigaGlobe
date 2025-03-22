package gigaglobe;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Enemy {
    public double global_x;// Deprecated Soon
    public double global_y;// Deprecated Soon

    public double x;
    public double y;


    public double w;
    public double h;

    public int id;
    
    public double speed;
    public Color color;

    private EnemyType type;

    public Enemy target;
    public Point2D.Double[] path;
    
    public Enemy(double x, double y, double w, double h, Color c, EnemyType t, int id){
        this.global_x = x;
        this.global_y = y;
        this.x = x;
        this.y = y;


        this.w = w;
        this.h = h;
        speed = 1;
        type = t;
        color = new Color(c.getRGB());
        this.id = id;
    }

    public void setRandomposition(int wi, int he){
        Random random = new Random();
        global_x = random.nextInt(wi-100)+50;
        global_y = random.nextInt(he-100)+50;
    }

    public void move(double x, double y){   // Deprecated
        if(type == EnemyType.NEUTRAL)
            return;
        global_x += x;
        global_y += y;
    }
    
    public void buildRoute(Baseplate baseplate){
        // The path is a line from the enemy to the target with a gap for each point of 10 pixels
        // The path is built so that the enemy avoids meeting other enemies that are bigger than him


        // First we check if there is already a path
        if(path != null){
            // Path already exists
        }

        // Build the path
        // Fist we check the sourroundings of the enemy -
        // We check first distance to each enemy in the map
        //ArrayList<Double> enemies_distance = new ArrayList<Double>();
        ArrayList<Target> targets = new ArrayList<Target>();
        for (Enemy enemy : baseplate.entities) {
            if(enemy == this)
                continue;
            double distance = Math.sqrt(Math.pow(global_x - enemy.global_x, 2) + Math.pow(global_y - enemy.global_y, 2));
            targets.add(new Target(distance, new Point2D.Double(enemy.global_x, enemy.global_y), enemy.w, enemy.id));
        }

        // Now we got the distances to each enemy
        // We give priority to our EnemyType - if we are a pray we will care about the distance only
        // If we are a predator we will not care about the distance but about the width of the enemy
        // If we are neutral we will not move at all
        if(type == EnemyType.PREY){
            // We sort the enemies by distance from the closest to the farthest
            targets.sort(Comparator.comparingDouble(Target::getDistance).reversed());
        }else if(type == EnemyType.PREDATOR){
            // We sort the enemies by width from the smallest to the biggest
            targets.sort(Comparator.comparingDouble(Target::getWidth));
        }else{
            return;// We are neutral
        }
        ArrayList<Path> paths = new ArrayList<Path>();
        // Now we have an Array containing the enemies sorted by distance or width
        // We will build a path that directs the enemy to all the targets using a straight line
        // Each Line will have a counter which will tell how many enemies are in the way (they can be near)
        // We will then choose the Line with less count and we will analyze the surroundings of that line
        for (Target target : targets) {
            int dangerLevel = 0;
            Point2D.Double[] dangerZone = new Point2D.Double[0];
            // We first check if target is smaller than us and then we proceed
            if(target.width >= w)
                continue;
            // We already have the x and y
            // So we need to take a favourite path by checkign the sourroundings
            // To do that we will check x and y for each target
            // If target is close to another target with a gap of x and y of 30 pixels we will count that as +1 near enemy
            // and if that enemy is bigger than us we will count that as danger zone so +2
            for (Target nearTarget : targets) {
                if(nearTarget == target)
                    continue;
                if(Math.abs(target.position.x - nearTarget.position.x) < 30 && Math.abs(target.position.y - nearTarget.position.y) < 30){
                    // We get the distance from target to nearTarget and if it is negative is before our taret so in the path
                    // if it is positive is after our target so we will not count it as a danger
                    if(nearTarget.width < w)
                        continue;

                    dangerLevel++; // By default is 1
                    if(target.distance - nearTarget.distance > 0 || target.distance - nearTarget.distance < -50) // is before our target +1
                       dangerLevel++;    
                    else
                        continue; // is after our target so we will not count it as a danger and also it is far from standard range
                    
                    // Otherwise we will add the danger zone to the path
                    Point2D.Double[] newDangerZone = new Point2D.Double[dangerZone.length+1];
                    for (int i = 0; i < dangerZone.length; i++) {
                        newDangerZone[i] = dangerZone[i];
                    }
                    newDangerZone[dangerZone.length] = nearTarget.position;
                    dangerZone = newDangerZone;
                }
            }
            // We have the danger level for this target
            // We will add this to an array of path which will contain:
            // danger, distance, x and y (Point), id
            paths.add(new Path(target.distance, target.id, target.position, dangerLevel, target.width, dangerZone));
        }

        // Now we have paths but we have to decide which one to take
        // The array path is sorted by distance only and that indicates if we are a prey the sort will be : 1, 2, 3 ..
        // If we are a predator the sort will be : 3, 2, 1 ..

        // So for now by default we will take the route with less danger level
        paths.sort(Comparator.comparingInt(Path::getDangerLevel));
        // We discard all the paths with danger level higher than the first one and keep the ones witht he same danger level
        int dangerLevel = paths.get(0).getDangerLevel();
        ArrayList<Path> paths2 = new ArrayList<Path>();
        for (Path path : paths) {
            if(path.getDangerLevel() == dangerLevel){
                paths2.add(path);
            }
        }
        // Now we have the paths with the same danger level and based on what we are we will choose the one:
        // If we are a prey we will choose the one with the shortest distance
        // If we are a predator we will choose the one with bigger width
        if(type == EnemyType.PREY){
            paths2.sort(Comparator.comparingDouble(Path::getDistance)); // From short to long
        }else if(type == EnemyType.PREDATOR){
            paths2.sort(Comparator.comparingDouble(Path::getWidth).reversed());// From Big to small
        }

        // Now that we have our paths we need to build it
        // To build the path we have to simulate that we are going on that path
        // So where the danger level is we will then make turns to avoid the danger
        // The turns could be multiple so we will have to check the surroundings again
        // We will always take the safest path
        // Then once we have our paths with x and y taht points at a turn and distance is the distance from the previous turn
        // We have built the path

        // route.createRoute(path2, this.x, this.y, baseplate, this.id);
        // Building Path
        ArrayList<Route> routes = new ArrayList<Route>();// Thsi will contain the routes
        for (Path path : paths2) {
            // Create a route for each path2
        }

    }

    public void move2NextPoint(){
        // Move the enemy to the next point in the path
    }

}
