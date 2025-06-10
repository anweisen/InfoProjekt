package game.map;

import game.Game;
import game.engine.Model;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class Map {

    private final String name;
    private final Image background;
    private final Waypoint[] waypoints;
    private final Waypoint start, end;
    private final boolean[][] canPlace;


    public Map(String name, Image background, Waypoint[] waypoints, Waypoint start, Waypoint end, Image allowPlace) {
        this.name = name;
        this.background = background;
        this.waypoints = waypoints;
        this.start = start;
        this.end = end;
        this.canPlace = new boolean[Game.VIRTUAL_WIDTH][Game.VIRTUAL_HEIGHT];
        calculateCanPlace(allowPlace);
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
    }

    public void calculateCanPlace(Image allowPlace) {
        PixelReader reader = allowPlace.getPixelReader();
        for (int i = 0; i < canPlace.length; i++) {
            for (int j = 0; j < canPlace[i].length; j++) {
                if (reader.getColor(i, j).equals(Color.BLACK)) {
                    canPlace[i][j] = true;
                }
            }
        }
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

    /**
     * POJO im Format der JSON-Datei, die die Map beschreibt, um von GSON leichter geladen zu werden.
     */
    public record MapPojo(String name, String img, String allowPlace, Waypoint[] waypoints, Waypoint start, Waypoint end) {
    }

    public record Waypoint(double x, double y) {
    }

}
