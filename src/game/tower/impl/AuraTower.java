package game.tower.impl;

import game.state.GameState;
import game.enemy.Enemy;
import game.engine.assets.Model;
import game.engine.Particle;
import game.tower.AbstractTower;
import game.tower.TowerTargetSelector;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;

public class AuraTower extends AbstractTower {

    private static final Model auraModel = Model.loadModelWith("projectile", "aura.png", 1, 1);

    // A tower that emits a powerful aura, dealing the damage to nearby enemies

    public AuraTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
        targetSelector = TowerTargetSelector.UNSUPPORTED;
    }

    @Override
    public boolean shoot() {
        boolean hasShot = false;
        for (Enemy enemies : state.getEnemies()) {
            if (distanceTo(enemies) <= getRange()) {
                doShoot(enemies);
                hasShot = true; // Set hasShot to true if at least one enemy is in range
            }
        }
        if (hasShot) {
            state.registerParticle(new Particle.Image(state, x, y,
                auraModel.withSize(getRange() * 2 + 10, getRange() * 2 + 10), Particle.Timing.EASE_OUT_QUAD, .25));
        }
        return hasShot;
    }

    public void doShoot(Enemy enemy) {
        enemy.reduceHealth(getDamage());
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
    }

    @Override
    public TowerTargetSelector[] getPossibleTargetSelectors() {
        return null;
    }
}
