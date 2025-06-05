package game.enemy;

import com.google.gson.JsonObject;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import javafx.scene.canvas.GraphicsContext;

public class Enemy extends GameObject {

    // private static final Model model = Model.loadModelWith("enemy", "robo.png", 64, 64);

    private final Config config;

    private String type;
    private Model model;
    private double speed;
    private int damage;
    private int reward;
    private int health;

    public Enemy(GameState state, double x, double y, String type) {
        super(state, x, y, 50, 50);
        this.type = type;
        this.config = Config.load("enemy.json");
        this.model = config.getModel();
        this.speed = config.getSpeed();
        this.damage = config.getDamage();
        this.reward = config.getReward();
        this.health = config.getHealth();
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

    public String getType() {
        return type;
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

    public void reduceHealth(int damage) {
        health = health -= damage;
        if (health <= 0) {
            die();
        }
    }

    private void die() {
        markForRemoval();
    }


    public static final class Config {

        private final String type; // Gegnertyp

        private final Model model; // Grafik Model

        private final double speed; // Geschwindigkeit, mit der sich der Gegner bewegt
        private final int damage; // Schaden am Ende, wenn der Gegner nicht getötet wird
        private final int reward; // Belohnung, wenn der Gegner getötet wird
        private final int health; // Leben des Gegners

        public Config(String type, Model model, double speed, int damage, int reward, int health) {
            this.type = type;
            this.model = model;
            this.speed = speed;
            this.damage = damage;
            this.reward = reward;
            this.health = health;
        }

        public static Config load(String filename) {
            JsonObject json = Model.loadJson("enemy", filename, JsonObject.class);
            JsonObject modelJson = json.getAsJsonObject("model");
            Model model = Model.loadModelFrom("enemy", modelJson);

            return new Config(
                json.get("type").getAsString(),
                model,
                json.get("speed").getAsDouble(),
                json.get("damage").getAsInt(),
                json.get("reward").getAsInt(),
                json.get("health").getAsInt());
        }

        public String getType() {
            return type;
        }

        public Model getModel() {
            return model;
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
