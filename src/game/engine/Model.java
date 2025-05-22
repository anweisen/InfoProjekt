package game.engine;

import javafx.scene.image.Image;

public class Model {

    private final Image image;

    public Model(Image image) {
        this.image = image;
    }

    public static Model load(String type, String name) {
        Image image = new Image(Model.class.getClassLoader().getResourceAsStream("assets/" + type + "/" + name));
        return new Model(image);
    }

    public Image getImage() {
        return image;
    }
}
