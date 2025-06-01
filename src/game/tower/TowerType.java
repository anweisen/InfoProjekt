package game.tower;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import game.GameState;
import game.engine.Model;
import java.util.function.Function;

/**
 * Ein TowerType repräsentiert eine bestimmte, registrierte Art von Turm.
 * Sie verbindet die Konfiguration aus der JSON-Datei (TowerType.Config)
 * und die jeweilige Tower-Klasse (via TowerConstructor).
 * <p>
 * Die TowerType.Config enthält Daten wie Name, Preis, Bilder, Upgrades usw.
 * (angegeben in der jeweiligen JSON-Datei in /assets/conf/tower).
 * <p>
 * Jeder platzierte Turm ist eine Instanz von AbstractTower bzw. der jeweiligen Tower-Klasse.
 * Diese enthält Daten wie Position und Level dieses platzierten Turms.
 * Die jeweilige Tower-Klasse definiert dabei die Logik für den Turm wie das Schießen.
 */
public final class TowerType {

    private final Config config;
    private final TowerConstructor constructor;

    public TowerType(Config config, TowerConstructor constructor) {
        this.config = config;
        this.constructor = constructor;
    }

    public AbstractTower create(GameState state, double x, double y) {
        return constructor.newTower(state, config, x, y);
    }

    public Config getConfig() {
        return config;
    }

    public TowerConstructor getConstructor() {
        return constructor;
    }

    @FunctionalInterface
    public interface TowerConstructor {
        AbstractTower newTower(GameState state, Config config, double x, double y);
    }

    public record Upgrade(
        String name, // Name des Upgrades
        String info, // Beschreibung des Upgrades
        int price, // Preis des Upgrades
        int range, // Zusätzliche Reichweite
        int damage, // Zusätzlicher Schaden
        int targets, // Zusätzliche Anzahl an Zielen
        double speed // Zusätzliche Schussgeschwindigkeit (in Schüssen pro Sekunde, Durchschnitt->Kommazahlen möglich)
    ) {
    }

    public static final class Config {

        private final String name;
        private final String info; // Beschreibung des Turms

        private final Model baseModel; // Normales Model des Turms
        private final Model[] models1; // Models für Upgrade-Pfad 1
        private final Model[] models2; // Models für Upgrade-Pfad 2

        // Eigentlich sollte projectileOffset zum Model gehören... TowerModel einführen?
        private final int projectileOffsetX;
        private final int projectileOffsetY;

        private final int price;
        private final int baseRange;
        private final int baseDamage;
        private final int baseTargets;
        private final double baseSpeed; // Schussgeschwindigkeit in Schüssen pro Sekunde (Durchschnitt, Kommazahlen möglich)

        private final Upgrade[] upgrades1; // Upgrade-Pfad 1
        private final Upgrade[] upgrades2; // Upgrade-Pfad 2

        public Config(String name, String info, Model baseModel, Model[] models1, Model[] models2,
                      int projectileOffsetX, int projectileOffsetY,
                      int price, int baseRange, int baseDamage, int baseTargets, double baseSpeed,
                      Upgrade[] upgrades1, Upgrade[] upgrades2) {
            this.name = name;
            this.info = info;
            this.baseModel = baseModel;
            this.models1 = models1;
            this.models2 = models2;
            this.projectileOffsetX = projectileOffsetX;
            this.projectileOffsetY = projectileOffsetY;
            this.price = price;
            this.baseRange = baseRange;
            this.baseDamage = baseDamage;
            this.baseTargets = baseTargets;
            this.baseSpeed = baseSpeed;
            this.upgrades1 = upgrades1;
            this.upgrades2 = upgrades2;
        }

        public static Config load(String filename) {
            JsonObject json = Model.loadJson("tower", filename, JsonObject.class);

            JsonObject modelJson = json.getAsJsonObject("model");
            Model baseModel = Model.loadModelFrom("tower", modelJson);

            // POJO?
            return new Config(
                json.get("name").getAsString(),
                json.get("info").getAsString(),
                baseModel,
                loadModelArray(json.getAsJsonArray("models1"), baseModel),
                loadModelArray(json.getAsJsonArray("models2"), baseModel),
                modelJson.get("projectileX").getAsInt(),
                modelJson.get("projectileY").getAsInt(),
                json.get("price").getAsInt(),
                json.get("range").getAsInt(),
                json.get("damage").getAsInt(),
                json.get("targets").getAsInt(),
                json.get("speed").getAsDouble(),
                Model.GSON.fromJson(json.get("upgrades1"), Upgrade[].class),
                Model.GSON.fromJson(json.get("upgrades2"), Upgrade[].class));
        }

        private static Model[] loadModelArray(JsonArray json, Model baseModel) {
            Model[] models = new Model[5]; // TODO: = MAX_TOWER_LEVEL!
            if (json == null || json.isEmpty()) {
                return models;
            }

            for (int i = 0; i < json.size(); i++) {
                if (json.get(i) == null || json.get(i).isJsonNull()) continue;
                JsonObject obj = json.get(i).getAsJsonObject();
                if (!obj.has("width")) obj.addProperty("width", baseModel.getWidth());
                if (!obj.has("height")) obj.addProperty("height", baseModel.getHeight());
                models[i] = Model.loadModelFrom("tower", obj);
            }
            return models;
        }

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }

        public Model getBaseModel() {
            return baseModel;
        }

        public Model[] getModels1() {
            return models1;
        }

        public Model[] getModels2() {
            return models2;
        }

        public int getProjectileOffsetX() {
            return projectileOffsetX;
        }

        public int getProjectileOffsetY() {
            return projectileOffsetY;
        }

        public int getPrice() {
            return price;
        }

        public int getBaseRange() {
            return baseRange;
        }

        public int getBaseDamage() {
            return baseDamage;
        }

        public int getBaseTargets() {
            return baseTargets;
        }

        public double getBaseSpeed() {
            return baseSpeed;
        }

        public Upgrade[] getUpgrades1() {
            return upgrades1;
        }

        public Upgrade[] getUpgrades2() {
            return upgrades2;
        }

        public Model getModelForLevel(int level, boolean upgradeTreeOne) {
            if (level == 0) return baseModel;
            Model[] models = upgradeTreeOne ? models1 : models2;

            for (int i = level; i >= 0; i--) {
                if (i >= models.length) continue; // fail-safe
                if (models[i] != null) return models[i];
            }

            return baseModel;
        }

        public int getDamageAtLevel(int level, boolean upgradeTreeOne) {
            return sumUpgradedStat(level, upgradeTreeOne, baseDamage, Upgrade::damage).intValue();
        }

        public int getRangeAtLevel(int level, boolean upgradeTreeOne) {
            return sumUpgradedStat(level, upgradeTreeOne, baseRange, Upgrade::range).intValue();
        }

        public int getTargetsAtLevel(int level, boolean upgradeTreeOne) {
            return sumUpgradedStat(level, upgradeTreeOne, baseTargets, Upgrade::targets).intValue();
        }

        public double getSpeedAtLevel(int level, boolean upgradeTreeOne) {
            return sumUpgradedStat(level, upgradeTreeOne, baseSpeed, Upgrade::speed).doubleValue();
        }

        private Number sumUpgradedStat(int level, boolean upgradeTreeOne, Number baseStat, Function<Upgrade, Number> attribute) {
            Number result = baseStat;

            TowerType.Upgrade[] upgrades = upgradeTreeOne ? upgrades1 : upgrades2;
            for (int i = 0; i < level; i++) {
                result = result.doubleValue() + attribute.apply(upgrades[i]).doubleValue();
            }

            return result;
        }
    }
}
