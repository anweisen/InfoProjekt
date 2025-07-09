package game;

import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

import game.engine.Model;
import game.engine.State;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MenuState extends State {

    private Image backgroundImage;
    private ArrayList<Image> mapImages = new ArrayList<>();
    private ArrayList<String> mapNames = new ArrayList<>();
    private int currentMapIndex;

    // Layout
    private double imageMargin;
    private double imageX, imageY, imageWidth, imageHeight;

    private double closeX, closeY, closeWidth, closeHeight;

    private Clip menuClip;

    public MenuState(Game game) {
        super(game);

        backgroundImage = Model.loadImage("menu", "background.png");
        for (int i = 0; i < game.getMaps().size(); i++) {
            mapImages.add(game.getMaps().get(i).getImage());
            mapNames.add(game.getMaps().get(i).getName());
        }

        currentMapIndex = 0;

        imageMargin = Game.VIRTUAL_HEIGHT * 0.15;
        imageX = Game.VIRTUAL_WIDTH * 0.1;
        imageY = imageMargin;
        imageWidth = Game.VIRTUAL_WIDTH * 0.8;
        imageHeight = Game.VIRTUAL_HEIGHT * 0.7;

        closeWidth = 80;
        closeHeight = 80;
        closeX = Game.VIRTUAL_WIDTH - closeWidth - 20;
        closeY = Game.VIRTUAL_HEIGHT - closeHeight - 20;

        playSound("main.wav", 1f);
    }

    @Override
    public void render(GraphicsContext graphics) {
        graphics.clearRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);

        if (backgroundImage != null) {
            graphics.drawImage(backgroundImage, 0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);
        } else {
            graphics.setFill(Color.LIGHTGRAY);
            graphics.fillRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);
        }

        double arc = 40;

        double imgX = imageX;
        double imgY = imageY;
        double imgW = imageWidth;
        double imgH = imageHeight;

        // Map-Image mit abgerundeten Ecken zeichnen (Clipping)
        graphics.save();
        graphics.beginPath();
        graphics.moveTo(imgX + arc, imgY);
        graphics.lineTo(imgX + imgW - arc, imgY);
        graphics.arcTo(imgX + imgW, imgY, imgX + imgW, imgY + arc, arc);
        graphics.lineTo(imgX + imgW, imgY + imgH - arc);
        graphics.arcTo(imgX + imgW, imgY + imgH, imgX + imgW - arc, imgY + imgH, arc);
        graphics.lineTo(imgX + arc, imgY + imgH);
        graphics.arcTo(imgX, imgY + imgH, imgX, imgY + imgH - arc, arc);
        graphics.lineTo(imgX, imgY + arc);
        graphics.arcTo(imgX, imgY, imgX + arc, imgY, arc);
        graphics.closePath();
        graphics.clip();

        Image currentMap = mapImages.get(currentMapIndex);
        graphics.drawImage(currentMap, imgX, imgY, imgW, imgH);

        graphics.restore();

        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        graphics.setFill(Color.BLACK);
        graphics.fillText(mapNames.get(currentMapIndex), Game.VIRTUAL_WIDTH / 2d - 30, imageY + imageHeight + 30);

        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        graphics.setFill(Color.BLACK);
        graphics.fillText("Klicke auf das Bild zum Starten", Game.VIRTUAL_WIDTH / 2d - 100, Game.VIRTUAL_HEIGHT - 50);
    }

    @Override
    public void update(double deltaTime) {
        
    }

    @Override
    public void dispose() {
            if(menuClip != null && menuClip.isRunning()) {
            menuClip.stop();
            menuClip.close();
            menuClip = null;
        }
    }

    @Override
    public void handleClick(double x, double y) {
        if (x >= 0 && x < imageX &&
            y >= imageY && y <= imageY + imageHeight) {
            currentMapIndex = (currentMapIndex - 1 + mapImages.size()) % mapImages.size();
            return;
        }

        if (x > imageX + imageWidth && x <= Game.VIRTUAL_WIDTH &&
            y >= imageY && y <= imageY + imageHeight) {
            currentMapIndex = (currentMapIndex + 1) % mapImages.size();
            return;
        }

        if (x >= closeX && x <= closeX + closeWidth &&
            y >= closeY && y <= closeY + closeHeight) {
            System.exit(0);
            return;
        }

        if (x >= imageX && x <= imageX + imageWidth &&
            y >= imageY && y <= imageY + imageHeight) {
            game.setState(new GameState(game, game.getMaps().get(currentMapIndex)));
        }
    }

    @Override
    public void handleKeyPressed(KeyEvent event) {
        // wird nicht verwendet
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

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log10(volume)*20);
        gainControl.setValue(dB);

        clip.start();

        menuClip = clip;
    } catch (Exception e) {
        System.out.println("Sound konnte nicht abgespielt werden: ");
        e.printStackTrace();
    }
}
}
