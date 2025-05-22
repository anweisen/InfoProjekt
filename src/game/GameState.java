package game;

import game.engine.GameObject;
import game.engine.State;
import game.map.Map;
import game.shop.Shop;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class GameState extends State {

    private final Map map;
    private final Shop shop;

    private final List<GameObject> objects;

    public GameState(Game game, Map map) {
        super(game);
        this.map = map;
        this.shop = new Shop();
        this.objects = new ArrayList<>();
    }

    @Override
    public void render(GraphicsContext graphics) {
        graphics.clearRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);
        map.render(graphics);

        for (GameObject object : objects) {
            object.render(graphics);
        }

        shop.render(graphics);
    }

    @Override
    public void update(double deltaTime) {
        for (GameObject object : objects) {
            object.update(deltaTime);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleClick(double x, double y) {

    }
}
