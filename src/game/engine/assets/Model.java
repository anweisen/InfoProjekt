package game.engine.assets;

import com.google.gson.JsonObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.concurrent.ConcurrentHashMap;

public class Model {

    private static final ConcurrentHashMap<String, Image> IMAGE_CACHE = new ConcurrentHashMap<>(); // Kopien reduzieren VRAM-Auslastung

    private final Image image;

    // Größe des Models im internen Koordinatensystem (Game.VIRTUAL_...)
    private final int width;
    private final int height;

    public Model(Image image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;
    }

    public static Image loadImage(String category, String name) {
        String key = category + "/" + name;
        if (IMAGE_CACHE.containsKey(key)) {
            return IMAGE_CACHE.get(key);
        }

        Image image = new Image(Assets.loadResource("img", category, name));
        IMAGE_CACHE.put(key, image);
        return image;
    }

    public static Model loadModelWith(String category, String name, int width, int height) {
        return new Model(loadImage(category, name), width, height);
    }

    public static Model loadModelFrom(String category, JsonObject json) {
        String img = json.get("img").getAsString();
        int width = json.get("width").getAsInt();
        int height = json.get("height").getAsInt();
        return loadModelWith(category, img, width, height);
    }

    public static void render(GraphicsContext graphics, Image image, double x, double y, double width, double height) {
        graphics.drawImage(image, x - width / 2, y - height / 2, width, height);
    }

    public static void renderRotated(GraphicsContext graphics, Image image, double x, double y, double width, double height, double rotation) {
        graphics.save();
        graphics.translate(x, y);
        graphics.rotate(rotation);
        graphics.drawImage(image, -width / 2, -height / 2, width, height);
        graphics.restore();
    }

    public void render(GraphicsContext graphics, double x, double y) {
        render(graphics, image, x, y, width, height);
    }

    public void renderScaled(GraphicsContext graphics, double x, double y, double scale) {
        render(graphics, image, x, y, width * scale, height * scale);
    }

    public void renderRotated(GraphicsContext graphics, double x, double y, double rotation) {
        renderRotated(graphics, image, x, y, width, height, rotation);
    }

    public Image getImage() {
        return image;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Model withSize(int width, int height) {
        return new Model(image, width, height);
    }
}
