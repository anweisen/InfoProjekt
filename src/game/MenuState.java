package game;

import game.engine.State;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MenuState extends State {

    public MenuState(Game game) {
        super(game);
    }

    @Override
    public void render(GraphicsContext graphics) {
        graphics.clearRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);

        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        graphics.fillText("Test-Menu", Game.VIRTUAL_WIDTH / 2d, Game.VIRTUAL_HEIGHT / 2d);
        graphics.fillText("Klicke zum Starten", Game.VIRTUAL_WIDTH / 2d, Game.VIRTUAL_HEIGHT / 2d + 50);
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void handleClick(double x, double y) {
        game.setState(new GameState(game, new Map()));
    }
}
