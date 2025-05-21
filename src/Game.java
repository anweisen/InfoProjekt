import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Game extends Application {
    
    public static final int
        VIRTUAL_WIDTH = 1600,
        VIRTUAL_HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        GraphicsContext graphics = canvas.getGraphicsContext2D();

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
