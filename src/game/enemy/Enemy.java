package game.enemy;

import com.google.gson.JsonObject;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import javafx.scene.canvas.GraphicsContext;

// Diskussion: Sollte/Muss Enemy überhaupt abstract sein? Welche Logik würde sich je Typ ändern? => Nur andere Attributswerte?
public class Enemy extends GameObject {

    // provisorisches Model zu Testzwecken (dass überhaupt etwas gezeigt wird), später: auch Config für verschiedene Gegner-Typen?
    private static final Model model = Model.loadModelWith("enemy", "robo.png", 64, 64);

    // -> Attribute wie Gegner-Typ (Geschwindigkeit, Strafe, Belohnung ...) -> über eine enemy.json?

    public Enemy(GameState state, double x, double y) {
        super(state, x, y, 50, 50);
    }

    @Override
    public void update(double deltaTime) {
    }

    // public Map.Waypoint getNextWaypoint() {
    // }

    @Override
    public void render(GraphicsContext graphics) {
        model.render(graphics, x, y);
    }

    public static final class Config {

        private final String name;
        private final double speed; // Geschwindigkeit, mit der sich der Gegner bewegt
        private final int damage; // Schaden am Ende, wenn der Gegner nicht getötet wird
        private final int reward; // Belohnung, wenn der Gegner getötet wird
        private final int health; // Leben des Gegners

        public Config(String name, double speed, int damage, int reward, int health) {
            this.name = name;
            this.speed = speed;
            this.damage = damage;
            this.reward = reward;
            this.health = health;
        }

        public static Config load(String filename) {
            JsonObject json = Model.loadJson("enemy", filename, JsonObject.class);

            return new Config(
                json.get("name").getAsString(),
                json.get("speed").getAsDouble(),
                json.get("damage").getAsInt(),
                json.get("reward").getAsInt(),
                json.get("health").getAsInt());
        }

        public String getName() {
            return name;
        }

        public double getSpeed() {
            return speed;
        }

        public int getDamage() {
            return damage;
        }

        public int getReward() {
            return reward;
        }

        public int getHealth() {
            return health;
        }
    }
}
