package game.tower.impl;

import game.GameState;
import game.enemy.Enemy;
import game.tower.AbstractTower;
import game.tower.TowerType;
import game.tower.projectile.TargetProjectile;
import game.tower.projectile.VectorProjectile;
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

                double dx = targetX - x;
                double dy = targetY - y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance < 1) continue;

                double nx = dx / distance; // Normalisierte Richtung
                double ny = dy / distance;

              double radians = calculateRadians(dx, dy);
              state.registerProjectile(new VectorProjectile(state,
                x + calculateRotatedOffsetX(config.getProjectileOffsetX(), config.getProjectileOffsetY(), radians),
                y + calculateRotatedOffsetY(config.getProjectileOffsetX(), config.getProjectileOffsetY(), radians),
                nx, ny));
//                state.registerProjectile(new TargetProjectile(state, x, y, targetX, targetY));
              return true; // shot fired
            }
        }

        return false;
    }

    @Override
    public void render(GraphicsContext graphics) {
        getModel().renderRotated(graphics, x, y, angle);
//        graphics.save();
//        graphics.translate(x, y);
//        graphics.rotate(angle);
//        graphics.drawImage(getModel().getImage(), -width / 2, -height / 2, width, height);
//        graphics.setFill(Color.RED);
//        graphics.fillOval(config.getProjectileOffsetX() - 5, config.getProjectileOffsetY() - 5, 10, 10);
//        graphics.restore();


//      for (Enemy enemy : state.getEnemies()) {
//        if (this.distanceTo(enemy) <= this.getRange()) {
//          double targetX = enemy.getX();
//          double targetY = enemy.getY();
//
//          double dx = targetX - x;
//          double dy = targetY - y;
//          double distance = Math.sqrt(dx * dx + dy * dy);
//          if (distance < 1) continue;
//
//          double nx = dx / distance; // Normalisierte Richtung
//          double ny = dy / distance;
//
//          double radians = Math.atan2(dx, -dy); // Quelle: Copilot
////          double radians = Math.toRadians(angle);
//
//          graphics.setFill(Color.BLUE);
//          graphics.fillOval(
//            Math.cos(radians) * config.getProjectileOffsetX() - Math.sin(radians) * config.getProjectileOffsetY() + x - 5,
//            Math.sin(radians) * config.getProjectileOffsetX() + Math.cos(radians) * config.getProjectileOffsetY() + y - 5,
//            10, 10);
//
//          return; // shot fired
//        }
//      }

//        // Zeichne eine Linie zum Gegner zu Testzwecken
//        for (Enemy enemy : state.getEnemies()) {
//            if (this.distanceTo(enemy) <= this.getRange()) {
//                graphics.setStroke(Color.RED);
//                graphics.setLineWidth(2);
//                graphics.strokeLine(x, y, enemy.getX(), enemy.getY());
//                break;
//            }
//        }
    }

}
