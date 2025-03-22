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

    public EnemyType type;

    public Enemy target;
    public Point2D.Double[] path;

    private Baseplate baseplate;
    
    public Enemy(double x, double y, double w, double h, Color c, EnemyType t, int id, Baseplate b){
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
        this.baseplate = b;


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

    public void buildRoute(Baseplate baseplate) {
        // If we're neutral, we don't move
        if (type == EnemyType.NEUTRAL) {
            return;
        }
        
        // Find potential targets
        ArrayList<Enemy> potentialTargets = new ArrayList<>();
        for (Enemy enemy : baseplate.entities) {
            // Skip ourselves
            if (enemy.id == this.id) {
                continue;
            }
            
            // PREDATORS target smaller enemies, PREY avoid larger enemies
            if ((type == EnemyType.PREDATOR && enemy.w < this.w) || 
                (type == EnemyType.PREY && enemy.w > this.w)) {
                potentialTargets.add(enemy);
            }
        }
        
        // If no suitable targets, move randomly
        if (potentialTargets.isEmpty()) {
            moveRandomly();
            return;
        }
        
        // Choose target based on type
        Enemy chosenTarget = null;
        
        if (type == EnemyType.PREDATOR) {
            // Predators prioritize smaller enemies
            potentialTargets.sort(Comparator.comparingDouble(e -> e.w));
            chosenTarget = potentialTargets.get(0); // Smallest prey
        } else if (type == EnemyType.PREY) {
            // Prey prioritize distance (farthest from predators)
            potentialTargets.sort(Comparator.comparingDouble(e -> 
                -calculateDistance(this.global_x, this.global_y, e.global_x, e.global_y)));
            chosenTarget = potentialTargets.get(0); // Farthest predator
        }
        
        // Set the target
        this.target = chosenTarget;
    }
    
    public void moveToTarget() {
        // If no target or we're neutral, don't move
        if (target == null || type == EnemyType.NEUTRAL) {
            return;
        }
        
        // Calculate direction to target
        double targetX = target.global_x;
        double targetY = target.global_y;
        
        // If we're prey, move AWAY from predators
        if (type == EnemyType.PREY) {
            // Reverse the direction
            double dx = this.global_x - targetX;
            double dy = this.global_y - targetY;
            
            // Calculate a point further away in the same direction
            targetX = this.global_x + dx;
            targetY = this.global_y + dy;
        }
        
        // Check for obstacles in the path
        boolean obstacleFound = checkForObstacles(targetX, targetY);
        
        if (obstacleFound && type == EnemyType.PREDATOR) {
            // Find a path around obstacles
            Point2D.Double detourPoint = findDetourPoint(targetX, targetY);
            targetX = detourPoint.x;
            targetY = detourPoint.y;
        }
        
        // Calculate movement vector
        double dx = targetX - this.global_x;
        double dy = targetY - this.global_y;
        double distance = Math.sqrt(dx*dx + dy*dy);
        
        // Normalize and apply speed
        if (distance > 0) {
            dx = (dx / distance) * speed;
            dy = (dy / distance) * speed;
            
            // Move the enemy
            this.global_x += dx;
            this.global_y += dy;
            this.x = this.global_x;
            this.y = this.global_y;
        }
        
        // Check if we reached or need to switch targets
        if ((type == EnemyType.PREDATOR && distance < this.w) ||
            (type == EnemyType.PREY && distance > 300)) {
            // Find a new target
            target = null;
        }
    }
    
    private boolean checkForObstacles(double targetX, double targetY) {
        // Simple obstacle detection
        for (Enemy enemy : baseplate.entities) {
            // Skip ourselves and our target
            if (enemy.id == this.id || (target != null && enemy.id == target.id)) {
                continue;
            }
            
            // Skip smaller enemies for predators
            if (type == EnemyType.PREDATOR && enemy.w < this.w) {
                continue;
            }
            
            // Check if enemy is between us and the target
            if (isEnemyInPath(this.global_x, this.global_y, targetX, targetY, 
                              enemy.global_x, enemy.global_y, enemy.w)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isEnemyInPath(double startX, double startY, double endX, double endY,
                                  double enemyX, double enemyY, double enemyWidth) {
        // Vector from start to end
        double dx = endX - startX;
        double dy = endY - startY;
        double length = Math.sqrt(dx*dx + dy*dy);
        
        // Normalize
        double unitX = dx / length;
        double unitY = dy / length;
        
        // Vector from start to enemy
        double ex = enemyX - startX;
        double ey = enemyY - startY;
        
        // Project enemy onto path vector
        double projection = ex * unitX + ey * unitY;
        
        // Enemy is behind us or past the end point
        if (projection < 0 || projection > length) {
            return false;
        }
        
        // Find closest point on line to enemy
        double closestX = startX + unitX * projection;
        double closestY = startY + unitY * projection;
        
        // Check if enemy is close enough to path to be an obstacle
        double distanceToPath = Math.sqrt(
            Math.pow(closestX - enemyX, 2) + 
            Math.pow(closestY - enemyY, 2)
        );
        
        return distanceToPath < (enemyWidth + this.w) / 2;
    }
    
    private Point2D.Double findDetourPoint(double targetX, double targetY) {
        // Simple detour calculation - go perpendicular to the target direction
        double dx = targetX - this.global_x;
        double dy = targetY - this.global_y;
        double length = Math.sqrt(dx*dx + dy*dy);
        
        // Normalize
        dx = dx / length;
        dy = dy / length;
        
        // Perpendicular vector
        double perpX = -dy;
        double perpY = dx;
        
        // Choose a random direction (left or right of the obstacle)
        if (Math.random() > 0.5) {
            perpX = -perpX;
            perpY = -perpY;
        }
        
        // Create a detour point
        double detourDistance = this.w * 2;
        return new Point2D.Double(
            this.global_x + perpX * detourDistance + dx * detourDistance,
            this.global_y + perpY * detourDistance + dy * detourDistance
        );
    }
    
    private void moveRandomly() {
        // Move in a random direction
        double angle = Math.random() * 2 * Math.PI;
        double dx = Math.cos(angle) * speed;
        double dy = Math.sin(angle) * speed;
        
        // Update position
        this.global_x += dx;
        this.global_y += dy;
        this.x = this.global_x;
        this.y = this.global_y;
        
        // Keep within boundaries
        if (this.global_x < 0) this.global_x = 0;
        if (this.global_y < 0) this.global_y = 0;
        if (this.global_x > baseplate.w - this.w) this.global_x = baseplate.w - this.w;
        if (this.global_y > baseplate.h - this.h) this.global_y = baseplate.h - this.h;
    }
    
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}
