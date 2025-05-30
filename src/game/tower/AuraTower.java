package game.tower;

import javafx.scene.canvas.GraphicsContext;

public class AuraTower extends AbstractTower {
    // A tower that emits a powerful aura, dealing the damage to nearby enemies
    double price = 100;

    public AuraTower(double x, double y) {
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
