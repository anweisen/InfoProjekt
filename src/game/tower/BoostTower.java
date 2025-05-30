
package game.tower;

import javafx.scene.canvas.GraphicsContext;

public class BoostTower extends AbstractTower {
    // A tower that boosts the damage of all placed towers
    double price = 100;

    public BoostTower(double x, double y) {
        super(x, y, 100, 100);
    }

    @Override
    public double getShootInterval() {
        return 0;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public boolean shoot() {
        return true;
    }

    @Override
    public void render(GraphicsContext graphics) {

    }
}