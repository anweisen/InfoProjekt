package game;

import game.enemy.Enemy;
import game.engine.GameObject;
import game.engine.Particle;
import game.engine.State;
import game.map.Map;
import game.shop.Shop;
import game.tower.AbstractTower;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Collection;

public class GameState extends State {

    private final Map map;
    private final Shop shop;

    private final Collection<AbstractTower> towers = new ArrayList<>();
    private final Collection<Enemy> enemies = new ArrayList<>();
    private final Collection<GameObject> projectiles = new ArrayList<>();
    private final Collection<Particle> particles = new ArrayList<>();
    private int gegneranzahlpros = 1;
    private int spieldauer = 180;
    


    // TODO: Leben, Geld, ...

    private double spawnInterval; // für Standard-Enemy
    private double spawnIntervalStandard; //für Type1-Enemy
    private AbstractTower selectedTower;
    public GameState(Game game, Map map) {
        super(game);
        this.map = map;
        this.shop = new Shop(this);
    }
//keine Runden, Zeit zb 5 Minuten zum übereben
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

        if (shop.isOpen()) {
            shop.renderShopUI(graphics);
        }
    }

    public void gegneranzahl(int spieldauer, int gegneranzahlpros){
        spieldauer = spieldauer-1;
        if(spieldauer%30== 0){
            gegneranzahlpros= gegneranzahlpros+2;
        }
        for(int i=0;i<gegneranzahlpros;i++){
            enemies.add(new Enemy(this, map.getStart().x(),map.getStart().y() , "Standard"));
        }
        for(int j=0;j<gegneranzahlpros-1;j++){
            enemies.add(new Enemy(this, map.getStart().x(),map.getStart().y() , "Type1"));
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

        for(Enemy enemy : enemies) {
            if (enemy.isMarkedForRemoval()) {
                enemies.remove(enemy);
            }
        }
    }

    public void spawnEnemies(double deltaTime){
        // Provisorische Gegner-Spawning-Logik zum Testen
        spawnIntervalStandard += deltaTime; //für Standard-Enemy
        if (spawnIntervalStandard > 0.7) {
            spawnIntervalStandard = 0;
            enemies.add(new Enemy(this, map.getStart().x(), map.getStart().y(), "Standard"));
        }

        if(shop.getMoney()>=300){
                spawnInterval += deltaTime; //für Type1-Enemy
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
        if (x == 0 && y == 0) {
            shop.toggle();
            return;
        }

        shop.handleClick(x, y);
        // Erstelle Turm beim Klicken zu Testzwecken!
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
}
