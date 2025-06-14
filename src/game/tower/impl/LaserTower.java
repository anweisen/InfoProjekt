package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LaserTower extends AbstractTower {

    // A tower that shoots a fast laser projectile, dealing damage over time to
    // enemies

    public LaserTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    boolean hasShot = false;
    Enemy targetEnemy;

    @Override
    public boolean shoot() {
        for (Enemy enemies : state.getEnemies()) {
            if (distanceTo(enemies) < getRange()) {
                enemies.reduceHealth(getDamage());
                hasShot = true;
                targetEnemy = enemies;
                return true;
            }
        }
        hasShot = false;
        return false;
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        if (distanceTo(targetEnemy) < getRange() && hasShot && targetEnemy != null) {

            graphics.setStroke(Color.RED); // optional: change color
            graphics.setLineWidth(4); // optional: change line width
            graphics.strokeLine(getX(), getY(), targetEnemy.getX(), targetEnemy.getY());

        }
    }

}
