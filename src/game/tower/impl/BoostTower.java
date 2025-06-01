package game.tower.impl;

import game.GameState;
import game.tower.AbstractTower;
import game.tower.TowerType;

public class BoostTower extends AbstractTower {

    // A tower that boosts the damage of all placed towers

    public BoostTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    @Override
    public boolean shoot() {
        return false;
    }

}
