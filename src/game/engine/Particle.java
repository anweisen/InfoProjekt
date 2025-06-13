package game.engine;

import game.GameState;
import javafx.scene.canvas.GraphicsContext;

public abstract class Particle extends GameObject {

    protected final double lifetimeSeconds;
    protected final Timing timing;

    protected double progress = 0;

    public Particle(GameState state, double x, double y, double size, Timing timing, double lifetimeSeconds) {
        this(state, x, y, size, size, timing, lifetimeSeconds);
    }

    public Particle(GameState state, double x, double y, double width, double height, Timing timing, double lifetimeSeconds) {
        super(state, x, y, width, height);
        this.lifetimeSeconds = lifetimeSeconds;
        this.timing = timing;
    }

    @Override
    public void update(double deltaTime) {
        progress += deltaTime / lifetimeSeconds;

        if (progress >= 1) {
            this.progress = 1; // clamp
            this.markForRemoval();
        }
    }

    @Override
    public void render(GraphicsContext graphics) {
        render(graphics, timing.translate(progress));
    }

    public double getProgress() {
        return progress;
    }

    public double getLifetimeSeconds() {
        return lifetimeSeconds;
    }

    public Timing getTiming() {
        return timing;
    }

    public abstract void render(GraphicsContext graphics, double time);

    @FunctionalInterface
    public interface Timing {
        // https://easings.net/
        Timing LINEAR = time -> time;
        Timing EASE_OUT_QUAD = time -> 1 - Math.pow(1 - time, 2);
        Timing EASE_OUT_CUBIC = time -> 1 - Math.pow(1 - time, 3);
        Timing EASE_IN_QUAD = time -> Math.pow(time, 2);
        Timing EASE_IN_CUBIC = time -> Math.pow(time, 3);
        Timing EASE_IN_OUT_QUAD = time -> time < 0.5 ? 2 * Math.pow(time, 2) : 1 - Math.pow(-2 * time + 2, 2) / 2;

        double translate(double time);
    }

    public static class Image extends Particle {
        private final Model model;

        public Image(GameState state, double x, double y, Model model, Timing timing, double lifetimeSeconds) {
            super(state, x, y, model.getWidth(), model.getHeight(), timing, lifetimeSeconds);
            this.model = model;
        }

        @Override
        public void render(GraphicsContext graphics, double time) {
            model.renderScaled(graphics, x, y, time);
        }
    }

}
