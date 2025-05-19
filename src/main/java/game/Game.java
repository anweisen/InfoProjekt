package game;

import game.engine.Actor;
import game.engine.GameObject;
import game.engine.Renderer;
import game.logic.tower.ExampleTower;
import game.util.DeferredList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Game extends Application {

  private static Game instance;

  public static Game getInstance() {
    return instance;
  }

  public static final int
    VIRTUAL_WIDTH = 1600,
    VIRTUAL_HEIGHT = 900;

  // CopyOnWriteArrayList vs DeferredList ? (perf)
  private final DeferredList<Actor> actors = new DeferredList<>();
  private final DeferredList<Renderer> renderers = new DeferredList<>();

  public static void main(String[] args) {
    Application.launch(Game.class, args);
  }

  @Override
  public void init() throws Exception {
    instance = this;
  }

  @Override
  public void start(Stage stage) throws Exception {
    Canvas canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Pane root = new Pane(canvas);

    canvas.widthProperty().bind(stage.widthProperty());
    canvas.heightProperty().bind(stage.heightProperty());

    RescaleListener rescaleListener = new RescaleListener(context);
    canvas.widthProperty().addListener(rescaleListener);
    canvas.heightProperty().addListener(rescaleListener);

    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Q12 Spiel");
    stage.setFullScreenExitHint("");
    stage.setFullScreen(true);
    stage.show();



    // TEST
    canvas.setOnMouseClicked(event -> {
      double scaleX = canvas.getWidth() / VIRTUAL_WIDTH;
      double scaleY = canvas.getHeight() / VIRTUAL_HEIGHT;
      double virtualX = event.getX() / scaleX;
      double virtualY = event.getY() / scaleY;

      registerGameObject(new ExampleTower(virtualX, virtualY));
    });
    canvas.setOnTouchPressed(event -> {
      double scaleX = canvas.getWidth() / VIRTUAL_WIDTH;
      double scaleY = canvas.getHeight() / VIRTUAL_HEIGHT;
      double virtualX = event.getTouchPoint().getX() / scaleX;
      double virtualY = event.getTouchPoint().getY() / scaleY;

      registerGameObject(new ExampleTower(virtualX, virtualY));
    });
    track = new Image(getClass().getResourceAsStream("/assets/track.png"));




    new GameLoop(context).start();
  }

  Image track;

  public void registerGameObject(GameObject gameObject) {
    this.actors.add(gameObject);
    this.renderers.add(gameObject);
  }

  public void unregisterGameObject(GameObject gameObject) {
    this.actors.remove(gameObject);
    this.renderers.remove(gameObject);
  }

  private void updateAndRender(double delta, GraphicsContext context) {
    for (Actor actor : actors) {
      actor.update(delta);
    }

    actors.applyPendingChanges();
    renderers.applyPendingChanges();


    context.clearRect(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    context.drawImage(track, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

    for (Renderer renderer : renderers) {
      renderer.render(context);
    }
  }

  public class GameLoop extends AnimationTimer {

    private final GraphicsContext context;

    private long lastUpdate;
    private long secondMark;
    private long frameCounter;

    public GameLoop(GraphicsContext context) {
      this.context = context;
    }

    @Override
    public void handle(long now) {
      // max 1000fps
      if (lastUpdate != 0 && now - lastUpdate < 1_000_000_000 / 1000) return;

      frameCounter++;
      if (now - secondMark >= 1_000_000_000) {
        System.out.println("FPS: " + frameCounter + "(" + renderers.size() + ")");
        frameCounter = 0;
        secondMark = now;
      }

      double delta = Math.min(1, (now - lastUpdate) / 1_000_000_000d);

      updateAndRender(delta, context);
      lastUpdate = now;
    }
  }

}