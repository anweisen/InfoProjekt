package game.engine.assets;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Assets {

    public static final Gson GSON = new Gson();

    public static String getResourceStreamPath(String type, String category, String name) {
        return "assets/" + type + "/" + category + "/" + name;
    }

    public static InputStream loadResource(String type, String category, String name) {
        String path = getResourceStreamPath(type, category, name);
        InputStream resource = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        if (resource == null) throw new IllegalArgumentException("Resource not found at '" + path + "'");
        return resource;
    }

    public static <T> T loadJson(String category, String name, Class<T> type) {
        InputStream stream = loadResource("conf", category, name);
        InputStreamReader reader = new InputStreamReader(stream);
        return GSON.fromJson(reader, type);
    }
}
