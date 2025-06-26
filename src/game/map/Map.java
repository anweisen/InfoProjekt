package game.map;

import game.Game;
import game.engine.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Map {

    private static final int DISTANCE_BETWEEN_SPLINE_SAMPLES = 18;

    private final String name;
    private final Image background;
    private final Waypoint[] waypoints;
    private final Waypoint start, end;
    private final List<Waypoint> splinePoints;
    private final boolean[][] canPlace;

    public Map(String name, Image background, Waypoint[] waypoints, Waypoint start, Waypoint end, Image allowPlace) {
        this.name = name;
        this.background = background;
        this.waypoints = waypoints;
        this.start = start;
        this.end = end;
        this.splinePoints = new ArrayList<>(waypoints.length * 10);
        this.canPlace = new boolean[Game.VIRTUAL_WIDTH][Game.VIRTUAL_HEIGHT];
        calculateCanPlace(allowPlace);
        calculateSpline();
    }

    public static Map loadMap(String filename) {
        MapPojo map = Model.loadJson("map", filename, MapPojo.class);
        Image background = Model.loadImage("map", map.img);
        Image blackWhite = Model.loadImage("map", map.allowPlace);
        return new Map(map.name, background, map.waypoints, map.start, map.end, blackWhite);
    }

    public void render(GraphicsContext graphics) {
        graphics.drawImage(background, 0, 0, Game.VIRTUAL_WIDTH, Game.VIRTUAL_HEIGHT);

        // Zeichne Wegpunkte zur Visualisierung (Test!)
        graphics.setFill(Color.BLUE);
        for (Waypoint waypoint : waypoints) {
            graphics.fillOval(waypoint.x - 5, waypoint.y - 5, 10, 10);
        }
        // Zeichne Spline-Punkte zur Visualisierung (Test!)
        graphics.setFill(Color.RED);
        for (Waypoint point : splinePoints) {
            graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
        }
    }

    private void calculateCanPlace(Image allowPlace) {
        PixelReader reader = allowPlace.getPixelReader();
        for (int i = 0; i < canPlace.length; i++) {
            for (int j = 0; j < canPlace[i].length; j++) {
                if (reader.getColor(i, j).equals(Color.BLACK)) {
                    canPlace[i][j] = true;
                }
            }
        }
    }

    /**
     * Berechnet die "Spline"-Punkte zwischen den Wegpunkten,
     * um eine glatte Kurve ohne Abknicken/Ecken zu erzeugen.
     *
     * @see #catmullRom(Waypoint, Waypoint, Waypoint, Waypoint, double)
     */
    private void calculateSpline() {
        for (int i = -1; i < waypoints.length; i++) { // -1: start
            Waypoint p0 = getWaypointSafely(i - 1);
            Waypoint p1 = getWaypointSafely(i);
            Waypoint p2 = getWaypointSafely(i + 1);
            Waypoint p3 = getWaypointSafely(i + 2);

            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double sampleCount = Math.ceil(distance / DISTANCE_BETWEEN_SPLINE_SAMPLES); // aufrunden

            for (int j = 0; j < sampleCount; j++) {
                double t = (double) j / sampleCount;
                splinePoints.add(catmullRom(p0, p1, p2, p3, t));
            }
        }

        // Letzten Punkt hinzufügen, damit die Kurve bis zum Ende geht
        splinePoints.add(end);
    }

    public boolean getCanPlace(int x, int y) {
        return canPlace[x][y];
    }

    public boolean[][] getCanPlace() {
        return canPlace;
    }

    public String getName() {
        return name;
    }

    public Waypoint getStart() {
        return start;
    }

    public Waypoint getEnd() {
        return end;
    }

    public Waypoint[] getWaypoints() {
        return waypoints;
    }

    public Image getImage() {
        return background;
    }

    // Verhindert IndexOutOfBoundsException und vereinfacht Logik (-> start, end)
    public Waypoint getWaypointSafely(int index) {
        if (index >= waypoints.length) return end;
        if (index < 0) return start;
        return waypoints[index];
    }

    /**
     * Gibt die "Spline"-Punkte zurück, die den Pfad der Karte angeben.
     * Diese Punkte liegen auf einer glatten Kurve durch die Wegpunkte
     * und sind das Ergebnis der "Catmull-Rom-Spline-Interpolation".
     * Die Punkte sind in der Reihenfolge, in der sie auf der Karte erscheinen
     * und sind den ursprünglichen Wegpunkten für geschmeidige Bewegungen o.ä vorzuziehen.
     *
     * @return eine Liste von Punkten die auf einer glatten Kurve durch die Waypoints liegen
     */
    public List<Waypoint> getSplinePoints() {
        return splinePoints;
    }

    /**
     * POJO im Format der JSON-Datei, die die Map beschreibt, um von GSON leichter geladen zu werden.
     */
    public record MapPojo(String name, String img, String allowPlace, Waypoint[] waypoints, Waypoint start, Waypoint end) {
    }

    public record Waypoint(double x, double y) {
    }

    /**
     * "Catmull-Rom-Spline-Interpolation" (Formel-Quelle: ChatGPT).
     * Errechnet eine Kurve zwischen p1 und p2 (p0 und p3 geben die Richtung an, "davor und danach").
     * Erzeugt eine insgesamt glatte Kurve ohne Abknicken/Ecken.
     *
     * @param p0 die "Vergangenheit" (vor p1)
     * @param p1 Startpunkt der Kurve
     * @param p2 Endpunkt der Kurve
     * @param p3 die "Zukunft" (nach p2)
     * @param t Parameter zwischen 0 und 1, der die Position auf der Kurve angibt
     *          (0 = p1, 1 = p2)
     * @return ein neuer Waypoint, der auf der errechneten, glatten Kurve bei t liegt
     */
    public static Waypoint catmullRom(Waypoint p0, Waypoint p1, Waypoint p2, Waypoint p3, double t) {
        double t2 = t * t;
        double t3 = t2 * t;

        double x = 0.5 * (
            (2 * p1.x()) +
                (-p0.x() + p2.x()) * t +
                (2 * p0.x() - 5 * p1.x() + 4 * p2.x() - p3.x()) * t2 +
                (-p0.x() + 3 * p1.x() - 3 * p2.x() + p3.x()) * t3
        );

        double y = 0.5 * (
            (2 * p1.y()) +
                (-p0.y() + p2.y()) * t +
                (2 * p0.y() - 5 * p1.y() + 4 * p2.y() - p3.y()) * t2 +
                (-p0.y() + 3 * p1.y() - 3 * p2.y() + p3.y()) * t3
        );

        return new Waypoint(x, y);
    }

}
