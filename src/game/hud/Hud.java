package game.hud;

import game.GameState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Hud {

    private final GameState state;

    private final double WIDTH;
    private final double HEIGHT;

    private final double buttonX;
    private final double buttonY;
    private final double buttonWidth;
    private final double buttonHeight;

    private int money;
    private int lives;

    public Hud(GameState state) {
        this.state = state;
        this.WIDTH = state.getGame().VIRTUAL_WIDTH;
        this.HEIGHT = state.getGame().VIRTUAL_HEIGHT/10;

        this.buttonWidth = WIDTH / 6;
        this.buttonHeight = HEIGHT * 0.6;
        this.buttonX = WIDTH - buttonWidth - WIDTH * 0.03;
        this.buttonY = HEIGHT * 0.2;

        this.money = 100; // Startgeld
        this.lives = 20; // Startleben
    }

    public void render(GraphicsContext graphics) {
        // Draw semi-transparent dark rounded background
        graphics.setFill(Color.rgb(30, 30, 30, 0.85));
        // graphics.fillRoundRect(0, 0, WIDTH, HEIGHT, HEIGHT * 0.4, HEIGHT * 0.4);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        // Set font and color for text
        graphics.setFill(Color.WHITE);
        graphics.setFont(new Font("Arial", HEIGHT * 0.28));
        double textPadding = WIDTH * 0.02;
        graphics.fillText("Money: " + money, textPadding, HEIGHT * 0.38);
        graphics.fillText("Lives: " + lives, textPadding, HEIGHT * 0.78);

        // Draw Shop button background (shadow)
        graphics.setFill(Color.rgb(30, 30, 30, 0.5));
        graphics.fillRoundRect(buttonX + buttonWidth * 0.03, buttonY + buttonHeight * 0.05, buttonWidth, buttonHeight, buttonHeight * 0.3, buttonHeight * 0.3);

        // Draw Shop button (main)
        graphics.setFill(Color.DARKGRAY);
        graphics.fillRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, buttonHeight * 0.3, buttonHeight * 0.3);

        // Draw Shop button border
        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(buttonHeight * 0.07);
        graphics.strokeRoundRect(buttonX, buttonY, buttonWidth, buttonHeight, buttonHeight * 0.3, buttonHeight * 0.3);

        // Draw Shop button text
        graphics.setFill(Color.WHITE);
        graphics.setFont(new Font("Arial", buttonHeight * 0.5));
        String buttonText = "Shop";
        double textWidth = graphics.getFont().getSize() * buttonText.length() * 0.6;
        double textX = buttonX + (buttonWidth - textWidth) / 2;
        double textY = buttonY + buttonHeight * 0.65;
        graphics.fillText(buttonText, textX, textY);
    }

    public boolean isShopButtonClicked(double x, double y) {
        return x >= buttonX && x <= buttonX + buttonWidth &&
               y >= buttonY && y <= buttonY + buttonHeight;
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public void removeMoney(int amount) {
        money -= amount;
    }

    public void loseLife() {
        lives--;
    }

    public int getMoney() {
        return money;
    }

    public int getLives() {
        return lives;
    }

    public double getHeight() {
        return HEIGHT;
    }

}
