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
    int cooloff = 0;

    public boolean shoot() {
        for (Enemy enemy : state.getEnemies()) {
            if (distanceTo(enemy) <= getRange()) {
                doShoot(enemy);
                state.playSound("pew.wav", 0.6f);
                return true;
            }
        }
        return false;
    }

    public boolean doShoot(Enemy enemy) {
        if (distanceTo(enemy) < getRange()) {
            enemy.reduceHealth(getDamage() * damageBoost);
            System.out.println(damageBoost);
            hasShot = true;
            targetEnemy = enemy;
            cooloff = 1;
            return true;
        }
        hasShot = false;
        return false;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (targetEnemy != null && targetEnemy.isMarkedForRemoval()) {
            cooloff -= 5 * deltaTime;
            if (cooloff < 0)
                cooloff = 0;
        }
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        if (targetEnemy != null && distanceTo(targetEnemy) < getRange() && hasShot) {

            graphics.setStroke(Color.ORANGE.deriveColor(1, 1, 1, cooloff)); // optional: change color
            graphics.setLineWidth(4); // optional: change line width
            graphics.strokeLine(getX(), getY() - 20, targetEnemy.getX(), targetEnemy.getY()); // -20 für offset

        }
    }

}
