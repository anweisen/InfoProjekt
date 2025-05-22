package game.tower;

import javafx.scene.canvas.GraphicsContext;

public class AuraTower extends AbstractTower {

    public AuraTower(double x, double y) {
        super(x, y, 100, 100);
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
