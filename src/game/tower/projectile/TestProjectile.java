package game.tower.projectile;

import game.GameState;
import game.engine.GameObject;
import game.engine.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TestProjectile extends GameObject {

    private static final Model model = Model.loadModelWith("projectile", "kugel.png", 32, 32);

    // evtl. auch ein zielsuchendes Projektil?
    private final double targetX, targetY;

    public TestProjectile(GameState state, double x, double y, double targetX, double targetY) {
        super(state, x, y, model.getWidth(), model.getHeight());
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void update(double deltaTime) {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 1) {
            markForRemoval();
            return;
        }
        double speed = 1000;
        x += (dx / distance) * speed * deltaTime;
        y += (dy / distance) * speed * deltaTime;
    }

    @Override
    public void render(GraphicsContext graphics) {
        model.render(graphics, x, y);

        graphics.setFill(Color.RED);
        graphics.fillOval(targetX - 5, targetY - 5, 10, 10);
    }
}
