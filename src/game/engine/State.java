package game.engine;

import game.Game;
import javafx.scene.canvas.GraphicsContext;

public abstract class State {

    protected final Game game;

    public State(Game game) {
        this.game = game;
    }

    public final Game getGame() {
        return game;
    }

    public abstract void render(GraphicsContext graphics);

    /**
     * Diese Methode wird vor jedem Rendern eines Frames aufgerufen, um den Spielzustand zu aktualisieren.
     *
     * @param deltaTime Die Zeit in Sekunden (aber maximal 1), die seit dem letzten Aufruf von {@code update()} vergangen ist.
     *                  Dieser Wert sollte verwendet werden, um zeitbasierte Berechnungen (z. B. Bewegungen oder Animationen)
     *                  unabhängig von der Bildrate konsistent zu halten.
     *                  Die Begrenzung soll unerwartetes Verhalten verhindern.
     */
    public abstract void update(double deltaTime);

    /**
     * Diese Methode schließt dieses State-Objekt und gibt alle Ressourcen frei, die es hält.
     * Nachdem diese Methode aufgerufen wurde, wird weder {@link #render(GraphicsContext)} noch {@link #update(double)} aufgerufen.
     */
    public abstract void dispose();

    public abstract void handleClick(double x, double y);

}
