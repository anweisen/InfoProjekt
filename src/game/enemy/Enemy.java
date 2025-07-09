package game.enemy;

import com.google.gson.JsonObject;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy extends GameObject {

    // private static final Model model = Model.loadModelWith("enemy", "robo.png", 64, 64);

    private final Config config;

    private String type;
    private Model model;
    private double speed;
    private double damage;
    private int reward;
    private Map myMap;
    private int numberOfWaypoints; // Anzahl aller gespeicherter Wegpunkte
    private int waypointCounter = 0; // abgegangene Wegpunkte

    private double maxHealth; //Macht noch keinen Sinn --> werde ich ausbessern
    private double currentHealth;

    private double healthAnimationFrom;
    private double healthAnimationProgress;
    private double healthBarAnimatedPercentage;
    private final double healthBarWidth = 50;
    private final double healthBarHeight = 4;

    public Enemy(GameState state, double x, double y, String type) {
        super(state, x, y, 50, 50);
        this.type = type;
        if (type.equals("Standard")) {
            this.config = Config.load("enemy.json");
        } else {
            this.config = Config.load("enemy1.json");
        }

        this.model = config.getModel();
        this.speed = config.getSpeed();
        this.damage = config.getDamage();
        this.reward = config.getReward();
        this.currentHealth = config.getMaxHealth();
        this.maxHealth = config.getMaxHealth();
        this.myMap = state.getMap();
        this.numberOfWaypoints = myMap.getSplinePoints().size();
    }

    @Override
    public void update(double deltaTime) {
       //Ende
        if (waypointCounter >= numberOfWaypoints) { 
            markForRemoval(); // Gegner stirbt
            state.getShop().getHud().loseLife(); //Leben abziehen vom Spieler
            return;
        }

        //Koordinaten des nächsten Wegpunkts
        double nextWaypointX = myMap.getSplinePoints().get(waypointCounter).x(); 
        double nextWaypointY = myMap.getSplinePoints().get(waypointCounter).y();

        //Winkel
        double angle = Math.atan2(nextWaypointY - y, nextWaypointX - x); // Winkel zum nächsten Wegpunkt
       
        //Bewegungsänderung
        x =  x +  speed * deltaTime * Math.cos(angle); // x-Koordinate
        y = y + speed * deltaTime * Math.sin(angle); // y-Koordinate

        //nächster Wegpunkt erreicht
        if(distanceTo(nextWaypointX, nextWaypointY)<= speed * deltaTime) {
            waypointCounter++;
        }

        updateHealthBar(deltaTime);
    }

    public void updateHealthBar(double deltaTime) {
        healthAnimationProgress += 5 * deltaTime;
        if (healthAnimationProgress > 1) healthAnimationProgress = 1;
        healthBarAnimatedPercentage = (currentHealth + (healthAnimationFrom - currentHealth) * (1-healthAnimationProgress)) / maxHealth;
    }

    private Color getHealthColor(double percentage) {
        if (percentage > 0.65) {
            return Color.GREEN;
        } else if (percentage > 0.35) {
            return Color.ORANGE;
        } else {
            return Color.RED;
        }
    }

    @Override
    public void render(GraphicsContext graphics) {
        model.render(graphics, x, y);

        double scaleProgress = healthAnimationProgress < 0.5 ? healthAnimationProgress * 2 : (1 - healthAnimationProgress) * 2;
        double scale = 1 + scaleProgress * 0.2;
        double scaledHealthBarWidth = healthBarWidth * scale;
        double scaledHealthBarHeight = healthBarHeight * scale;
        graphics.setFill(Color.DARKSLATEGRAY);
        graphics.fillRect(x - scaledHealthBarWidth / 2, y + getHeight() / 2, scaledHealthBarWidth, scaledHealthBarHeight);
        graphics.setFill(getHealthColor(currentHealth / maxHealth));
        graphics.fillRect(x - scaledHealthBarWidth / 2, y + getHeight() / 2, scaledHealthBarWidth * healthBarAnimatedPercentage, scaledHealthBarHeight);
    }

    public String getType() {
        return type;
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
        return currentHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void reduceHealth(double damage) {
        healthAnimationFrom = currentHealth;
        healthAnimationProgress = 0;
        currentHealth -= damage;
        if (currentHealth <= 0) {
            die();
        }
    }

    private void die() {
        state.getShop().addMoney(getReward());
        markForRemoval();
    }


    public static final class Config {

        private final String type; // Gegnertyp

        private final Model model; // Grafik Model

        private final double speed; // Geschwindigkeit, mit der sich der Gegner bewegt
        private final double damage; // Schaden am Ende, wenn der Gegner nicht getötet wird
        private final int reward; // Belohnung, wenn der Gegner getötet wird
        private final double currentHealth; // Leben des Gegners, momentan
        private final double maxHealth; // Maximales Leben des Gegners
    

        public Config(String type, Model model, double speed, int damage, int reward, double health, double maxhealth) {
            this.type = type;
            this.model = model;
            this.speed = speed;
            this.damage = damage;
            this.reward = reward;
            this.currentHealth = health;
            this.maxHealth = maxhealth;
            
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
                json.get("health").getAsDouble(),
                json.get("maxHealth").getAsDouble());
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

        public double getDamage() {
            return damage;
        }

        public int getReward() {
            return reward;
        }

        public double getHealth() {
            return currentHealth;
        }

        public double getMaxHealth() {
            return maxHealth;
        }
    }
}
