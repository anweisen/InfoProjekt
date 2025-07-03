package game;

import game.engine.State;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class DeathState extends State {
    
    private int highscore;

    public DeathState(Game game, Map map) {
        super(game);
    }

    @Override
    public void render(GraphicsContext graphics) {
        // Hintergrund schwarz machen
        graphics.setFill(Color.BLACK);
        graphics.fillRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);
        
        // Titel "Game Over"
        graphics.setFill(Color.RED);
        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.fillText("GAME OVER", Game.VIRTUAL_WIDTH / 2.0, Game.VIRTUAL_HEIGHT / 2.0 - 50);
        
        // Highscore anzeigen
        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        graphics.fillText("Highscore: " + highscore, Game.VIRTUAL_WIDTH / 2.0, Game.VIRTUAL_HEIGHT / 2.0 + 20);
        
        // Anweisungen für den Spieler
        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        graphics.fillText("Klicke um zurück zum Menü zu gelangen", Game.VIRTUAL_WIDTH / 2.0, Game.VIRTUAL_HEIGHT / 2.0 + 80);
    }

    @Override
    public void update(double deltaTime) {
        
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void handleClick(double x, double y) {
        
    }
    
}
