package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AuraTower extends AbstractTower {

    // A tower that emits a powerful aura, dealing the damage to nearby enemies

    public AuraTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    double enemiesX;
    double enemiesY;

    @Override
    public boolean shoot() {
        boolean anyHit = false;
        for (Enemy enemies : state.getEnemies()) {
            this.enemiesX = enemies.getX();
            this.enemiesY = enemies.getY();
            double abstand = Math.sqrt((getX() - enemiesX) * (getX() - enemiesX)
                    + (getY() - enemiesY) * (getY() - enemiesY));

            if (abstand < getRange()) {
                enemies.reduceHealth(getDamage());
                anyHit = true;
            }
        }
        return anyHit;
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        graphics.strokeLine(getX(), getY(), enemiesX, enemiesY);
        graphics.setStroke(Color.RED); // optional: change color
        graphics.setLineWidth(2); // optional: change line width
    }
}
