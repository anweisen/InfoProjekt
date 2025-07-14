package game.tower.impl;

import game.enemy.Enemy;
import game.engine.assets.Sound;
import game.state.GameState;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LaserTower extends AbstractTower {

    private static final Sound shootSound = Sound.loadSound("tower", "pew.wav", 0.3f);

    // A tower that shoots a fast laser projectile, dealing damage over time to
    // enemies

    public LaserTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    boolean hasShot = false;
    Enemy targetEnemy;
    double cooloff = 0;

    @Override
    public boolean shoot() {
        Enemy target = targetSelector.findTarget(state, this);
        if (target != null) {
            doShoot(target);
            return true;
        }
        hasShot = false;
        return false;
    }

    public void doShoot(Enemy enemy) {
        enemy.reduceHealth(getDamage());
        shootSound.playSound();
        hasShot = true;
        targetEnemy = enemy;
        cooloff = 1;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (targetEnemy != null && targetEnemy.isMarkedForRemoval()) {
            cooloff -= 5d * deltaTime;
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
