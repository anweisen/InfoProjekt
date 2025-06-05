package game.shop;

import java.util.List;

import game.Game;
import game.GameState;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Shop {

    private final GameState state;
    private final double WIDTH;
    private final double HEIGHT;
    private final List<TowerType> TOWERS;

    public Shop(GameState state) {
        this.state = state;
        this.WIDTH = Game.VIRTUAL_WIDTH * 0.25;
        this.HEIGHT = Game.VIRTUAL_HEIGHT;
        this.TOWERS = state.getGame().getTowerTypes();
    }

    public void render(GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.fillRect(Game.VIRTUAL_WIDTH - WIDTH, 0, WIDTH, HEIGHT);
        double padding = Game.VIRTUAL_WIDTH * 0.015;
        int columns = 2;
        double availableWidth = WIDTH - (columns + 1) * padding;
        double squareSize = availableWidth / columns;

        for (int i = 0; i < TOWERS.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            double x = Game.VIRTUAL_WIDTH - WIDTH + padding + col * (squareSize + padding);
            double y = padding + row * (squareSize + padding);

            context.setFill(Color.LIGHTGRAY);
            context.fillRect(x, y, squareSize, squareSize);
            context.drawImage(TOWERS.get(i).getConfig().getBaseModel().getImage(), x, y, squareSize, squareSize);

            // Draw the tower name centered underneath the square
            String name = TOWERS.get(i).getConfig().getName();
            context.setFill(Color.BLACK);
            context.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            context.setTextBaseline(javafx.geometry.VPos.TOP);
            double textX = x + squareSize / 2;
            double textY = y + squareSize + 5;
            context.fillText(name, textX, textY);
        }
        // Reset text alignment if needed elsewhere
        context.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
        context.setTextBaseline(javafx.geometry.VPos.BASELINE);
    }

}
