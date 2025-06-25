package game.tower.impl;

import java.util.ArrayList;

import game.GameState;
import game.enemy.Enemy;
import game.engine.Model;
import game.engine.Particle;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AuraTower extends AbstractTower {

    private static final Model auraModel = Model.loadModelWith("projectile", "aura.png", 1, 1);

    // A tower that emits a powerful aura, dealing the damage to nearby enemies

    public AuraTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    boolean hasShot;
    ArrayList<Enemy> currenttargets = new ArrayList<>();

    @Override
    public boolean shoot() {
        hasShot = false;
        for (Enemy enemies : state.getEnemies()) {
            if (distanceTo(enemies) < getRange()) {
                doShoot(enemies);
                hasShot = true; // Set hasShot to true if at least one enemy is in range
            }
        }
        if (hasShot) {
            state.registerParticle(new Particle.Image(state, x, y,
                    auraModel.withSize(getRange() * 2 + 10, getRange() * 2 + 10), Particle.Timing.EASE_OUT_QUAD, .25));
        }
        // Remove enemy if it goes out of range or is dead
        currenttargets.removeIf(target -> distanceTo(target) >= getRange() || target.isMarkedForRemoval()); // woher_target?
        return hasShot;
    }

    public void doShoot(Enemy enemy) {
        if (distanceTo(enemy) < getRange()) {
            enemy.reduceHealth(getDamage());
            currenttargets.add(enemy);
        }
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        for (Enemy enemies : currenttargets) {
            if (distanceTo(enemies) < getRange()) {
                graphics.setStroke(Color.GREEN); // optional: change color
                graphics.setLineWidth(2); // optional: change line width
                graphics.strokeLine(getX(), getY(), enemies.getX(), enemies.getY());
            }
        }
    }
}
