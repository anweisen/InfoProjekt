package game.tower.impl;

import game.GameState;
import game.tower.AbstractTower;
import game.tower.TowerType;

public class InfernoTower extends AbstractTower {

    // A tower that shoots a powerful beam of fire, dealing increasing damage over
    // time to enemies

    public InfernoTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    @Override
    public boolean shoot() {
        return false;
    }
}
