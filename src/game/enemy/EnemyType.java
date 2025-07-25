package game.enemy;

import com.google.gson.JsonObject;
import game.engine.assets.Assets;
import game.engine.assets.Model;

public final class EnemyType {

    private final int type;

    private final Model model; // Grafik Model

    private final double speed; // Geschwindigkeit, mit der sich der Gegner bewegt
    private final double damage; // Schaden am Ende, wenn der Gegner nicht getötet wird
    private final int reward; // Belohnung, wenn der Gegner getötet wird
    private final double health; // Maximales Leben des Gegners

    public EnemyType(int type, Model model, double speed, int damage, int reward, double health) {
        this.type = type;
        this.model = model;
        this.speed = speed;
        this.damage = damage;
        this.reward = reward;
        this.health = health;
    }

    public static EnemyType load(String filename) {
        JsonObject json = Assets.loadJson("enemy", filename, JsonObject.class);
        JsonObject modelJson = json.getAsJsonObject("model");
        Model model = Model.loadModelFrom("enemy", modelJson);

        return new EnemyType(
            json.get("type").getAsInt(),
            model,
            json.get("speed").getAsDouble(),
            json.get("damage").getAsInt(),
            json.get("reward").getAsInt(),
            json.get("health").getAsDouble()
        );
    }

    public int getType() {
        return type;
    }

    public Model getModel() {
        return model;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDamage() {
        return damage;
    }

    public int getReward() {
        return reward;
    }

    public double getHealth() {
        return health;
    }
}
