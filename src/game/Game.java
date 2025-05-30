package game;

import game.engine.State;
import game.map.Map;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Affine;
import javafx.stage.Stage;

public class Game extends Application {

    public static final int
        VIRTUAL_WIDTH = 1600,
        VIRTUAL_HEIGHT = 900;

    private State currentState;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        currentState = new MenuState(this);
//        currentState = new GameState(this, new Map());
    }

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());

        // TODO: 16:9?
        RescaleListener rescaleListener = new RescaleListener(graphics);
        canvas.widthProperty().addListener(rescaleListener);
        canvas.heightProperty().addListener(rescaleListener);

        // setup window
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        scene.setOnMouseClicked(event -> {
            double scaleX = canvas.getWidth() / VIRTUAL_WIDTH;
            double scaleY = canvas.getHeight() / VIRTUAL_HEIGHT;
            double x = event.getX() / scaleX;
            double y = event.getY() / scaleY;
            currentState.handleClick(x, y);
        });

        GameLoopTimer loop = new GameLoopTimer(graphics);
        loop.start();
    }

    public void setState(State newState) {
        if (newState == null) throw new IllegalArgumentException("State cannot be null");

        currentState.dispose();
        currentState = newState;
    }

    public static class RescaleListener implements ChangeListener<Number> {
        private final GraphicsContext graphics;

        public RescaleListener(GraphicsContext graphics) {
            this.graphics = graphics;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            double scaleX = graphics.getCanvas().getWidth() / VIRTUAL_WIDTH;
            double scaleY = graphics.getCanvas().getHeight() / VIRTUAL_HEIGHT;

            Affine affine = new Affine();
            affine.appendScale(scaleX, scaleY);
            graphics.setTransform(affine);
        }
    }

    public class GameLoopTimer extends AnimationTimer {
        private final GraphicsContext graphics;
        private long lastTime = System.nanoTime();

        private int frames = 0;
        private long lastFpsTime = System.nanoTime();

        public GameLoopTimer(GraphicsContext graphics) {
            this.graphics = graphics;
        }

        @Override
        public void handle(long now) {
            // Provisorischer FPS-Zähler zur Performanceüberwachung
            frames++;
            if (now - lastFpsTime >= 1_000_000_000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                lastFpsTime = now;
            }

            double deltaTime = Math.min(1, (now - lastTime) / 1_000_000_000.0);
            lastTime = now;

            currentState.update(deltaTime);
            currentState.render(graphics);
        }
    }

}
