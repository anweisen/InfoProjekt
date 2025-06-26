package game.tower.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Game;
import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import game.map.Map;
import game.map.Map.Waypoint;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;

public class TrapTower extends AbstractTower {

    // A spike thrower that sets a trap that slows down or damages enemies
    List<Map> maps = new ArrayList<>();
    List<int[]> possible = new ArrayList<>();
    Random r = new Random();

    public TrapTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
        calculatePossible();
    }

    private void calculatePossible() {
        for (int i = 0; i < Game.VIRTUAL_WIDTH; i++) {
            for (int k = 0; k < Game.VIRTUAL_HEIGHT; k++) {
                if (state.getMap().getCanPlace()[i][k] && distanceTo(i, k) <= getRange()) {
                    possible.add(new int[] { i, k });
                }
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
        int[] Koordinate = possible.get(r.nextInt(possible.size()));
        doshoot(Koordinate[0], Koordinate[1]);
        return true;
    }

    public void doshoot(int x, int y) {
        state.registerProjectile(new Projectile(state, x, y));

    }

    public class Projectile extends GameObject {
        private static final Model projectileModel = Model.loadModelWith("projectile", "kugel.png", 32, 32);

        public Projectile(GameState state, int x, int y) {
            super(state, x, y, projectileModel.getWidth(), projectileModel.getHeight());
        }

        @Override
        public void update(double deltaTime) {

        }

        @Override
        public void render(GraphicsContext graphics) {
            projectileModel.render(graphics, x, y);
        }
    }
}
