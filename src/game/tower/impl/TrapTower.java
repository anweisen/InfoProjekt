package game.tower.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.state.GameState;
import game.enemy.Enemy;
import game.engine.GameObject;
import game.engine.assets.Model;
import game.map.Map;
import game.tower.AbstractTower;
import game.tower.TowerTargetSelector;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;

public class TrapTower extends AbstractTower {

    // A spike thrower that sets a trap that slows down or damages enemies
    private final List<Map.Waypoint> possible = new ArrayList<>();
    private final Random random = new Random();

    public TrapTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
        targetSelector = TowerTargetSelector.UNSUPPORTED;
        calculatePossible();
    }

    private void calculatePossible() {
        for (Map.Waypoint waypoint : state.getMap().getSplinePoints()) {
            if (distanceTo(waypoint.x(), waypoint.y()) <= getRange()) {
                possible.add(waypoint);
            }
        }
    }

    @Override
    public void upgradeLevel() {
        super.upgradeLevel();
        calculatePossible();
    }

    @Override
    public boolean shoot() {
        if (possible.isEmpty()) return true;
        Map.Waypoint spawn = possible.get(random.nextInt(possible.size()));
        doShoot(spawn);
        return true;
    }

    public void doShoot(Map.Waypoint point) {
        state.registerProjectile(new Projectile(state, point.x(), point.y()));
    }

    @Override
    public TowerTargetSelector[] getPossibleTargetSelectors() {
        return null;
    }

    public class Projectile extends GameObject {
        private static final Model projectileModel = Model.loadModelWith("projectile", "kugel.png", 32, 32);

        public Projectile(GameState state, double x, double y) {
            super(state, x, y, projectileModel.getWidth(), projectileModel.getHeight());
        }

        double range = 50;

        @Override
        public void update(double deltaTime) {
            for (Enemy enemy : state.getEnemies()) {
                if (distanceTo(enemy) <= range) {
                    enemy.reduceHealth(getDamage());
                    this.markForRemoval();
                    return; // Nur ein Gegner
                }
            }
        }

        @Override
        public void render(GraphicsContext graphics) {
            projectileModel.render(graphics, x, y);
        }
    }
}
