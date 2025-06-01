package game;

import game.engine.Model;
import game.engine.State;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MenuState extends State {

    private Image backgroundImage;
    private Image[] mapImages;
    private String[] mapNames;
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

        backgroundImage = Model.loadImage("menu", "background.jpg");
        // Nutze: Game#getMaps() Map#getName() Map#getImage()
        mapImages = new Image[] {
            Model.loadImage("menu", "Map1.jpg"),
            Model.loadImage("menu", "Map2.jpg")
        };
        mapNames = new String[] {"Map1", "Map2"};
        arrowLeft = Model.loadImage("menu", "left_arrow.png");
        arrowRight = Model.loadImage("menu", "right_arrow.png");
        closeButton = Model.loadImage("menu", "close_button.png");

        currentMapIndex = 0;

        /* 
            Mit Hilfe von ChatGPT, muss aber sowieso
            umgeschrieben werden, weil unsere Maps
            ein ganz anderes Format haben.
        */
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
            double bgX = (Game.VIRTUAL_WIDTH - backgroundImage.getWidth()) / 2;
            double bgY = (Game.VIRTUAL_HEIGHT - backgroundImage.getHeight()) / 2;
            graphics.drawImage(backgroundImage, bgX, bgY);
        } else {
            // Fallback-Hintergrund
            graphics.setFill(Color.LIGHTGRAY);
            graphics.fillRect(0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);
        }

        Image currentMap = mapImages[currentMapIndex];
        graphics.drawImage(currentMap, imageX, imageY, imageWidth, imageHeight);

        graphics.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        graphics.setFill(Color.BLACK);
        graphics.fillText(mapNames[currentMapIndex], Game.VIRTUAL_WIDTH / 2d - 30, imageY + imageHeight + 30);

        graphics.drawImage(arrowLeft, leftArrowX, leftArrowY, arrowWidth, arrowHeight);
        graphics.drawImage(arrowRight, rightArrowX, rightArrowY, arrowWidth, arrowHeight);

        graphics.drawImage(closeButton, closeX, closeY, closeWidth, closeHeight);

        graphics.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
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
            currentMapIndex = (currentMapIndex - 1 + mapImages.length) % mapImages.length;
            return;
        }

        if (x >= rightArrowX && x <= rightArrowX + arrowWidth &&
            y >= rightArrowY && y <= rightArrowY + arrowHeight) {
            currentMapIndex = (currentMapIndex + 1) % mapImages.length;
            return;
        }

        if (x >= closeX && x <= closeX + closeWidth &&
            y >= closeY && y <= closeY + closeHeight) {
            System.exit(0);
            return;
        }

        /* 
            Weiß nicht genau, wie Philipp das
            mit den Maps machen will, wird entsprechend
            später noch angepasst.
         */
        if (x >= imageX && x <= imageX + imageWidth &&
            y >= imageY && y <= imageY + imageHeight) {
//            game.setState(new GameState(game, new Map()));
            game.setState(new GameState(game, game.getMaps().get(currentMapIndex)));
        }
    }
}
