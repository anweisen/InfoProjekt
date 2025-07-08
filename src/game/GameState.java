package game;

import game.enemy.Enemy;
import game.engine.GameObject;
import game.engine.Particle;
import game.engine.State;
import game.hud.Hud;
import game.map.Map;
import game.shop.Shop;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


public class GameState extends State {

    private final Map map;
    private final Shop shop;
    private final Hud hud;

    private final Collection<AbstractTower> towers = new ArrayList<>();
    private final Collection<Enemy> enemies = new ArrayList<>();
    private final Collection<GameObject> projectiles = new ArrayList<>();
    private final Collection<Particle> particles = new ArrayList<>();
    private int gegneranzahlpros = 1;
    private int spieldauer = 180;

    private boolean gameOver = false; // Spiel ist vorbei, wenn Leben = 0, also wenn Gegner bereits oft genug im Ziel
                                      // angekommen sind

 

    // TODO: Leben, Geld, ...
    private double spawnIntervalStandard; // für Standard-Enemy
    private double spawnInterval; // für Type1-Enemy
    private AbstractTower selectedTower;

    public GameState(Game game, Map map) {
        super(game);
        this.map = map;
        this.hud = new Hud(this);
        this.shop = new Shop(this);
        // playSound();
    }

    public void playSound(String file,float volume) {
        if (file == null || file.isEmpty()) {
            System.out.println("Sound Datei nicht vorhanden.");
            return;
        }
    try {
        // Hole den Sound als InputStream aus dem Ressourcenpfad
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(
            getClass().getResource("/assets/sounds/"+file)
        );
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log10(volume)*20);
        gainControl.setValue(dB);

        clip.start();
    } catch (Exception e) {
        System.out.println("Sound konnte nicht abgespielt werden: ");
        e.printStackTrace();
    }
}

//keine Runden, Zeit zb 5 Minuten zum übereben
    @Override
    public void render(GraphicsContext graphics) {
        if (gameOver() == true) {
            // Spiel zu Ende-Text
            game.setState(new DeathState(game, map));
            return;
        }

        graphics.clearRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);
        map.render(graphics);

        if (selectedTower != null) {
            graphics.setFill(Color.rgb(0, 0, 0, 0.2));
            graphics.fillOval(selectedTower.getX() - selectedTower.getRange(),
                    selectedTower.getY() - selectedTower.getRange(),
                    selectedTower.getRange() * 2, selectedTower.getRange() * 2);
            shop.renderUpgrades(graphics);
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

        if (shop.isOpen()) {
            shop.renderShop(graphics);
        }
    }

    @Override
    public void update(double deltaTime) {
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

        for (Enemy enemy : enemies) {
            if (enemy.isMarkedForRemoval()) {
                enemies.remove(enemy);
            }
        }

        if (gameOver() == true) {
            return;
        }
    }

    public void gegneranzahl(int spieldauer, int gegneranzahlpros) {
        spieldauer = spieldauer - 1;
        if (spieldauer % 30 == 0) {
            gegneranzahlpros = gegneranzahlpros + 2;
        }
        for (int i = 0; i < gegneranzahlpros; i++) {
            enemies.add(new Enemy(this, map.getStart().x(), map.getStart().y(), "Standard"));
        }
        for (int j = 0; j < gegneranzahlpros - 1; j++) {
            enemies.add(new Enemy(this, map.getStart().x(), map.getStart().y(), "Type1"));
        }
    }

    public void spawnEnemies(double deltaTime) {
        // endgültige Gegner-Spawning-Logik für zwei versch. Gegner typen
        spawnIntervalStandard += deltaTime; // für Standard-Enemy
        if (spawnIntervalStandard > 1.1) {
            spawnIntervalStandard = 0;
            enemies.add(new Enemy(this, map.getStart().x(), map.getStart().y(), "Standard"));
        }

        if (shop.getMoney() >= 300) {
            spawnInterval += deltaTime; // für Type1-Enemy
            if (spawnInterval > 1.5) {
                spawnInterval = 0;
                enemies.add(new Enemy(this, map.getStart().x(), map.getStart().y(), "Type1"));
            }
        }

    }

    @Override
    public void dispose() {
    }

    @Override
    public void handleClick(double x, double y) {
        System.out.println("GameState.handleClick:" + x + "," + y);
        for (AbstractTower tower : towers) {
            if (tower.containsPoint(x, y)) {
                selectedTower = tower == selectedTower ? null : tower;
                return;
            }
        }

        if (hud.isShopButtonClicked(x, y)) {
            shop.toggle();
            return;
        }

        shop.handleClick(x, y);
        // Erstelle Turm beim Klicken zu Testzwecken!
    }

    public boolean gameOver() {
        if (hud.getLives() <= 0) {
            gameOver = true;
        }
        return gameOver;
    }

    public void spawnTower(TowerType type, double x, double y) {
        towers.add(type.create(this, x, y));
    }

    // Provisorium
    public void registerProjectile(GameObject projectile) {
        projectiles.add(projectile);
    }

    public void registerParticle(Particle particle) {
        particles.add(particle);
    }

    public AbstractTower getSelectedTower() {
        return selectedTower;
    }

    public Collection<AbstractTower> getTowers() {
        return towers;
    }

    public Collection<Enemy> getEnemies() {
        return enemies;
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
