package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class InfernoTower extends AbstractTower {

    // A tower that shoots a powerful beam of fire, dealing increasing damage over
    // time to enemies

    public InfernoTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    Enemy targetEnemy;
    Enemy lastEnemy = null;
    int intensity = 1;

    @Override
    public boolean shoot() {

        double closestDistance = Double.MAX_VALUE;
        Enemy closestEnemy = null;

        for (Enemy enemies : state.getEnemies()) {
            if (enemies != null) {
                if (distanceTo(enemies) < getRange() && distanceTo(enemies) < closestDistance
                        && enemies.getHealth() > 0) {
                    closestDistance = distanceTo(enemies);
                    closestEnemy = enemies;
                }
            }
        }
        doShoot(closestEnemy); // Return the closest enemy within range
        if (closestEnemy == null) {
            return false; // No enemy in range
        }
        return true; // Successfully shot at the target enemy
    }

    public boolean doShoot(Enemy enemy) { // Update the target enemy before shooting
        if (targetEnemy == null || targetEnemy.getHealth() <= 0) {
            targetEnemy = getTargetEnemy();
            if (targetEnemy == null) {
                return false; // No enemy in range
            }
        }

        if (lastEnemy != targetEnemy) {
            intensity = 1;
        }

        if (distanceTo(targetEnemy) < getRange()) {
            targetEnemy.reduceHealth(getDamage() * intensity);
            intensity++;
            lastEnemy = targetEnemy;
            return true;
        }
        return false; // Target enemy is out of range
    }

    public Enemy getTargetEnemy() {
        double closestDistance = Double.MAX_VALUE;
        Enemy closestEnemy = null;

        for (Enemy enemies : state.getEnemies()) {
            if (enemies != null) {
                if (distanceTo(enemies) < getRange() && distanceTo(enemies) < closestDistance
                        && enemies.getHealth() > 0) {
                    closestDistance = distanceTo(enemies);
                    closestEnemy = enemies;
                }
            }
        }
        return closestEnemy; // Return the closest enemy within range
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        if (distanceTo(targetEnemy) < getRange() && targetEnemy != null) {
            graphics.setStroke(Color.ORANGE); // optional: change color
            graphics.setLineWidth(intensity * 2); // optional: change line width
            graphics.strokeLine(getX(), getY(), targetEnemy.getX(), targetEnemy.getY());

            graphics.setStroke(Color.RED); // optional: change color
            graphics.setLineWidth(intensity); // optional: change line width
            graphics.strokeLine(getX(), getY(), targetEnemy.getX(), targetEnemy.getY());

        }
    }
}
