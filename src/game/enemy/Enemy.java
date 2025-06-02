package game.enemy;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import javafx.scene.canvas.GraphicsContext;

// Diskussion: Sollte/Muss Enemy überhaupt abstract sein? Welche Logik würde sich je Typ ändern? => Nur andere Attributswerte?
public class Enemy extends GameObject {

    // provisorisches Model zu Testzwecken (dass überhaupt etwas gezeigt wird), später: auch Config für verschiedene Gegner-Typen?
    private static final Model model = Model.loadModelWith("enemy", "robo.png", 64, 64);

    // -> Attribute wie Gegner-Typ (Geschwindigkeit, Strafe, Belohnung ...) -> über eine enemy.json?

    private String enemyType;
    private int enemyHealth;
    private int killReward;
    private int movementSpeed;
    private int penalty;

    public Enemy(GameState state, double x, double y) {
        super(state, x, y, 50, 50);
    }

    @Override
    public void update(double deltaTime) {
    }

    public void reduceHealth(int damage) {
        enemyHealth -= damage;
        if (enemyHealth <= 0) {
            die();
        }
    }

    public void die() {
        this.markForRemoval();
    }

    // public Map.Waypoint getNextWaypoint() {
    // }

    public int getEnemyHealth() {
        return enemyHealth;
    }

    public int getKillReward() {
        return killReward;
    }

    public int getPenalty() {
        return penalty;
    }

    @Override
    public void render(GraphicsContext graphics) {
        model.render(graphics, x, y);
    }
}
