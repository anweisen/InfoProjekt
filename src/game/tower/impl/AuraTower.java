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
    double enemiesdistance;

    @Override
    public boolean shoot() {
        boolean anyHit = false;
        for (Enemy enemies : state.getEnemies()) {
            enemiesX = enemies.getX();
            enemiesY = enemies.getY();
            enemiesdistance = Math
                    .sqrt((getX() - enemiesX) * (getX() - enemiesX) + (getY() - enemiesY) * (getY() - enemiesY));

            if (enemiesdistance < getRange()) {
                enemies.reduceHealth(getDamage());
                anyHit = true;
            }
        }
        return anyHit;
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        if (enemiesdistance < getRange() && shoot()) {
            graphics.strokeLine(getX(), getY(), enemiesX, enemiesY);
            graphics.setStroke(Color.GREY); // optional: change color
            graphics.setLineWidth(2); // optional: change line width
        }
        graphics.setFill(Color.rgb(128, 128, 128, 0.5)); // semi-transparent red
        graphics.fillOval(getX() - getRange(), getY() - getRange(), 2 * getRange(), 2 * getRange()); // Draw aura effect
    }
}
