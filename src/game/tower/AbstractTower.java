package game.tower;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import javafx.scene.canvas.GraphicsContext;

public abstract class AbstractTower extends GameObject {

    protected final TowerType.Config config;

    protected double nextShotCounter;

    // Level des Turms und Upgrade Baums des Turms (1 oder 2)
    protected int level;
    protected boolean upgradeTreeOne; // könnte auch ein Enum oder int sein -> mehr Upgrade-Bäume

    /**
     * Diesen Constructor sollte jede Tower-Klasse in dieser Form haben
     * @see TowerType.TowerConstructor#newTower(GameState, TowerType.Config, double, double)
     * @see TowerType#create(GameState, double, double)
     */
    public AbstractTower(GameState state, TowerType.Config config, double x, double y) {
        super(state, x, y, config.getBaseModel().getWidth(), config.getBaseModel().getHeight());
        this.config = config;
    }

    // Beim Überschreiben super.update(deltaTime) aufrufen! (außer shoot() wird nicht genutzt)
    @Override
    public void update(double deltaTime) {
        nextShotCounter += deltaTime;

        if (nextShotCounter >= getShootInterval()) {
            if (shoot()) {
                nextShotCounter = 0;
            }
        }
    }

    // Beim Überschreiben NICHT super.render(graphics) aufrufen!
    @Override
    public void render(GraphicsContext graphics) {
        getModel().render(graphics, x, y);
    }

    public abstract boolean shoot();

    public int getRange() {
        return config.getRangeAtLevel(level, upgradeTreeOne);
    }

    public int getDamage() {
        return config.getDamageAtLevel(level, upgradeTreeOne);
    }

    public int getTargets() {
        return config.getTargetsAtLevel(level, upgradeTreeOne);
    }

    public double getShootSpeed() {
        return config.getSpeedAtLevel(level, upgradeTreeOne);
    }

    public double getShootInterval() {
        return 1.0 / getShootSpeed();
    }

    public TowerType.Config getConfig() {
        return config;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean getUpgradeTree() {
        return upgradeTreeOne;
    }

    public void setUpgradeTree(boolean isUpgradeTreeOne) {
        this.upgradeTreeOne = isUpgradeTreeOne;
    }

    public Model getModel() {
        return config.getModelForLevel(level, upgradeTreeOne);
    }

}
