package game;

import java.util.ArrayList;

import game.engine.Model;
import game.engine.State;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MenuState extends State {

    private Image backgroundImage;
    private ArrayList<Image> mapImages = new ArrayList<>();
    private ArrayList<String> mapNames = new ArrayList<>();
    private int currentMapIndex;
    private Image arrowLeft, arrowRight, closeButton;

    // Layout
    private double imageMargin;
    private double imageX, imageY, imageWidth, imageHeight;

    private double leftArrowX, leftArrowY, arrowWidth, arrowHeight;
    private double rightArrowX, rightArrowY;

    private double closeX, closeY, closeWidth, closeHeight;

    public MenuState(Game game) {
        super(game);

        backgroundImage = Model.loadImage("menu", "background.png");
        for (int i = 0; i < game.getMaps().size(); i++) {
            mapImages.add(game.getMaps().get(i).getImage());
            mapNames.add(game.getMaps().get(i).getName());
        }
        /* arrowLeft = Model.loadImage("menu", "left_arrow.png");
        arrowRight = Model.loadImage("menu", "right_arrow.png");
        closeButton = Model.loadImage("menu", "close_button.png"); */

        currentMapIndex = 0;

        imageMargin = Game.VIRTUAL_HEIGHT * 0.15;
        imageX = Game.VIRTUAL_WIDTH * 0.1;
        imageY = imageMargin;
        imageWidth = Game.VIRTUAL_WIDTH * 0.8;
        imageHeight = Game.VIRTUAL_HEIGHT * 0.7;

        arrowWidth = 40;
        arrowHeight = 40;
        leftArrowX = imageX - arrowWidth - 20;
        leftArrowY = imageY + imageHeight / 2 - arrowHeight / 2;

        rightArrowX = imageX + imageWidth + 20;
        rightArrowY = leftArrowY;

        closeWidth = 40;
        closeHeight = 40;
        closeX = Game.VIRTUAL_WIDTH - closeWidth - 20;
        closeY = Game.VIRTUAL_HEIGHT - closeHeight - 20;
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

        /* graphics.drawImage(arrowLeft, leftArrowX, leftArrowY, arrowWidth, arrowHeight);
        graphics.drawImage(arrowRight, rightArrowX, rightArrowY, arrowWidth, arrowHeight);
        graphics.drawImage(closeButton, closeX, closeY, closeWidth, closeHeight); */

        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        graphics.setFill(Color.BLACK);
        graphics.fillText("Klicke auf das Bild zum Starten", Game.VIRTUAL_WIDTH / 2d - 100, Game.VIRTUAL_HEIGHT - 50);
    }

    @Override
    public void update(double deltaTime) {
        
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void handleClick(double x, double y) {
        if (x >= leftArrowX && x <= leftArrowX + arrowWidth &&
            y >= leftArrowY && y <= leftArrowY + arrowHeight) {
            currentMapIndex = (currentMapIndex - 1 + mapImages.size()) % mapImages.size();
            return;
        }

        if (x >= rightArrowX && x <= rightArrowX + arrowWidth &&
            y >= rightArrowY && y <= rightArrowY + arrowHeight) {
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
}
