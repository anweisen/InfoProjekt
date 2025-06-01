package game.enemy;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;

// Diskussion: Sollte/Muss Enemy überhaupt abstract sein? Welche Logik würde sich je Typ ändern? => Nur andere Attributswerte?
public class Enemy extends GameObject {

    // provisorisches Model zu Testzwecken (dass überhaupt etwas gezeigt wird), später: auch Config für verschiedene Gegner-Typen!
    private static final Model model = Model.loadModelWith("enemy", "robo.png", 64, 64);

    // -> Attribute wie Gegner-Typ (Geschwindigkeit, Strafe, Belohnung ...) -> über eine enemy.json?

    protected int health;
    protected int waypoint;

    public Enemy(GameState state, double x, double y) {
        super(state, x, y, 50, 50);
    }

    @Override
    public void update(double deltaTime) {
        // provisorische (lineare) Bewegungslogik zum Testen der Tower!
        // Bitte komplett entfernen/ersetzen! => z.B: "Spline"

        Map.Waypoint next = getNextWaypoint();
        double dx = next.x() - x;
        double dy = next.y() - y;

        double distance = Math.sqrt(dx * dx + dy * dy); // Pythagoras
        if (distance < 1) {
            waypoint++;
            if (waypoint > state.getMap().getWaypoints().length) {
                markForRemoval();
                // Hier muss noch mehr passieren: Spieler-Lebensabzug, ...
                return;
            }
        }

        double speed = 100; // "Pixel pro Sekunde"
        x += (dx / distance) * speed * deltaTime;
        y += (dy / distance) * speed * deltaTime;
    }

    public void removeHealth(int damage) {
        health -= damage;
        if (health <= 0) {
            die();
        }
    }

    public void die() {
        this.markForRemoval();
    }

    public Map.Waypoint getNextWaypoint() {
        if (waypoint >= state.getMap().getWaypoints().length) {
            return state.getMap().getEnd();
        }
        return state.getMap().getWaypoints()[waypoint];
    }

    @Override
    public void render(GraphicsContext graphics) {
        model.render(graphics, x, y);
    }
}
