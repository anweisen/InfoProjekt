package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import game.tower.projectile.TestProjectile;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TestProjTower extends AbstractTower {

    public TestProjTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, config, x, y);
    }

    private double angle;

    @Override
    public boolean shoot() {
        // Schießt auf den nächsten Gegner in Reichweite ein Projektil zur derzeitigen Position des Gegners
        // => Vproj >> Venemy
        // (In BTD6 scheint das auch so der Fall zu sein: Sind Projektile zu langsam, dann treffen sie nicht mehr, fliegen vorbei)
        // => Anderer Ansatz: Position vorher berechnen und dann schießen (komplizierter, (un)schöner?)

        // Außerdem: Projektil soll in Kanonenöffnung spawnen:
        // config.getProjectileOffsetX() (offset von Mitte des Models), aber muss rotation berücksichtigen!!

        for (Enemy enemy : state.getEnemies()) {
            if (this.distanceTo(enemy) <= this.getRange()) {
                double targetX = enemy.getX();
                double targetY = enemy.getY();
                angle = angleInDirection(targetX, targetY);
                state.registerProjectile(new TestProjectile(state, x, y, targetX, targetY));
                return true; // shot fired
            }
        }

        return false;
    }

    @Override
    public void render(GraphicsContext graphics) {
        getModel().renderRotated(graphics, x, y, angle);

        // Zeichne eine Linie zum Gegner zu Testzwecken
        for (Enemy enemy : state.getEnemies()) {
            if (this.distanceTo(enemy) <= this.getRange()) {
                graphics.setStroke(Color.RED);
                graphics.setLineWidth(2);
                graphics.strokeLine(x, y, enemy.getX(), enemy.getY());
                break;
            }
        }
    }

}
