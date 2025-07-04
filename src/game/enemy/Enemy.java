package game.enemy;

import com.google.gson.JsonObject;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

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
    

    //Lebenslinie
    private Rectangle healthBar;
    private double healthBarWidth = 50;
    private double healthBarHeight = 4;

    public Enemy(GameState state, double x, double y, String type) {
        super(state, x, y, 50, 50);
        this.type = type;
        if(type.equals("Standard")){
            this.config = Config.load("enemy.json");
        }
        else{
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

        //Lebenslinie
        this.healthBar = new Rectangle(healthBarWidth, healthBarHeight);
        this.healthBar.setFill(javafx.scene.paint.Color.GREEN); 

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

        updateHealthBar();
    }

    public void updateHealthBar() {
        double percentage = currentHealth / maxHealth;
        healthBar.setWidth(healthBarWidth * percentage);
        if (percentage > 0.65) {
            healthBar.setFill(javafx.scene.paint.Color.GREEN);}
        else if (percentage <= 0.65 && percentage > 0.35) {
            healthBar.setFill(javafx.scene.paint.Color.ORANGE);
        } else if (percentage <= 0.35) {
            healthBar.setFill(javafx.scene.paint.Color.RED);
        }
    }

    @Override
    public void render(GraphicsContext graphics) {
        model.render(graphics, x, y);
        graphics.setFill(healthBar.getFill());
        graphics.fillRect(x-27, y +30, healthBar.getWidth(), healthBar.getHeight());
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
        currentHealth = currentHealth - damage;
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
