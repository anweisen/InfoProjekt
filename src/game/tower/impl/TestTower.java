package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TestTower extends AbstractTower {

    private double angle = 0;
    private double targetX = -1, targetY = -1;
    private double intensity;

    public TestTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    @Override
    public boolean shoot() {
        for (Enemy enemy : state.getEnemies()) {
            if (this.distanceTo(enemy) <= this.getRange()) {
                intensity = 1;
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        intensity *= Math.exp(-7.67 * deltaTime); // Exponentielle Abkühlung (ChatGPT): 0.3s~10%

        for (Enemy enemy : state.getEnemies()) {
            if (this.distanceTo(enemy) <= this.getRange()) {
                targetX = enemy.getX();
                targetY = enemy.getY();
                angle = angleInDirection(targetX, targetY);
                return;
            }
        }
    }

    @Override
    public void render(GraphicsContext graphics) {
        getModel().renderRotated(graphics, x, y, angle);

        if (targetX != -1 && targetY != -1) {
            graphics.setStroke(Color.rgb(255, 0, 0, intensity));
            graphics.setLineWidth(6);
            graphics.strokeLine(x, y, targetX, targetY);
        }
    }
}
