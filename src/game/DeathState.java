package game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import game.engine.State;
import game.map.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class DeathState extends State {
    
    private int highscore;

    public DeathState(Game game, Map map, int seconds) {
        super(game);
        playSound("gameover.wav", 1.0f);
        highscore = seconds;
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

     public void playSound(String file,float volume) {
            if (file == null || file.isEmpty()) {
                System.out.println("Sound Datei nicht vorhanden.");
                return;
            }
        try {
            // Hole den Sound als InputStream aus dem Ressourcenpfad
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                getClass().getResource("/assets/sounds/"+file)
            );
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound konnte nicht abgespielt werden: ");
            e.printStackTrace();
        }
    }
    
}
