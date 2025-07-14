package game.tower.impl;

import game.state.GameState;
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

    private Enemy targetEnemy;
    private int intensity = 1;
    private double cooloff = 0;

    @Override
    public boolean shoot() {
        if (isInvalidTarget(targetEnemy)) {
            targetEnemy = targetSelector.findTarget(state, this); // Find a new target if the current one is invalid
            intensity = 1;
        }

        if (targetEnemy == null) return false;
        doShoot();
        return true; // Successfully shot at the target enemy
    }

    public void doShoot() {
        targetEnemy.reduceHealth(getDamage() * intensity);
        intensity++;
        cooloff = 1;
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
        if (targetEnemy != null && distanceTo(targetEnemy) < getRange()) {
            graphics.setStroke(Color.ORANGE.deriveColor(1, 1, 1, cooloff)); // optional: change color
            graphics.setLineWidth(intensity * 2); // optional: change line width
            graphics.strokeLine(getX(), getY(), targetEnemy.getX(), targetEnemy.getY());

            graphics.setStroke(Color.RED.deriveColor(1, 1, 1, cooloff)); // optional: change color
            graphics.setLineWidth(intensity); // optional: change line width
            graphics.strokeLine(getX(), getY(), targetEnemy.getX(), targetEnemy.getY());
        }
    }
}
