package game.shop;

import game.Game;
import game.GameState;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Shop {

    private static final double WIDTH = Game.VIRTUAL_WIDTH * 0.2;

    private final GameState state;

    public Shop(GameState state) {
        this.state = state;
    }

    public void render(GraphicsContext graphics) {
        graphics.setFill(Color.rgb(0, 0, 0, 0.5));
        graphics.fillRect(Game.VIRTUAL_WIDTH - WIDTH, 0, WIDTH, Game.VIRTUAL_HEIGHT);

        int towerNumber = 0;
        for (TowerType type : state.getGame().getTowerTypes()) {
            double x = Game.VIRTUAL_WIDTH - WIDTH + 10;
            double y = 50 + towerNumber * 200;
            graphics.setFill(Color.WHITE);
            graphics.fillText(type.getConfig().getName(), x, y);
            graphics.setFill(Color.GRAY);
            graphics.fillRect(x, y + 10, WIDTH - 20, 30); // Platzhalter für Turm-Icon
            graphics.setFill(Color.BLACK);
            graphics.fillText("Preis: " + type.getConfig().getPrice(), x + 10, y + 30);
            graphics.drawImage(type.getConfig().getBaseModel().getImage(), x, y + 50, 64, 64); // Turm-Bild
            towerNumber++;
        }
    }

    public boolean isInShop(double x, double y) {
        return x >= Game.VIRTUAL_WIDTH - WIDTH && x <= Game.VIRTUAL_WIDTH && y >= 0 && y <= Game.VIRTUAL_HEIGHT;
    }

    public void handleClick(double x, double y) {

    }

}
