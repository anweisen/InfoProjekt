package game.hud;

import game.Game;
import game.engine.assets.Model;
import game.state.GameState;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.text.DecimalFormat;

public class Hud {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###");
    private static final Model coinModel = Model.loadModelWith("menu", "coin.png", 44, 44);
    private static final Model heartModel = Model.loadModelWith("menu", "heart.png", 44, 44);
    private static final double positionY = Game.VIRTUAL_HEIGHT * 0.04;
    private static final double startX = Game.VIRTUAL_HEIGHT * 0.05;
    private static final Font font = Font.font("Calibri", FontWeight.BLACK, 30);
    private static final double livesBarWidth = 150;

    private final GameState state;

    public Hud(GameState state) {
        this.state = state;
    }

    public void render(GraphicsContext graphics) {
        final double halfModelSize = heartModel.getWidth() / 2d;
        final double livesBarHeight = font.getSize() * .8;
        final double halfLivesBarHeight = livesBarHeight / 2d;

        graphics.setStroke(Color.BLACK);
        // TODO animate
        heartModel.render(graphics, startX, positionY);
        graphics.setFill(Color.DARKSLATEGRAY);
        graphics.fillRoundRect(startX + halfModelSize + 5, positionY - halfLivesBarHeight, livesBarWidth, livesBarHeight, 16, 16);
        graphics.setFill(Color.rgb(240, 54, 54));
        graphics.fillRoundRect(startX + halfModelSize + 5, positionY - halfLivesBarHeight, livesBarWidth * ((double) state.getLives() / state.getStartLives()), livesBarHeight, 16, 16);
        graphics.setLineWidth(2);
        graphics.strokeRoundRect(startX + halfModelSize + 5, positionY - halfLivesBarHeight, livesBarWidth, livesBarHeight, 16, 16);

        coinModel.render(graphics, startX + halfModelSize * 2 + 5 + livesBarWidth + 50, positionY);
        String formattedMoney = DECIMAL_FORMAT.format(state.getMoney());
        graphics.setFill(Color.WHITE);
        graphics.setLineWidth(4);
        graphics.setFont(font);
        graphics.setTextAlign(TextAlignment.LEFT);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.strokeText(formattedMoney, startX + halfModelSize * 3 + 5 + livesBarWidth + 50 + 5, positionY + 2);
        graphics.fillText(formattedMoney, startX + halfModelSize * 3 + 5 + livesBarWidth + 50 + 5, positionY + 2);
    }
}
