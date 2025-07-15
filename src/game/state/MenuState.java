package game.state;

import game.Game;
import game.engine.State;
import game.engine.assets.Model;
import game.engine.assets.Sound;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import javax.sound.sampled.Clip;
import java.util.ArrayList;

public class MenuState extends State {

    private Image backgroundImage;
    private ArrayList<Image> mapImages = new ArrayList<>();
    private ArrayList<String> mapNames = new ArrayList<>();
    private int currentMapIndex;

    // Layout
    private double imageX, imageY, imageWidth, imageHeight;

    private double closeX, closeY, closeWidth, closeHeight;

    private Clip menuClip;

    public MenuState(Game game) {
        super(game);

        backgroundImage = Model.loadImage("menu", "bg.png");
        for (int i = 0; i < game.getMaps().size(); i++) {
            mapImages.add(game.getMaps().get(i).getImage());
            mapNames.add(game.getMaps().get(i).getName());
        }

        currentMapIndex = 0;

        double padding = 0.2;
        imageY = Game.VIRTUAL_HEIGHT * padding;
        imageX = Game.VIRTUAL_WIDTH * padding;
        imageWidth = Game.VIRTUAL_WIDTH - imageX * 2;
        imageHeight = Game.VIRTUAL_HEIGHT - imageY * 2;

        closeWidth = 80;
        closeHeight = 80;
        closeX = Game.VIRTUAL_WIDTH - closeWidth - 20;
        closeY = Game.VIRTUAL_HEIGHT - closeHeight - 20;

        playMusic();
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

        // draw outline around the image
        graphics.setStroke(Color.rgb(248, 248, 248, .4));
        double borderWidth = 8;
        graphics.setLineWidth(borderWidth);
        graphics.strokeRoundRect(imgX - borderWidth / 2, imgY - borderWidth / 2, imgW + borderWidth, imgH + borderWidth, arc * 2 + borderWidth, arc * 2 + borderWidth);

        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        graphics.setFont(Font.font("Calibri", FontWeight.BOLD, 26));
        graphics.setLineWidth(2.5);
        graphics.setFill(Color.rgb(248, 248, 248));
        graphics.setStroke(Color.rgb(0, 0, 0, .25));
        graphics.strokeText(mapNames.get(currentMapIndex), Game.VIRTUAL_WIDTH / 2d + .5, .5 + imageY + imageHeight + 66);
        graphics.fillText(mapNames.get(currentMapIndex), Game.VIRTUAL_WIDTH / 2d, imageY + imageHeight + 66);
        graphics.setFont(Font.font("Calibri", FontWeight.NORMAL, 20));
        graphics.setLineWidth(2);
        graphics.strokeText("Klicke auf das Bild zum Starten", Game.VIRTUAL_WIDTH / 2d + .5, .5 + imageY + imageHeight + 96);
        graphics.fillText("Klicke auf das Bild zum Starten", Game.VIRTUAL_WIDTH / 2d, imageY + imageHeight + 96);
    }

    @Override
    public void update(double deltaTime) {

    }

    @Override
    public void dispose() {
        if (menuClip != null && menuClip.isRunning()) {
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
            Sound.SWOOSH.playSound();
            return;
        }

        if (x > imageX + imageWidth && x <= Game.VIRTUAL_WIDTH &&
            y >= imageY && y <= imageY + imageHeight) {
            currentMapIndex = (currentMapIndex + 1) % mapImages.size();
            Sound.SWOOSH.playSound();
            return;
        }

        if (x >= closeX && x <= closeX + closeWidth &&
            y >= closeY && y <= closeY + closeHeight) {
            System.exit(0);
            return;
        }

        if (x >= imageX && x <= imageX + imageWidth &&
            y >= imageY && y <= imageY + imageHeight) {
            Sound.POP.playSound();
            game.setState(new GameState(game, game.getMaps().get(currentMapIndex)));
        }
    }

    @Override
    public void handleKeyPressed(KeyEvent event) {
        // wird nicht verwendet
    }

    private void playMusic() {
        menuClip = Sound.loadClip("menu", "main.wav", 0.4f);
        if (menuClip != null) {
            menuClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
}
