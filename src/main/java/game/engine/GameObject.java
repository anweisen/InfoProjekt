package game.engine;

public abstract class GameObject implements Actor, Renderer {

  protected double x;
  protected double y;

  protected double width;
  protected double height;

  public GameObject(double x, double y, double width, double height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

}
