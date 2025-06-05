package game.shop;

import java.util.ArrayList;
import java.util.List;

import game.GameState;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Shop {

    private final GameState state;
    private final double WIDTH;
    private final double HEIGHT;

    public Shop(GameState state) {
        this.state = state;
        this.WIDTH = state.getGame().VIRTUAL_WIDTH * 0.25;
        this.HEIGHT = state.getGame().VIRTUAL_HEIGHT;
    }

    public void render(GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.fillRect(state.getGame().VIRTUAL_WIDTH - WIDTH, 0, WIDTH, HEIGHT);
    }

}
