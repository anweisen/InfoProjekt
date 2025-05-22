package game.tower;

import game.engine.GameObject;

public abstract class AbstractTower extends GameObject {

    protected double nextShotCounter;

    protected int level;

    public AbstractTower(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public abstract double getShootInterval();

    @Override
    public void update(double deltaTime) {
        nextShotCounter += deltaTime;

        if (nextShotCounter >= getShootInterval()) {
            if (shoot()) {
                nextShotCounter = 0;
            }
        }
    }

    public abstract boolean shoot();
}
