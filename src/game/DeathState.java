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
import java.util.List;
import java.util.ArrayList;

public class DeathState extends State {
    
    private int highscore;
    private String playerName;
    private boolean isEnteringName;
    private List<String> topScores;
    private boolean scoreSaved;

    public DeathState(Game game, Map map, int seconds) {
        super(game);
<<<<<<< Updated upstream
        playSound("gameover.wav", 1.0f);
        highscore = seconds;
=======
        this.highscore = 1; // Erstmal 1 wie gewünscht
        this.playerName = "";
        this.isEnteringName = false;
        this.topScores = HighScoreManager.getHighscores();
        // Sicherstellen, dass topScores nie null ist
        if (this.topScores == null) {
            this.topScores = new ArrayList<>();
        }
        this.scoreSaved = false;
>>>>>>> Stashed changes
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
        graphics.fillText("GAME OVER", Game.VIRTUAL_WIDTH / 2.0, 120);
        
        // Linke Seite - Aktueller Highscore und Eingabe
        graphics.setTextAlign(TextAlignment.CENTER);
        double leftX = Game.VIRTUAL_WIDTH / 4.0;
        
        // Aktueller Highscore anzeigen
        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        graphics.fillText("Dein Score:", leftX, Game.VIRTUAL_HEIGHT / 2.0 - 80);
        
        graphics.setFill(Color.YELLOW);
        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        graphics.fillText(String.valueOf(highscore), leftX, Game.VIRTUAL_HEIGHT / 2.0 - 30);
        
        // Eingabefeld für Namen (nur anzeigen wenn noch nicht gespeichert)
        if (!scoreSaved) {
            graphics.setFill(Color.WHITE);
            graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
            graphics.fillText("Name eingeben:", leftX, Game.VIRTUAL_HEIGHT / 2.0 + 30);
            
            // Textfeld für Namen (simuliert)
            graphics.setStroke(Color.WHITE);
            graphics.setLineWidth(2);
            double textFieldWidth = 200;
            double textFieldHeight = 30;
            double textFieldX = leftX - textFieldWidth / 2;
            double textFieldY = Game.VIRTUAL_HEIGHT / 2.0 + 50;
            graphics.strokeRect(textFieldX, textFieldY, textFieldWidth, textFieldHeight);
            
            // Name anzeigen (wenn eingegeben)
            if (isEnteringName) {
                graphics.setFill(Color.LIGHTGRAY);
                graphics.fillRect(textFieldX, textFieldY, textFieldWidth, textFieldHeight);
            }
            
            graphics.setFill(Color.BLACK);
            graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            graphics.setTextAlign(TextAlignment.LEFT);
            graphics.fillText(playerName + (isEnteringName ? "|" : ""), textFieldX + 5, textFieldY + 20);
            
            // Save Button
            graphics.setTextAlign(TextAlignment.CENTER);
            graphics.setFill(Color.GREEN);
            graphics.fillRect(leftX - 50, Game.VIRTUAL_HEIGHT / 2.0 + 110, 100, 35);
            graphics.setFill(Color.WHITE);
            graphics.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            graphics.fillText("SPEICHERN", leftX, Game.VIRTUAL_HEIGHT / 2.0 + 132);
        } else {
            // Nach dem Speichern nur Bestätigung anzeigen
            graphics.setFill(Color.GREEN);
            graphics.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            graphics.setTextAlign(TextAlignment.CENTER);
            graphics.fillText("✓ Score gespeichert!", leftX, Game.VIRTUAL_HEIGHT / 2.0 + 80);
        }
        
        // Rechte Seite - Top 5 Highscores
        double rightX = Game.VIRTUAL_WIDTH * 3.0 / 4.0;
        
        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.fillText("TOP 5 HIGHSCORES", rightX, Game.VIRTUAL_HEIGHT / 2.0 - 80);
        
        // Highscore-Tabelle
        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        graphics.setTextAlign(TextAlignment.LEFT);
        
        if (topScores.isEmpty()) {
            // Wenn keine Highscores vorhanden sind
            graphics.setFill(Color.LIGHTGRAY);
            graphics.setTextAlign(TextAlignment.CENTER);
            graphics.fillText("Noch keine Highscores vorhanden", rightX, Game.VIRTUAL_HEIGHT / 2.0 - 20);
        } else {
            // Vorhandene Highscores anzeigen (max 5)
            for (int i = 0; i < Math.min(5, topScores.size()); i++) {
                String score = topScores.get(i);
                double yPos = Game.VIRTUAL_HEIGHT / 2.0 - 30 + (i * 25);
                
                // Platz anzeigen
                graphics.setFill(Color.GOLD);
                graphics.fillText((i + 1) + ".", rightX - 120, yPos);
                
                // Name und Score anzeigen
                graphics.setFill(Color.WHITE);
                graphics.fillText(score, rightX - 100, yPos);
            }
            
            // Wenn weniger als 5 Scores vorhanden sind, zeige leere Plätze
            if (topScores.size() < 5) {
                graphics.setFill(Color.GRAY);
                for (int i = topScores.size(); i < 5; i++) {
                    double yPos = Game.VIRTUAL_HEIGHT / 2.0 - 30 + (i * 25);
                    graphics.fillText((i + 1) + ". ---", rightX - 120, yPos);
                }
            }
        }
        
        // Anweisungen für den Spieler
<<<<<<< Updated upstream
        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        graphics.fillText("Klicke um zurück zum Menü zu gelangen", Game.VIRTUAL_WIDTH / 2.0, Game.VIRTUAL_HEIGHT / 2.0 + 80);
        
=======
        graphics.setFill(Color.LIGHTGRAY);
        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.fillText("Klicke um zurück zum Menü zu gelangen", Game.VIRTUAL_WIDTH / 2.0, Game.VIRTUAL_HEIGHT - 50);
        graphics.fillText("Klicke auf das Textfeld um deinen Namen einzugeben", Game.VIRTUAL_WIDTH / 2.0, Game.VIRTUAL_HEIGHT - 30);
>>>>>>> Stashed changes
    }

    @Override
    public void update(double deltaTime) {
        
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void handleClick(double x, double y) {
        double leftX = Game.VIRTUAL_WIDTH / 4.0;
        double textFieldWidth = 200;
        double textFieldHeight = 30;
        double textFieldX = leftX - textFieldWidth / 2;
        double textFieldY = Game.VIRTUAL_HEIGHT / 2.0 + 50;
        
        // Klick auf Textfeld (nur wenn noch nicht gespeichert)
        if (!scoreSaved && x >= textFieldX && x <= textFieldX + textFieldWidth &&
            y >= textFieldY && y <= textFieldY + textFieldHeight) {
            isEnteringName = true;
            return;
        }
        
        // Klick auf Save Button (nur wenn noch nicht gespeichert)
        if (!scoreSaved && !playerName.trim().isEmpty() && 
            x >= leftX - 50 && x <= leftX + 50 &&
            y >= Game.VIRTUAL_HEIGHT / 2.0 + 110 && y <= Game.VIRTUAL_HEIGHT / 2.0 + 145) {
            
            // Highscore speichern
            HighScoreManager.addHighscore(playerName.trim(), highscore);
            scoreSaved = true;
            
            // Highscores neu laden
            topScores = HighScoreManager.getHighscores();
            // Sicherstellen, dass topScores nie null ist
            if (topScores == null) {
                topScores = new ArrayList<>();
            }
            return;
        }
        
        // Klick außerhalb - zurück zum Menü (wenn nicht gerade Name eingegeben wird)
        if (!isEnteringName) {
            game.setState(new MenuState(game));
        } else {
            isEnteringName = false;
        }
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
