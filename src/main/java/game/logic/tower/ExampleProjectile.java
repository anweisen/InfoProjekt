package game.logic.tower;

import game.Game;
import game.engine.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ExampleProjectile extends GameObject {


  private static final Image towerImage;

  static {
    towerImage = new Image(GameObject.class.getResourceAsStream("/assets/ball.png"));

  }

  public ExampleProjectile(double x, double y) {
    super(x, y, 25, 25);
  }

  @Override
  public void update(double delta) {
    x += (Game.VIRTUAL_WIDTH / 2d) * delta; // 2s on screen

    if (x > Game.VIRTUAL_WIDTH || y > Game.VIRTUAL_HEIGHT || x < 0 || y < 0) {
      Game.getInstance().unregisterGameObject(this);
    }
  }

  @Override
  public void render(GraphicsContext context) {
    context.save();
    context.translate(x, y);
    context.rotate(x);
    context.drawImage(towerImage, -width/2, -height/2, width, height);
    context.restore();
  }
}
