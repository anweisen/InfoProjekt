package game.enemy;

import com.google.gson.JsonObject;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;

public class Enemy extends GameObject {

    // private static final Model model = Model.loadModelWith("enemy", "robo.png", 64, 64);

    private final Config config;

    private String type;
    private Model model;
    private double speed;
    private double damage;
    private int reward;
    private double health;
    private Map myMap;
    private int waypointNumber; // Anzahl aller gespeicherter Wegpunkte
    private int waypointCounter = 0; // abgegangene Wegpunkte

    public Enemy(GameState state, double x, double y, String type) {
        super(state, x, y, 50, 50);
        this.type = type;
        this.config = Config.load("enemy.json");
        this.model = config.getModel();
        this.speed = config.getSpeed();
        this.damage = config.getDamage();
        this.reward = config.getReward();
        this.health = config.getHealth();
        this.myMap = state.getMap();
        this.waypointNumber = myMap.getWaypoints().length - 1;
    }

   //Was ist mit Klasse Waypoint?

    @Override
    public void update(double deltaTime) {
       //Ende
        if (waypointCounter == waypointNumber) { 
            die(); // Gegner stirbt
            return;
        }

        //Koordinaten des nächsten Wegpunkts
        double nextWaypointX = myMap.getWaypointSafely(waypointCounter).x(); //bekommt man so die einzelnen koordinaten?
        double nextWaypointY = myMap.getWaypointSafely(waypointCounter).y();

        //Winkel
        double angle = calculateRadiansFor(x-nextWaypointX, y-nextWaypointY);
       
        //Bewegungsänderung
        x =  x +  speed * deltaTime * Math.cos(angle); // x-Koordinate
        y = y + speed * deltaTime * Math.sin(angle); // y-Koordinate

       
        //nächster Wegpunkt erreicht
        if(distanceTo(nextWaypointX, nextWaypointY)<= speed * deltaTime) {
            waypointCounter++;
        }
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

    public double getDamage() {
        return damage;
    }

    public int getReward() {
        return reward;
    }

    public double getHealth() {
        return health;
    }

    public void reduceHealth(double damage) {
        health = health - damage;
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
        private final double damage; // Schaden am Ende, wenn der Gegner nicht getötet wird
        private final int reward; // Belohnung, wenn der Gegner getötet wird
        private final double health; // Leben des Gegners
    

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
}
