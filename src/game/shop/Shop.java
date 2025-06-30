package game.shop;

import java.util.ArrayList;

import game.Game;
import game.GameState;
import game.hud.Hud;
import game.tower.TowerType;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;

public class Shop {

    private static final int COLUMNS = 2;
    private static final double PADDING_RATIO = 0.015;

    private final GameState state;
    private final double WIDTH;
    private final double HEIGHT;

    private ArrayList<TowerType> towerTypes;
    private int selectedTowerIndex;
    private boolean isOpen;
    private final Hud hud;

    public Shop(GameState state) {
        this.state = state;
        this.WIDTH = Game.VIRTUAL_WIDTH * 0.25;
        this.HEIGHT = Game.VIRTUAL_HEIGHT;

        this.towerTypes = new ArrayList<>(state.getGame().getTowerTypes());
        this.towerTypes.sort(java.util.Comparator.comparingInt(t -> t.getConfig().getPrice()));
        this.selectedTowerIndex = 0;
        this.isOpen = false;
        this.hud = state.getHud();
    }

    public void addMoney(int amount) {
        hud.addMoney(amount);
        System.out.println(amount + " added to shop");
    }

    public int getMoney() {
        System.out.println(hud.getMoney());
        return hud.getMoney();
    }

    public void buy(TowerType towerType, double x, double y) {
        if (hud.getMoney() >= towerType.getConfig().getPrice()) {
            hud.removeMoney(towerType.getConfig().getPrice());
            state.spawnTower(towerType, x, y);
            System.out.println("Tower placed: " + towerType.getConfig().getName());
        } else {
            System.out.println("Not enough money to place tower: " + towerType.getConfig().getName());
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void toggle() {
        isOpen = !isOpen;
    }

    public double getWidth() {
        return WIDTH;
    }

    public double getHeight() {
        return HEIGHT;
    }

    public Hud getHud() {
        return hud;
    }

    public void render(GraphicsContext context) {
        context.setFill(Color.WHITE);
        context.fillRect(Game.VIRTUAL_WIDTH - WIDTH, 0, WIDTH, HEIGHT);

        double padding = Game.VIRTUAL_WIDTH * PADDING_RATIO;
        double availableWidth = WIDTH - (COLUMNS + 1) * padding;
        double squareSize = availableWidth / COLUMNS;

        for (int i = 0; i < towerTypes.size(); i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            double x = Game.VIRTUAL_WIDTH - WIDTH + padding + col * (squareSize + padding);
            double y = padding + row * (squareSize + padding);

            context.setFill(Color.LIGHTGRAY);
            context.fillRect(x, y, squareSize, squareSize);
            context.drawImage(
                towerTypes.get(i).getConfig().getBaseModel().getImage(),
                x, y, squareSize, squareSize
            );

            // Grey out if not enough money
            int cost = towerTypes.get(i).getConfig().getPrice();
            if (hud.getMoney() < cost) {
                context.setFill(Color.rgb(128, 128, 128, 0.5));
                context.fillRect(x, y, squareSize, squareSize);
            }

            String name = towerTypes.get(i).getConfig().getName();
            context.setFill(Color.BLACK);
            context.setTextAlign(TextAlignment.CENTER);
            context.setTextBaseline(VPos.TOP);
            context.fillText(name, x + squareSize / 2, y + squareSize + 5);
        }
        context.setTextAlign(TextAlignment.LEFT);
        context.setTextBaseline(VPos.BASELINE);
    }

    public void handleClick(double mouseX, double mouseY) {
        double shopX = Game.VIRTUAL_WIDTH - WIDTH;
        if (mouseX < shopX || mouseX > Game.VIRTUAL_WIDTH || mouseY < 0 || mouseY > HEIGHT || !isOpen) {
            buy(towerTypes.get(selectedTowerIndex), mouseX, mouseY);
            return;
        }

        double padding = Game.VIRTUAL_WIDTH * PADDING_RATIO;
        double availableWidth = WIDTH - (COLUMNS + 1) * padding;
        double squareSize = availableWidth / COLUMNS;

        for (int i = 0; i < towerTypes.size(); i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            double x = shopX + padding + col * (squareSize + padding);
            double y = padding + row * (squareSize + padding);

            if (mouseX >= x && mouseX <= x + squareSize && mouseY >= y && mouseY <= y + squareSize) {
                selectedTowerIndex = i;
                return;
            }
        }
    }

}
