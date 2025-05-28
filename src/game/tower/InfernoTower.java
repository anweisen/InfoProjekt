package game.tower;

import javafx.scene.canvas.GraphicsContext;

public class InfernoTower extends AbstractTower {
    // A tower that shoots a powerful beam of fire, dealing increasing damage over
    // time to enemies
    double price = 100;

    public InfernoTower(double x, double y) {
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
