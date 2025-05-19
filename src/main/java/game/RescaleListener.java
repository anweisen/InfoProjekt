package game;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;

public class RescaleListener implements ChangeListener<Number> {

  private final GraphicsContext context;

  public RescaleListener(GraphicsContext context) {
    this.context = context;
  }

  @Override
  public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
    double scaleX = context.getCanvas().getWidth() / Game.VIRTUAL_WIDTH;
    double scaleY = context.getCanvas().getHeight() / Game.VIRTUAL_HEIGHT;
    Affine affine = new Affine();
    affine.appendScale(scaleX, scaleY);
    context.setTransform(affine);
  }
}
