package gigaglobe;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;


class InnerRoute {
    public Point2D.Double Final_destination;
    public List<Point2D.Double> enemiesIntheField;

    public InnerRoute(Point2D.Double Fd, List<Point2D.Double> eIF, Point2D.Double my_XY){
        this.Final_destination = Fd;
        this.enemiesIntheField = eIF;
    }

    public void analyzeRoute(){
        
    }
}


public class Route {
    public ArrayList<CustomPoints> points;
    public int id;
    public double distance;
    public int turns;
    public int dangerLevel;
    public double initialX;
    public double initialY;
    // reminder:
    /*
     Now the path is a straight line from the enemy to the target
     We have to Analyze it and modify to avoid other enemies
     */
    public void createRoute(Path path, double x, double y, Baseplate baseplate, int idOfMyself){ // x and y are the position of the enemy NOT THE TARGET
        Route route = new Route();
        // Path is made by:
            // Distance of the enemy
            // Position of the enemy x and y
            // Id of the enemy
            // Danger level of the enemy
            // Width of the enemy
            // Danger zone of the enemy
        // So id will remain the same
        //route.id = path.id;
        // so Let make our first point, from start to danger zone
        //points.add(new CustomPoints(x, y, path.dangerZone[0].x, path.dangerZone[0].y));
        // Now we have to build the possible turns that we can make
        // How to make build the turns path:
           // We wil check the surroundings of the enemy
           // We will get the path with the less danger level
           // Where to Stop:
              // We managed to choose a path with less danger level BUT the path will redirect us to the unwanted target
              // We have to change by a little bit our path so that we are far enough from the unwanted target
              // The "far enough" is a distance that we have to calculate using the width of the enemy + a default value (40)   -- We could make that it will decide based on speed but for now let make it simple
              // So now that we chose our right path we have to make a Stop to then turn again and try checkign if the direct path to the target is free
              // To calculate the stops we have to get the x and y of unwanted target and then calculate the distance between the nearest enemy
              // After that we will choose to stop at the middle of the distance between the nearest enemy and the unwanted target
        
             // List<Point2D.Double> enemiesIntheField = checkSurroundings(x, y, path.position, baseplate);
            for (int i = 0; i < path.dangerZone.length; i++) {
                List<Point2D.Double> enemiesIntheField = checkSurroundings(path.dangerZone[i].x, path.dangerZone[i].y, path.position, baseplate, path.id, idOfMyself);
                // make a simple new path to the target
                InnerRoute innerRoute = new InnerRoute(path.position, enemiesIntheField, path.dangerZone[i]);
            }
        


    }


    public static List<Point2D.Double> checkSurroundings(double x, double y, Point2D.Double dangerZone, Baseplate baseplate, int id, int idOfMyself){
        // From x and y we will check the surroundings in an area of the next Danger Zone which is Path 
        Point2D.Double center = new Point2D.Double(x, y);
        double radius = center.distance(dangerZone);// Our Danger Zone

        List<Point2D.Double> points = new ArrayList<>();
        for (Enemy enemy : baseplate.entities) {   
            if(enemy.id == id || enemy.id == idOfMyself)
                continue;           
            points.add(new Point2D.Double(enemy.x, enemy.y));
        }
        List<Point2D.Double> enemiesIntheField = findPointsWithinRadius(points, center, radius);
        // Now we have the enemies in the field
        return enemiesIntheField;
    }

    public static List<Point2D.Double> findPointsWithinRadius(List<Point2D.Double> points, Point2D.Double center, double radius) {
        List<Point2D.Double> result = new ArrayList<>();

        for (Point2D.Double p : points) {
            if (center.distance(p) <= radius) {
                result.add(p);
            }
        }
        return result;
    }
}
