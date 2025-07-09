package game.state;

import game.Game;
import game.enemy.Enemy;
import game.engine.GameObject;
import game.engine.Particle;
import game.engine.State;
import game.engine.assets.Sound;
import game.hud.Hud;
import game.map.Map;
import game.shop.Shop;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collection;

public class GameState extends State {

    private final Map map;
    private final Shop shop;
    private final Hud hud;

    private final Collection<AbstractTower> towers = new ArrayList<>();
    private final Collection<Enemy> enemies = new ArrayList<>();
    private final Collection<GameObject> projectiles = new ArrayList<>();
    private final Collection<Particle> particles = new ArrayList<>();

    private final int startLives = 20;
    private int lives = startLives;
    private int money = 1000;

    private double seconds = 0;

    // TODO: Leben, Geld, ...
    private double spawnIntervalStandard; // für Standard-Enemy
    private double spawnInterval; // für Type1-Enemy
    private AbstractTower selectedTower;

    public GameState(Game game, Map map) {
        super(game);
        this.map = map;
        this.hud = new Hud(this);
        this.shop = new Shop(this);
    }

    // keine Runden, Zeit zb 5 Minuten zum übereben
    @Override
    public void render(GraphicsContext graphics) {
        graphics.clearRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);
        map.render(graphics);

        if (selectedTower != null) {
            graphics.setFill(Color.rgb(0, 0, 0, 0.2));
            graphics.fillOval(selectedTower.getX() - selectedTower.getRange(),
                selectedTower.getY() - selectedTower.getRange(),
                selectedTower.getRange() * 2, selectedTower.getRange() * 2);
        }

        for (AbstractTower tower : towers) {
            tower.render(graphics);
        }
        for (Enemy enemy : enemies) {
            enemy.render(graphics);
        }
        for (GameObject projectile : projectiles) {
            projectile.render(graphics);
        }
        for (Particle particle : particles) {
            particle.render(graphics);
        }

        hud.render(graphics);
        shop.render(graphics);
    }

    @Override
    public void update(double deltaTime) {
        if (isGameOver()) {
            // Spiel zu Ende-Text
            game.setState(new DeathState(game, map, (int) seconds));
            return;
        }

        seconds += deltaTime;

        shop.update(deltaTime);

        for (AbstractTower tower : towers) {
            tower.update(deltaTime);
        }
        for (Enemy enemy : enemies) {
            enemy.update(deltaTime);
        }
        for (GameObject projectile : projectiles) {
            projectile.update(deltaTime);
        }
        for (Particle particle : particles) {
            particle.update(deltaTime);
        }

        towers.removeIf(GameObject::isMarkedForRemoval);
        enemies.removeIf(GameObject::isMarkedForRemoval);
        projectiles.removeIf(GameObject::isMarkedForRemoval);
        particles.removeIf(GameObject::isMarkedForRemoval);

        spawnEnemies(deltaTime);
    }

    public void spawnEnemies(double deltaTime) {
        // endgültige Gegner-Spawning-Logik für zwei versch. Gegner typen
        spawnIntervalStandard += deltaTime; // für Standard-Enemy
        if (spawnIntervalStandard > 1.1) {
            spawnIntervalStandard = 0;
            enemies.add(new Enemy(this, map.getStart().x(), map.getStart().y(), game.getEnemyTypes().get(0)));
        }

        if (money >= 300) {
            spawnInterval += deltaTime; // für Type1-Enemy
            if (spawnInterval > 1.5) {
                spawnInterval = 0;
                enemies.add(new Enemy(this, map.getStart().x(), map.getStart().y(), game.getEnemyTypes().get(1)));
            }
        }

    }

    @Override
    public void dispose() {
    }

    @Override
    public void handleClick(double x, double y) {
        if (shop.handleClick(x, y)) {
            return;
        }

        for (AbstractTower tower : towers) {
            if (tower.containsPoint(x, y)) {
                selectedTower = tower == selectedTower ? null : tower;
                Sound.CLICK.playSound();
                return;
            }
        }

        if (selectedTower != null) {
            selectedTower = null;
            return;
        }

        shop.handlePlacementClick(x, y);
    }

    @Override
    public void handleKeyPressed(KeyEvent event) {
        // wird nicht verwendet
    }

    public boolean isGameOver() {
        return lives < 1;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public void removeMoney(int amount) {
        money -= amount;
    }

    public int getStartLives() {
        return startLives;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
    }

    public void spawnTower(TowerType type, double x, double y) {
        towers.add(type.create(this, x, y));
    }

    public void registerProjectile(GameObject projectile) {
        projectiles.add(projectile);
    }

    public void registerParticle(Particle particle) {
        particles.add(particle);
    }

    public AbstractTower getSelectedTower() {
        return selectedTower;
    }

    public void setSelectedTower(AbstractTower selectedTower) {
        this.selectedTower = selectedTower;
    }

    public Collection<AbstractTower> getTowers() {
        return towers;
    }

    public Collection<Enemy> getEnemies() {
        return enemies;
    }

    public Collection<GameObject> getProjectiles() {
        return projectiles;
    }

    public Collection<Particle> getParticles() {
        return particles;
    }

    public Map getMap() {
        return map;
    }

    public Shop getShop() {
        return shop;
    }

    public Hud getHud() {
        return hud;
    }
}
