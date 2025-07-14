package game.shop;

import game.Game;
import game.engine.Particle;
import game.engine.assets.Model;
import game.engine.assets.Sound;
import game.hud.Hud;
import game.state.GameState;
import game.tower.AbstractTower;
import game.tower.TowerTargetSelector;
import game.tower.TowerType;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Shop {

    private static final Model buttonModel = Model.loadModelWith("menu", "shop.png", 56, 56);
    private static final Model coinModel = Model.loadModelWith("menu", "coin.png", 18, 18);

    // Style
    private static final double WIDTH = Game.VIRTUAL_WIDTH * 0.2;
    private static final int SHOP_COLUMNS = 2;
    private static final double shopPadding = WIDTH * 0.05;
    private static final double buttonPadding = 15;
    private static final double upgradeCardWidth = WIDTH * 0.58;
    private static final double upgradeCardHeight = WIDTH * 0.68;
    private static final double upgradeCardOffsetY = Game.VIRTUAL_HEIGHT * 0.33;
    private static final double upgradeCardGap = 36;
    private static final double sellButtonHeight = 50;
    private static final double sellButtonPosY = Game.VIRTUAL_HEIGHT - sellButtonHeight - 50;
    private static final double targetButtonHeight = 36;
    private static final double targetButtonPosY = upgradeCardOffsetY - targetButtonHeight - 28;

    private final GameState state;

    private int selectedTowerIndex = 0; // Index of the currently selected tower in the shop

    // Animation
    private boolean opened = true;
    private double openAnimationProgress = 0;
    private double fadeinAnimationProgress = 1;
    private AbstractTower lastSelectedTower; // to track the last selected tower for fade-in animation

    public Shop(GameState state) {
        this.state = state;
    }

    public boolean handleClick(double x, double y) {
        // Button
        if ((openAnimationProgress == 0 || openAnimationProgress == 1) &&
            isButtonClicked(x, y, Game.VIRTUAL_WIDTH - (WIDTH * openAnimationProgress) - buttonPadding - buttonModel.getWidth(), Game.VIRTUAL_HEIGHT - buttonPadding - buttonModel.getHeight(), buttonModel.getWidth(), buttonModel.getHeight())) {
            if (shouldBeOpened()) {
                state.setSelectedTower(null);
                opened = false;
            } else {
                opened = true;
            }
            Sound.SWOOSH.playSound();
            return true;
        }

        // Upgrade, Sell, Target
        if (state.getSelectedTower() != null && openAnimationProgress == 1) {
            if (isButtonClicked(x, y, Game.VIRTUAL_WIDTH - WIDTH / 2 - upgradeCardWidth / 2, sellButtonPosY, upgradeCardWidth, sellButtonHeight)) {
                state.addMoney(calculateSellingPrice(state.getSelectedTower()));
                state.getSelectedTower().markForRemoval();
                state.setSelectedTower(null);
                Sound.BUY.playSound();
                return true;
            }

            if (isButtonClicked(x, y, Game.VIRTUAL_WIDTH - WIDTH / 2 - upgradeCardWidth / 2, targetButtonPosY, upgradeCardWidth, targetButtonHeight)) {
                TowerTargetSelector.cycleTargetSelector(state.getSelectedTower());
                Sound.CLICK.playSound();
                return true;
            }

            for (int i = 0; i < 2; i++) {
                double upgradeY = upgradeCardOffsetY + i * (upgradeCardHeight + upgradeCardGap);
                if (isButtonClicked(x, y, Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2, upgradeY, upgradeCardWidth, upgradeCardHeight)) {
                    TowerType.Upgrade[] upgrades = state.getSelectedTower().getConfig().getUpgradesForTree(i == 0);
                    if (state.getSelectedTower().getLevel() >= upgrades.length) continue;
                    TowerType.Upgrade upgrade = upgrades[state.getSelectedTower().getLevel()];
                    boolean blocked = isUpgradeTreeBlocked(i, state.getSelectedTower());
                    if (!blocked && state.getMoney() >= upgrade.price()) {
                        state.getSelectedTower().upgradeLevel();
                        state.getSelectedTower().setUpgradeTree(i == 0);
                        state.removeMoney(upgrade.price());
                        Sound.BUY.playSound();
                        return true;
                    }
                }
            }
        }

        // Shop
        if (opened && openAnimationProgress == 1) {
            double availableWidth = WIDTH - (SHOP_COLUMNS + 1) * shopPadding;
            double squareSize = availableWidth / SHOP_COLUMNS;

            for (int i = 0; i < state.getGame().getTowerTypes().size(); i++) {
                int row = i / SHOP_COLUMNS;
                int col = i % SHOP_COLUMNS;
                double xPos = Game.VIRTUAL_WIDTH - WIDTH + shopPadding + col * (squareSize + shopPadding);
                double yPos = shopPadding + row * (squareSize + shopPadding);

                if (isButtonClicked(x, y, xPos, yPos, squareSize, squareSize)) {
                    selectedTowerIndex = i;
                    Sound.CLICK.playSound();
                    return true;
                }
            }
        }

        if (openAnimationProgress > 0) {
            double width = WIDTH * openAnimationProgress;
            return x >= (Game.VIRTUAL_WIDTH - width);
        }

        return false;
    }

    public boolean handlePlacementClick(double x, double y) {
        TowerType towerType = state.getGame().getTowerTypes().get(selectedTowerIndex);
        if (state.getMap().getCanPlace((int) x, (int) y) && towerType.getConfig().getPrice() < state.getMoney()) {
            state.spawnTower(towerType, x, y);
            state.removeMoney(towerType.getConfig().getPrice());
            Sound.BUY.playSound();
            return true;
        }
        return false;
    }

    public boolean shouldBeOpened() {
        return opened || state.getSelectedTower() != null;
    }

    public void update(double deltaTime) {
        // Wurde der Turm gewechselt (einer/keiner ausgewählt), muss eine weitere Animation abgespielt werden
        if ((opened || openAnimationProgress > 0) && (state.getSelectedTower() != lastSelectedTower)) {
            lastSelectedTower = state.getSelectedTower();
            fadeinAnimationProgress = 0; // Reset fade-in animation when a new tower is selected
        }

        fadeinAnimationProgress += deltaTime * 3; // Fade-in speed
        if (fadeinAnimationProgress > 1) {
            fadeinAnimationProgress = 1;
        }

        if (shouldBeOpened()) {
            openAnimationProgress += deltaTime * 2; // Animation speed
            if (openAnimationProgress > 1) {
                openAnimationProgress = 1;
            }
        } else {
            openAnimationProgress -= deltaTime * 2; // Animation speed
            if (openAnimationProgress < 0) {
                openAnimationProgress = 0;
            }
        }
    }

    public void render(GraphicsContext graphics) {
        double progress = Particle.Timing.EASE_OUT_QUAD.translate(openAnimationProgress);
        graphics.save();
        graphics.translate(WIDTH - WIDTH * progress, 0);

        renderButton(graphics);

        graphics.setGlobalAlpha(progress);
        graphics.setFill(Color.rgb(30, 30, 30, .85));
        graphics.fillRect(Game.VIRTUAL_WIDTH - WIDTH, 0, WIDTH, Game.VIRTUAL_HEIGHT);

        graphics.setGlobalAlpha(fadeinAnimationProgress * progress);
        graphics.translate(0, -10 * (1 - Particle.Timing.EASE_OUT_QUAD.translate(fadeinAnimationProgress)));

        if (state.getSelectedTower() == null) {
            renderShop(graphics);
        } else {
            renderUpgrades(graphics, state.getSelectedTower());
        }

        graphics.restore();
    }

    private void renderButton(GraphicsContext graphics) {
        graphics.setFill(Color.RED);
        graphics.drawImage(
            buttonModel.getImage(),
            Game.VIRTUAL_WIDTH - WIDTH - buttonModel.getWidth() - buttonPadding,
            Game.VIRTUAL_HEIGHT - buttonModel.getHeight() - buttonPadding,
            buttonModel.getWidth(), buttonModel.getHeight()
        );
    }

    private void renderShop(GraphicsContext graphics) {
        double availableWidth = WIDTH - (SHOP_COLUMNS + 1) * shopPadding;
        double squareSize = availableWidth / SHOP_COLUMNS;
        double modelPadding = 20;

        // Draw tower slots
        for (int i = 0; i < state.getGame().getTowerTypes().size(); i++) {
            int row = i / SHOP_COLUMNS;
            int col = i % SHOP_COLUMNS;
            double x = Game.VIRTUAL_WIDTH - WIDTH + shopPadding + col * (squareSize + shopPadding);
            double y = shopPadding + row * (squareSize + shopPadding);

            // Draw slot background
            graphics.setFill(Color.rgb(30, 30, 30, 0.5));
            graphics.fillRoundRect(x, y, squareSize, squareSize, 16, 16);

            // Draw tower image
            graphics.drawImage(state.getGame().getTowerTypes().get(i).getConfig().getBaseModel().getImage(), x + modelPadding / 2, y + modelPadding / 2, squareSize - modelPadding, squareSize - modelPadding);

            // Grey out if not enough money
            int price = state.getGame().getTowerTypes().get(i).getConfig().getPrice();
            if (state.getMoney() < price) {
                graphics.setFill(Color.rgb(0, 0, 0, 0.5));
                graphics.fillRoundRect(x, y, squareSize, squareSize, 16, 16);
            }

            // Draw semi-transparent background for text at bottom of slot
            double textBgHeight = 36;
            graphics.setFill(Color.rgb(30, 30, 30, 0.7));
            graphics.fillRoundRect(x, y + squareSize - textBgHeight, squareSize, textBgHeight, 0, 0);

            // Draw tower name and price inside the slot
            String name = state.getGame().getTowerTypes().get(i).getConfig().getName();
            String formattedPrice = Hud.DECIMAL_FORMAT.format(price);
            graphics.setFill(Color.WHITE);
            graphics.setFont(Font.font("Calibri", FontWeight.MEDIUM, 16));
            graphics.setTextBaseline(VPos.CENTER);
            graphics.setTextAlign(TextAlignment.CENTER);
            graphics.fillText(name, x + squareSize / 2, y + squareSize - textBgHeight + 10);
            graphics.setFont(Font.font("Calibri", FontWeight.BOLD, 18));
            if (state.getMoney() < price) graphics.setFill(Color.RED);
            graphics.fillText(formattedPrice, x + squareSize / 2 + coinModel.getWidth() / 2d, y + squareSize - textBgHeight + 26);
            coinModel.render(graphics, x + squareSize / 2 - approximateTextWidth(formattedPrice, graphics) / 2, y + squareSize - textBgHeight + 24);

            // Draw (highlighted) border
            if (i == selectedTowerIndex) graphics.setStroke(Color.rgb(180, 180, 185, 1));
            else graphics.setStroke(Color.rgb(100, 100, 105, 1));
            graphics.setLineWidth(3);
            graphics.strokeRoundRect(x, y, squareSize, squareSize, 18, 18);
        }
        graphics.setTextAlign(TextAlignment.LEFT);
        graphics.setTextBaseline(VPos.BASELINE);
    }

    private void renderUpgrades(GraphicsContext graphics, AbstractTower selectedTower) {
        // Tower Model
        double modelSize = 80;
        double modelY = Game.VIRTUAL_HEIGHT * 0.075 + modelSize / 2;
        graphics.setFill(Color.rgb(30, 30, 30, .5));
        double modelCardSize = modelSize + 16;
        graphics.fillRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - modelCardSize / 2, modelY - modelCardSize / 2, modelCardSize, modelCardSize, 20, 20);
        Model.render(graphics, selectedTower.getConfig().getBaseModel().getImage(), Game.VIRTUAL_WIDTH - WIDTH / 2d, modelY, modelSize, modelSize);

        // Name & Info
        graphics.setFill(Color.WHITE);
        graphics.setFont(Font.font("Calibri", FontWeight.BOLD, 28));
        graphics.setTextAlign(TextAlignment.CENTER);
        graphics.setTextBaseline(VPos.CENTER);
        double titleY = modelY + (modelCardSize - modelSize) + 40 + 14;
        graphics.fillText(selectedTower.getConfig().getName(), Game.VIRTUAL_WIDTH - WIDTH / 2d, titleY);
        graphics.setFill(Color.GRAY);
        double infoY = titleY + 14 + 8 + 7;
        graphics.setFont(Font.font("Calibri", FontWeight.MEDIUM, 14));
        graphics.fillText(selectedTower.getConfig().getInfo(), Game.VIRTUAL_WIDTH - WIDTH / 2d, infoY);

        // Target Selector
        if (selectedTower.getPossibleTargetSelectors() != null) {
            graphics.setFill(Color.rgb(30, 30, 30, .5));
            graphics.fillRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2d, targetButtonPosY, upgradeCardWidth, targetButtonHeight, 20, 20);
            graphics.setFill(Color.WHITE);
            graphics.setFont(Font.font("Calibri", FontWeight.SEMI_BOLD, 16));
            graphics.fillText(selectedTower.getTargetSelector().getDisplayName(), Game.VIRTUAL_WIDTH - WIDTH / 2d, targetButtonPosY + targetButtonHeight / 2 + 1);
            graphics.setLineWidth(1.5);
            graphics.setStroke(Color.rgb(180, 180, 185, 1));
            graphics.strokeRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2, targetButtonPosY, upgradeCardWidth, targetButtonHeight, 20, 20);
        }

        // Verkaufen
        graphics.setFill(Color.rgb(240, 54, 54));
        graphics.fillRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2d, sellButtonPosY, upgradeCardWidth, sellButtonHeight, 20, 20);
        graphics.setFont(Font.font("Calibri", FontWeight.MEDIUM, 18));
        graphics.setFill(Color.WHITE);
        graphics.fillText("Verkaufen", Game.VIRTUAL_WIDTH - WIDTH / 2d, sellButtonPosY + 15);
        graphics.setFont(Font.font("Calibri", FontWeight.BOLD, 24));
        String formattedSellingPrice = Hud.DECIMAL_FORMAT.format(calculateSellingPrice(selectedTower));
        graphics.fillText(formattedSellingPrice, Game.VIRTUAL_WIDTH - WIDTH / 2d + coinModel.getWidth() / 2d, sellButtonPosY + 32 + 1);
        coinModel.render(graphics, Game.VIRTUAL_WIDTH - WIDTH / 2d - approximateTextWidth(formattedSellingPrice, graphics) / 2, sellButtonPosY + 32);

        double upgradeY = upgradeCardOffsetY;
        // Upgrade Bäume
        for (int i = 0; i < 2; i++) {
            TowerType.Upgrade[] upgrades = selectedTower.getConfig().getUpgradesForTree(i == 0);
            boolean blocked = isUpgradeTreeBlocked(i, selectedTower);
            int level = blocked ? 0 : selectedTower.getLevel();
            boolean owned = level >= upgrades.length;
            TowerType.Upgrade upgrade = upgrades[Math.min(level, upgrades.length - 1)]; // (level + 1) - 1
            Model model = selectedTower.getConfig().getModelForLevel(level + 1, i == 0);

            // Background
            graphics.setFill(Color.rgb(30, 30, 30, .5));
            graphics.fillRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2, upgradeY, upgradeCardWidth, upgradeCardHeight, 20, 20);

            // Name, Preis
            graphics.setFont(Font.font("Calibri", FontWeight.MEDIUM, 16));
            graphics.setFill(Color.WHITE);
            graphics.fillText(upgrade.name(), Game.VIRTUAL_WIDTH - WIDTH / 2d, upgradeY + upgradeCardHeight - 52);
            graphics.setFont(Font.font("Calibri", FontWeight.BOLD, 18));
            graphics.setFill(Color.WHITE);
            if (owned) {
                graphics.setFill(Color.rgb(100, 200, 100, 1));
                graphics.fillText("max. Level", Game.VIRTUAL_WIDTH - WIDTH / 2d, upgradeY + upgradeCardHeight - 36 + 1);
            } else {
                String formattedPrice = Hud.DECIMAL_FORMAT.format(upgrade.price());
                if (upgrade.price() > state.getMoney()) graphics.setFill(Color.RED);
                graphics.fillText(formattedPrice, Game.VIRTUAL_WIDTH - WIDTH / 2d + coinModel.getWidth() / 2d, upgradeY + upgradeCardHeight - 36 + 1);
                coinModel.render(graphics, Game.VIRTUAL_WIDTH - WIDTH / 2d - approximateTextWidth(formattedPrice, graphics) / 2, upgradeY + upgradeCardHeight - 36);
            }

            // Info
            graphics.setFont(Font.font("Calibri", FontWeight.NORMAL, 14));
            graphics.setFill(Color.GRAY);
            String[] splitText = splitText(upgrade.info(), graphics, upgradeCardWidth - 5);
            if (splitText.length > 3) {
                splitText = new String[]{splitText[0], splitText[1], "..."};
            }
            for (int j = 0; j < splitText.length; j++) {
                graphics.fillText(splitText[j], Game.VIRTUAL_WIDTH - WIDTH / 2d, upgradeY + 10 + 14 + j * 14);
            }

            // Upgrade Level Indicator
            for (int j = 0; j < upgrades.length; j++) {
                graphics.setFill(level > j ? Color.rgb(100, 200, 100, 1) : Color.rgb(100, 100, 105, 0.5));
                graphics.fillRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2 + 10 + j * (upgradeCardWidth / upgrades.length), upgradeY + upgradeCardHeight - 16,
                    upgradeCardWidth / upgrades.length - 20, 6, 5, 5);
            }

            // Upgraded Model
            double iconSize = upgradeCardWidth * 0.4;
            graphics.drawImage(model.getImage(), Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2 + (upgradeCardWidth - iconSize) / 2, upgradeY + (upgradeCardHeight - iconSize) / 2, iconSize, iconSize);

            // Gray out blocked upgrades
            if (blocked) {
                graphics.setFill(Color.rgb(30, 30, 30, .66));
                graphics.fillRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2, upgradeY, upgradeCardWidth, upgradeCardHeight, 20, 20);
            }

            // Border
            graphics.setLineWidth(3);
            if (!blocked && level > 0) graphics.setStroke(Color.rgb(180, 180, 185, 1));
            else graphics.setStroke(Color.rgb(100, 100, 105, 1));
            graphics.strokeRoundRect(Game.VIRTUAL_WIDTH - WIDTH / 2d - upgradeCardWidth / 2, upgradeY, upgradeCardWidth, upgradeCardHeight, 20, 20);

            upgradeY += upgradeCardHeight + upgradeCardGap;
        }
    }

    public int calculateSellingPrice(AbstractTower tower) {
        return (int) (0.6d * tower.getConfig().getTotalPrice(tower.getLevel(), tower.getUpgradeTree()));
    }

    public static double approximateTextWidth(String text, GraphicsContext context) {
        return context.getFont().getSize() * text.length() * 0.6; // Approximate width calculation
    }

    public static String[] splitText(String text, GraphicsContext context, double maxWidth) {
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            double width = approximateTextWidth(testLine, context);

            if (width > maxWidth) {
                if (!currentLine.isEmpty()) {
                    result.append(currentLine).append("\n");
                }
                currentLine.setLength(0);
                currentLine.append(word);
            } else {
                currentLine.append(currentLine.isEmpty() ? word : " " + word);
            }
        }

        if (!currentLine.isEmpty()) {
            result.append(currentLine);
        }

        return result.toString().split("\n");
    }

    public static boolean isUpgradeTreeBlocked(int nthTree, AbstractTower tower) {
        return tower.getLevel() > 0 && (tower.getUpgradeTree() ? nthTree != 0 : nthTree != 1);
    }

    // ButtonX/Y: TopLeft Corner of Button
    public static boolean isButtonClicked(double clickX, double clickY, double buttonX, double buttonY, double buttonWidth, double buttonHeight) {
        return clickX >= buttonX && clickX <= buttonX + buttonWidth
            && clickY >= buttonY && clickY <= buttonY + buttonHeight;
    }
}
