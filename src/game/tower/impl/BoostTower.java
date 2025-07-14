package game.tower.impl;

import game.state.GameState;
import game.tower.AbstractTower;
import game.tower.TowerTargetSelector;
import game.tower.TowerType;

public class BoostTower extends AbstractTower {

    // A tower that boosts the damage of all placed towers

    public BoostTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
        targetSelector = TowerTargetSelector.UNSUPPORTED;
    }

    @Override
    public boolean shoot() {
        for (AbstractTower tower : state.getTowers()) {
            if (distanceTo(tower) < getRange()) {
                tower.setDamageBoost(getDamageRaw());// damage ist damageboost
            }
        }
        return false;
    }

    @Override
    public TowerTargetSelector[] getPossibleTargetSelectors() {
        return null;
    }
}
