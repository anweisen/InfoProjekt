package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class LaserTower extends AbstractTower {

    // A tower that shoots a fast laser projectile, dealing damage over time to
    // enemies

    public LaserTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    double enemiesX;
    double enemiesY;
    double enemiesdistance;

    @Override
    public boolean shoot() {
        for (Enemy enemies : state.getEnemies()) {
            enemiesX = enemies.getX();
            enemiesY = enemies.getY();
            enemiesdistance = Math
                    .sqrt((getX() - enemiesX) * (getX() - enemiesX) + (getY() - enemiesY) * (getY() - enemiesY));

            if (enemiesdistance < getRange()) {
                enemies.reduceHealth(getDamage());
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        if (enemiesdistance < getRange() && shoot()) {
            graphics.setStroke(Color.RED); // optional: change color
            graphics.setLineWidth(2); // optional: change line width
            graphics.strokeLine(getX(), getY(), enemiesX, enemiesY);
        }
    }

}
