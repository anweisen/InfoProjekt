package game.tower.projectile;

import game.Game;
import game.GameState;
import game.enemy.Enemy;
import game.engine.GameObject;
import javafx.scene.canvas.GraphicsContext;

import static game.tower.projectile.TargetProjectile.model;

public class VectorProjectile extends GameObject {

    private final double nx, ny; // normalized direction vector
    private final double sx, sy; // starting position

    public VectorProjectile(GameState state, double x, double y, double nx, double ny) {
        super(state, x, y, model.getWidth(), model.getHeight());
        this.nx = nx;
        this.ny = ny;
        this.sx = x;
        this.sy = y;
    }

    @Override
    public void update(double deltaTime) {
        // Update position based on normalized direction vector
        double speed = 1000; // Speed in pixels per second
        x += nx * speed * deltaTime;
        y += ny * speed * deltaTime;

        // Check if the projectile is out of bounds
        if (x < 0 || y < 0 || x > Game.VIRTUAL_WIDTH || y > Game.VIRTUAL_HEIGHT) {
            markForRemoval();
            return;
        }

        for (Enemy enemy : state.getEnemies()) {
            if (this.distanceTo(enemy) <= enemy.getWidth() / 2) {
                // Hit the enemy
                enemy.removeHealth(10); // Assuming 1 damage per hit, adjust as needed
                markForRemoval(); // Remove the projectile after hitting
                return;
            }
        }
    }

    @Override
    public void render(GraphicsContext graphics) {
        model.render(graphics, x, y);

//        graphics.setFill(javafx.scene.paint.Color.RED);
//        graphics.fillOval(sx - 5, sy - 5, 10, 10); // Starting position indicator
    }
}
