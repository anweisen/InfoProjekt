package game.tower.impl;

import java.util.ArrayList;

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

    boolean hasShot;
    ArrayList<Enemy> currenttargets = new ArrayList<>();

    @Override
    public boolean shoot() {
        hasShot = false;
        for (Enemy enemies : state.getEnemies()) {
            if (distanceTo(enemies) < getRange()) {
                enemies.reduceHealth(getDamage());
                currenttargets.add(enemies);
                hasShot = true;
            }
            if (distanceTo(enemies) >= getRange() && currenttargets.contains(enemies)) {
                currenttargets.remove(enemies); // Remove enemy if it goes out of range
            }
        }
        return hasShot;
    }

    @Override
    public void render(GraphicsContext graphics) {
        super.render(graphics);
        for (Enemy enemies : currenttargets) {
            if (distanceTo(enemies) < getRange()) {
                graphics.strokeLine(getX(), getY(), enemies.getX(), enemies.getY());
                graphics.setStroke(Color.GREY); // optional: change color
                graphics.setLineWidth(2); // optional: change line width
            }
        }
        graphics.setFill(Color.rgb(128, 128, 128, 0.2)); // semi-transparent grey for aura effect
        graphics.fillOval(getX() - getRange(), getY() - getRange(), 2 * getRange(), 2 * getRange()); // Draw aura effect
    }
}
