package game;

import game.engine.State;
import game.map.Map;
import game.tower.TowerType;
import game.tower.impl.AuraTower;
import game.tower.impl.CanonTower;
import game.tower.impl.InfernoTower;
import game.tower.impl.LaserTower;
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
import java.util.ArrayList;
import java.util.List;

public class Game extends Application {

    /**
     * Die interne (virtuelle) Größe des Spielfeldes,
     * um eine gleichbleibende Spielfeldgröße auf verschiedenen Bildschirmen zu
     * ermöglichen.
     */
    public static final int
        VIRTUAL_WIDTH = 1600,
        VIRTUAL_HEIGHT = 900;

    private final List<Map> maps = new ArrayList<>();
    private final List<TowerType> towers = new ArrayList<>();
    private State currentState;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        currentState = new MenuState(this);

        // Lade die verschiedenen Maps und Turmtypen aus den JSON-Konfigurationen (/assets/conf/)
        registerMap(Map.loadMap("map1.json"));
        registerTower(TowerType.Config.load("canon.json"), CanonTower::new);
        registerTower(TowerType.Config.load("AuraTower.json"), AuraTower::new);
        registerTower(TowerType.Config.load("LaserTower.json"), LaserTower::new);
        registerTower(TowerType.Config.load("InfernoTower.json"), InfernoTower::new);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        // Canvas, also das gerenderte Spiel, an die Fenstergröße binden
        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());
        // TODO: 16:9?
        RescaleListener rescaleListener = new RescaleListener(graphics);
        canvas.widthProperty().addListener(rescaleListener);
        canvas.heightProperty().addListener(rescaleListener);

        // Fenster
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        // Maus Klicks
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

    private void registerMap(Map map) {
        maps.add(map);
    }

    private void registerTower(TowerType.Config config, TowerType.TowerConstructor constructor) {
        towers.add(new TowerType(config, constructor));
    }

    public void setState(State newState) {
        if (newState == null)
            throw new IllegalArgumentException("State cannot be null");

        currentState.dispose();
        currentState = newState;
    }

    public List<Map> getMaps() {
        return maps;
    }

    public List<TowerType> getTowerTypes() {
        return towers;
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
            if ((now - lastTime) < 1_000_000_000 / 1000)
                return;

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
