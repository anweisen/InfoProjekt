package game.tower.impl;

import game.GameState;
import game.tower.AbstractTower;
import game.tower.TowerType;

public class AuraTower extends AbstractTower {

    // A tower that emits a powerful aura, dealing the damage to nearby enemies

    public AuraTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    @Override
    public boolean shoot() {
        return false;
    }
}
