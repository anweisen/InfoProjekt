package game.engine;

import game.GameState;
import javafx.scene.canvas.GraphicsContext;

public abstract class GameObject {

    protected final GameState state;

    protected double x, y;
    protected double width, height;

    protected boolean markedForRemoval = false;

    public GameObject(GameState state, double x, double y, double width, double height) {
        this.state = state;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @see State#update(double)
     */
    public abstract void update(double deltaTime);

    public abstract void render(GraphicsContext graphics);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public final void markForRemoval() {
        markedForRemoval = true;
    }

    public final boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public boolean containsPoint(double x, double y) {
        return x >= (this.x - width / 2) && x <= (this.x + width / 2)
            && y >= (this.y - height / 2) && y <= (this.y + height / 2);
    }

    public double distanceTo(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        return Math.sqrt(dx * dx + dy * dy); // Pythagoras
    }

    public double distanceTo(GameObject another) {
        return distanceTo(another.getX(), another.getY());
    }

    public double angleInDirection(double targetX, double targetY) {
        return angleFromTo(x, y, targetX, targetY);
    }

    public double angleInDirection(GameObject another) {
        return angleInDirection(another.getX(), another.getY());
    }

    public static double angleFromTo(double fromX, double fromY, double toX, double toY) {
        double dx = toX - fromX;
        double dy = toY - fromY;
        return Math.toDegrees(Math.atan2(dy, dx)) + 90; // Quelle: Copilot
    }
}
