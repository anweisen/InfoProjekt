package game.enemy;

import game.engine.assets.Sound;
import game.state.GameState;
import game.engine.GameObject;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy extends GameObject {

    private final EnemyType type;

    private final int numberOfWaypoints; // Anzahl aller gespeicherter Wegpunkte
    private int waypointCounter; // abgegangene Wegpunkte

    private double currentHealth;

    private double healthAnimationFrom;
    private double healthAnimationProgress;
    private double healthBarAnimatedPercentage;
    private final double healthBarWidth = 50;
    private final double healthBarHeight = 5;
    private final double healthBarRounding = 3;

    public Enemy(GameState state, double x, double y, EnemyType type) {
        super(state, x, y, 50, 50);
        this.type = type;
        this.currentHealth = type.getHealth();
        this.numberOfWaypoints = state.getMap().getSplinePoints().size();
        this.waypointCounter = 0;
    }

    @Override
    public void update(double deltaTime) {
       //Ende
        if (waypointCounter >= numberOfWaypoints) { 
            this.markForRemoval(); // Gegner stirbt
            state.loseLife(); // Leben abziehen vom Spieler
            return;
        }

        // Koordinaten des nächsten Wegpunkts
        double nextWaypointX = state.getMap().getSplinePoints().get(waypointCounter).x();
        double nextWaypointY = state.getMap().getSplinePoints().get(waypointCounter).y();

        // Winkel
        double angle = Math.atan2(nextWaypointY - y, nextWaypointX - x); // Winkel zum nächsten Wegpunkt
       
        // Bewegungsänderung
        x = x + type.getSpeed() * deltaTime * Math.cos(angle); // x-Koordinate
        y = y + type.getSpeed() * deltaTime * Math.sin(angle); // y-Koordinate

        // nächster Wegpunkt erreicht
        if(distanceTo(nextWaypointX, nextWaypointY) <= type.getSpeed() * deltaTime) {
            waypointCounter++;
        }

        updateHealthBar(deltaTime);
    }

    public void updateHealthBar(double deltaTime) {
        healthAnimationProgress += 5 * deltaTime;
        if (healthAnimationProgress > 1) healthAnimationProgress = 1;
        healthBarAnimatedPercentage = (currentHealth + (healthAnimationFrom - currentHealth) * (1-healthAnimationProgress)) / type.getHealth();
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
        type.getModel().render(graphics, x, y);

        double scaleProgress = healthAnimationProgress < 0.5 ? healthAnimationProgress * 2 : (1 - healthAnimationProgress) * 2;
        double scale = 1 + scaleProgress * 0.2;
        double scaledHealthBarWidth = healthBarWidth * scale;
        double scaledHealthBarHeight = healthBarHeight * scale;
        graphics.setFill(Color.DARKSLATEGRAY);
        graphics.fillRoundRect(x - scaledHealthBarWidth / 2, y + getHeight() / 2, scaledHealthBarWidth, scaledHealthBarHeight, healthBarRounding, healthBarRounding);
        graphics.setFill(getHealthColor(currentHealth / type.getHealth()));
        graphics.fillRoundRect(x - scaledHealthBarWidth / 2, y + getHeight() / 2, scaledHealthBarWidth * healthBarAnimatedPercentage, scaledHealthBarHeight, healthBarRounding, healthBarRounding);
    }

    public EnemyType getType() {
        return type;
    }

    public double getHealth() {
        return currentHealth;
    }

    public void setWaypointCounter(int waypointCounter) {
        this.waypointCounter = waypointCounter;
    }

    public int getWaypointCounter() {
        return waypointCounter;
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
        if(type.getType()!=0) {
            Enemy neu = new Enemy(state, this.x, this.y, state.getGame().getEnemyTypes().get(type.getType()-1));
            neu.setWaypointCounter(this.waypointCounter);
            state.getEnemies().add(neu);
            Sound.MECHANICAL.playSound();
        } else { 
            state.addMoney(type.getReward());
            markForRemoval();
            Sound.MECHANICAL.playSound();
        }
           
    } 
}
