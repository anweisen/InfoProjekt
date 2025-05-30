package game.tower;

import javafx.scene.canvas.GraphicsContext;

public class LaserTower extends AbstractTower {
    // A tower that shoots a fast laser projectile, dealing damage over time to
    // enemies
    double price = 100;

    public LaserTower(double x, double y) {
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