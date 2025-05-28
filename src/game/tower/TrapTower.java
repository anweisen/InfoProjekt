package game.tower;

import javafx.scene.canvas.GraphicsContext;

public class TrapTower extends AbstractTower {
    // A spike thrower that sets a trap that slows down or damages enemies
    double price = 100;

    public TrapTower(double x, double y) {
        super(x, y, 100, 100);

    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public double getShootInterval() {
        return 0;
    }

    @Override
    public boolean shoot() {
        return true;
    }

    @Override
    public void render(GraphicsContext graphics) {

    }
}