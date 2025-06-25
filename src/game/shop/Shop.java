package game.shop;

import game.Game;
import game.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Shop {

    private final GameState state;
    private final double WIDTH;
    private final double HEIGHT;

    private boolean isOpen; // Track if the shop is open or closed

    private int money; // Money available in the shop

    public Shop(GameState state) {
        this.state = state;
        this.WIDTH = Game.VIRTUAL_WIDTH * 0.25;
        this.HEIGHT = Game.VIRTUAL_HEIGHT;
        this.isOpen = false; // Initialize shop as open

        this.money = 100;
    }

    public void render(GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.fillRect(Game.VIRTUAL_WIDTH - WIDTH, 0, WIDTH, HEIGHT);
        double padding = Game.VIRTUAL_WIDTH * 0.015;
        int columns = 2;
        double availableWidth = WIDTH - (columns + 1) * padding;
        double squareSize = availableWidth / columns;

        for (int i = 0; i < state.getGame().getTowerTypes().size(); i++) {
            int row = i / columns;
            int col = i % columns;
            double x = Game.VIRTUAL_WIDTH - WIDTH + padding + col * (squareSize + padding);
            double y = padding + row * (squareSize + padding);

            context.setFill(Color.LIGHTGRAY);
            context.fillRect(x, y, squareSize, squareSize);
            context.drawImage(state.getGame().getTowerTypes().get(i).getConfig().getBaseModel().getImage(), x, y, squareSize, squareSize);

            // Draw the tower name centered underneath the square
            String name = state.getGame().getTowerTypes().get(i).getConfig().getName();
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

    public int handleClick(double mouseX, double mouseY) {
    // Check if click is inside the shop area
    double shopX = Game.VIRTUAL_WIDTH - WIDTH;
    if (mouseX < shopX || mouseX > Game.VIRTUAL_WIDTH || mouseY < 0 || mouseY > HEIGHT) {
        return -1;
    }

    if (isOpen) {
        double padding = Game.VIRTUAL_WIDTH * 0.015;
        int columns = 2;
        double availableWidth = WIDTH - (columns + 1) * padding;
        double squareSize = availableWidth / columns;

        for (int i = 0; i < state.getGame().getTowerTypes().size(); i++) {
            int row = i / columns;
            int col = i % columns;
            double x = shopX + padding + col * (squareSize + padding);
            double y = padding + row * (squareSize + padding);

            if (mouseX >= x && mouseX <= x + squareSize && mouseY >= y && mouseY <= y + squareSize) {
                return i; // Return the index of the clicked tower type
            }
        }
    }
    return -1;
}

    public boolean isOpen() {
        return isOpen;
    }

    public void toggle() {
        isOpen = !isOpen; // Toggle the shop's open state
    }

    public double getWidth() {
        return WIDTH;
    }

    public double getHeight() {
        return HEIGHT;
    }

}
