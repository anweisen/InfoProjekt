package game.tower.impl;

import game.Game;
import game.enemy.Enemy;
import game.engine.GameObject;
import game.engine.Particle;
import game.engine.assets.Model;
import game.engine.assets.Sound;
import game.state.GameState;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;

public class CanonTower extends AbstractTower {

    private static final Sound shootSound = Sound.loadSound("tower", "pew2.wav", 0.3f);

    private double angle; // Drehung der Kanone (zum Gegner hin)

    public CanonTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    @Override
    public boolean shoot() {
        Enemy targetEnemy = targetSelector.findTarget(state, this);
        if (targetEnemy == null) return false;
        doShoot(targetEnemy);
        return true;
    }

    private void doShoot(Enemy enemy) {
        double dx = enemy.getX() - x;
        double dy = enemy.getY() - y;

        // normalisierter Richtungsvektor
        double distance = Math.sqrt(dx * dx + dy * dy);
        double nx = dx / distance;
        double ny = dy / distance;

        double radians = calculateRadiansFor(dx, dy);
        angle = Math.toDegrees(radians);

        double originX = x + calculateRotatedOffsetX(radians);
        double originY = y + calculateRotatedOffsetY(radians);

        state.registerProjectile(new Projectile(state, originX, originY, nx, ny));

        shootSound.playSound();
    }

    @Override
    public void render(GraphicsContext graphics) {
        getModel().renderRotated(graphics, x, y, angle);
    }

    public class Projectile extends GameObject {
        private static final Model projectileModel = Model.loadModelWith("projectile", "kugel.png", 32, 32);
        private static final Model explosionModel = Model.loadModelWith("projectile", "explosion.png", 100, 100);
        private static final double speed = 800;

        private final double nx, ny;

        public Projectile(GameState state, double x, double y, double nx, double ny) {
            super(state, x, y, projectileModel.getWidth(), projectileModel.getHeight());
            this.nx = nx;
            this.ny = ny;
        }

        @Override
        public void update(double deltaTime) {
            x += nx * speed * deltaTime;
            y += ny * speed * deltaTime;

            for (Enemy enemy : state.getEnemies()) {
                if (distanceTo(enemy) <= explosionModel.getWidth() / 2d) {
                    enemy.reduceHealth(CanonTower.super.getDamage());
                    this.markForRemoval();
                    state.registerParticle(new Particle.Image(state, x, y, explosionModel, Particle.Timing.LINEAR, 0.15));
                }
            }

            if (x < 0 || x > Game.VIRTUAL_WIDTH || y < 0 || y > Game.VIRTUAL_HEIGHT) {
                this.markForRemoval();
            }
        }

        @Override
        public void render(GraphicsContext graphics) {
            projectileModel.render(graphics, x, y);
        }
    }
}
