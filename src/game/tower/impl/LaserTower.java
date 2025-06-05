package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;

public class LaserTower extends AbstractTower {

    // A tower that shoots a fast laser projectile, dealing damage over time to
    // enemies

    public LaserTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    @Override
    public boolean shoot() {
        // Implement laser shooting logic here
        return false;
    }

    @Override
    public void render(GraphicsContext graphics) {

    }

}
