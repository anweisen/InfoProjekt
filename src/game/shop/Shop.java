package game.shop;

import java.util.ArrayList;

import game.Game;
import game.GameState;
import game.hud.Hud;
import game.tower.AbstractTower;
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
    private final double HUD_HEIGHT;

    private ArrayList<TowerType> towerTypes;
    private int selectedTowerIndex;
    private boolean isOpen;
    private final Hud hud;

    public Shop(GameState state) {
        this.state = state;
        this.WIDTH = Game.VIRTUAL_WIDTH * 0.25;
        this.hud = state.getHud();
        this.HUD_HEIGHT = hud.getHeight();
        this.HEIGHT = Game.VIRTUAL_HEIGHT - HUD_HEIGHT;

        this.towerTypes = new ArrayList<>(state.getGame().getTowerTypes());
        this.towerTypes.sort(java.util.Comparator.comparingInt(t -> t.getConfig().getPrice()));
        this.selectedTowerIndex = 0;
        this.isOpen = false;
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

    public void setOpen(boolean open) {
        isOpen = open;
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

    public void renderShop(GraphicsContext context) {
        // Draw semi-transparent dark background
        context.setFill(Color.rgb(30, 30, 30, 0.85));
        // context.fillRoundRect(Game.VIRTUAL_WIDTH - WIDTH, HUD_HEIGHT, WIDTH, HEIGHT,
        // 30, 30);
        context.fillRect(Game.VIRTUAL_WIDTH - WIDTH, HUD_HEIGHT, WIDTH, HEIGHT);

        double padding = Game.VIRTUAL_WIDTH * PADDING_RATIO;
        double availableWidth = WIDTH - (COLUMNS + 1) * padding;
        double squareSize = availableWidth / COLUMNS;

        // Draw tower slots
        for (int i = 0; i < towerTypes.size(); i++) {
            int row = i / COLUMNS;
            int col = i % COLUMNS;
            double x = Game.VIRTUAL_WIDTH - WIDTH + padding + col * (squareSize + padding);
            // double y = HUD_HEIGHT + padding + titleHeight + row * (squareSize + padding);
            double y = HUD_HEIGHT + padding + row * (squareSize + padding);

            // Highlight selected tower
            if (i == selectedTowerIndex) {
                context.setFill(Color.rgb(70, 130, 180, 0.7)); // Soft blue highlight
                context.fillRoundRect(x - 4, y - 4, squareSize + 8, squareSize + 8, 18, 18);
            }

            // Draw slot background
            context.setFill(Color.DARKGRAY);
            context.fillRoundRect(x, y, squareSize, squareSize, 16, 16);

            // Draw border
            context.setStroke(Color.WHITE);
            context.setLineWidth(2);
            context.strokeRoundRect(x, y, squareSize, squareSize, 16, 16);

            // Draw tower image
            context.drawImage(
                    towerTypes.get(i).getConfig().getBaseModel().getImage(),
                    x + 6, y + 6, squareSize - 12, squareSize - 12);

            // Grey out if not enough money
            int cost = towerTypes.get(i).getConfig().getPrice();
            if (hud.getMoney() < cost) {
                context.setFill(Color.rgb(0, 0, 0, 0.5));
                context.fillRoundRect(x, y, squareSize, squareSize, 16, 16);
            }

            // Draw semi-transparent background for text at bottom of slot
            double textBgHeight = 36;
            context.setFill(Color.rgb(30, 30, 30, 0.7));
            context.fillRoundRect(x, y + squareSize - textBgHeight, squareSize, textBgHeight, 0, 0);

            // Draw tower name and price inside the slot
            String name = towerTypes.get(i).getConfig().getName();
            String price = "$" + cost;
            context.setFill(Color.WHITE);
            context.setFont(new javafx.scene.text.Font("Arial", 16));
            context.setTextAlign(TextAlignment.CENTER);
            context.setTextBaseline(VPos.TOP);
            context.fillText(name, x + squareSize / 2, y + squareSize - textBgHeight + 4);
            context.setFill(Color.LIGHTGREEN);
            context.setFont(new javafx.scene.text.Font("Arial", 14));
            context.fillText(price, x + squareSize / 2, y + squareSize - textBgHeight + 20);
        }
        context.setTextAlign(TextAlignment.LEFT);
        context.setTextBaseline(VPos.BASELINE);
    }

    public void renderUpgrades(GraphicsContext context, AbstractTower selectedTower) {
        // Draw semi-transparent dark background
        context.setFill(Color.rgb(30, 30, 30, 0.85));
        context.fillRect(Game.VIRTUAL_WIDTH - WIDTH, HUD_HEIGHT, WIDTH, HEIGHT);

        double padding = Game.VIRTUAL_WIDTH * PADDING_RATIO;
        double availableWidth = WIDTH - (COLUMNS + 1) * padding;
        double squareSize = availableWidth / COLUMNS;

        // Get upgrades for both paths
        TowerType.Upgrade[] upgrades1 = selectedTower.getConfig().getUpgrades1();
        TowerType.Upgrade[] upgrades2 = selectedTower.getConfig().getUpgrades2();
        int maxUpgrades = Math.max(upgrades1.length, upgrades2.length);

        // Draw upgrade slots
        for (int row = 0; row < maxUpgrades; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                TowerType.Upgrade upgrade = (col == 0) ? (row < upgrades1.length ? upgrades1[row] : null)
                        : (row < upgrades2.length ? upgrades2[row] : null);

                double x = Game.VIRTUAL_WIDTH - WIDTH + padding + col * (squareSize + padding);
                double y = HUD_HEIGHT + padding + row * (squareSize + padding);

                // Draw slot background
                context.setFill(Color.DARKGRAY);
                context.fillRoundRect(x, y, squareSize, squareSize, 16, 16);

                // Draw border
                context.setStroke(Color.WHITE);
                context.setLineWidth(2);
                context.strokeRoundRect(x, y, squareSize, squareSize, 16, 16);

                if (upgrade != null) {
                    // Grey out if not enough money
                    if (hud.getMoney() < upgrade.price()) {
                        context.setFill(Color.rgb(0, 0, 0, 0.5));
                        context.fillRoundRect(x, y, squareSize, squareSize, 16, 16);
                    }

                    // Draw semi-transparent background for text at bottom of slot
                    double textBgHeight = 36;
                    context.setFill(Color.rgb(30, 30, 30, 0.7));
                    context.fillRoundRect(x, y + squareSize - textBgHeight, squareSize, textBgHeight, 0, 0);

                    // Draw upgrade name
                    context.setFill(Color.WHITE);
                    context.setFont(new javafx.scene.text.Font("Arial", 16));
                    context.setTextAlign(TextAlignment.CENTER);
                    context.setTextBaseline(VPos.TOP);
                    context.fillText(upgrade.name(), x + squareSize / 2, y + 6);

                    // Draw upgrade price
                    context.setFill(Color.LIGHTGREEN);
                    context.setFont(new javafx.scene.text.Font("Arial", 14));
                    context.fillText("$" + upgrade.price(), x + squareSize / 2, y + squareSize - textBgHeight + 4);

                    // Draw upgrade info/description
                    context.setFill(Color.LIGHTGRAY);
                    context.setFont(new javafx.scene.text.Font("Arial", 12));
                    String info = upgrade.info();
                    double infoY = y + squareSize - textBgHeight + 20;
                    context.fillText(info, x + squareSize / 2, infoY);
                }
            }
        }
        context.setTextAlign(TextAlignment.LEFT);
        context.setTextBaseline(VPos.BASELINE);
    }

    public void handleClick(double mouseX, double mouseY) {
        double shopX = Game.VIRTUAL_WIDTH - WIDTH;
        if (mouseX < shopX || mouseX > Game.VIRTUAL_WIDTH || mouseY < HUD_HEIGHT || mouseY > HUD_HEIGHT + HEIGHT
                || !isOpen) {
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
            double y = HUD_HEIGHT + padding + 50 + row * (squareSize + padding);

            if (mouseX >= x && mouseX <= x + squareSize && mouseY >= y && mouseY <= y + squareSize) {
                selectedTowerIndex = i;
                return;
            }
        }
    }

}
