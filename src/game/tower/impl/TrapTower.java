package game.tower.impl;

import game.GameState;
import game.tower.AbstractTower;
import game.tower.TowerType;

public class TrapTower extends AbstractTower {

    // A spike thrower that sets a trap that slows down or damages enemies

    public TrapTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    @Override
    public boolean shoot() {
        return false;
    }
}
