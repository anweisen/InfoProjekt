package game.logic.tower;

import game.Game;
import game.engine.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ExampleTower extends GameObject {

  private static final Image towerImage;

  static {
    towerImage = new Image(GameObject.class.getResourceAsStream("/assets/canon.png"));
  }

  protected double nextShotCounter;

  public ExampleTower(double x, double y) {
    super(x, y, 90, 90);
  }

  @Override
  public void update(double delta) {
    nextShotCounter += delta;

    double shotInterval = getShotInterval();
    if (nextShotCounter >= shotInterval) {
      nextShotCounter = 0;

      Game.getInstance().registerGameObject(new ExampleProjectile(x, y));
    }
  }

  @Override
  public void render(GraphicsContext context) {
    context.drawImage(towerImage, x - width / 2, y - height / 2, width, height);
  }


  public double getShotInterval() {
    return 1d / 5; // 5 shots per second
  }

}
