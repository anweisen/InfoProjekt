import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HighScoreManager {
private static final String DB_URL = "jdbc:mysql://www.gmg-info.de/wa839_db1";
    private static final String DB_USER = "wa839_1";
    private static final String DB_PASSWORD = "anappleaday";

    public static void main(String[] args) {
        getHighscores();
    }
    public static List<String> getHighscores() {
        List<String> highscores = new ArrayList<>();

        String sql = "SELECT player_name, score, date, time FROM highscores ORDER BY score DESC LIMIT 10";
         //String sql = "SELECT * FROM Person";


        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("player_name");
                int score = rs.getInt("score");
                Date date = rs.getDate("date"); 
                highscores.add(name + " - " + score);
               System.out.println(name + "-" +score + " / " + date);
               
               
            }

        } catch (SQLException e) {
            System.err.println("Fehler beim Abrufen der Highscores: " + e.getMessage());
        }

        return highscores;
    }

    public static void addHighscore(String playerName, int score) {
        String sql = "INSERT INTO highscores (player_name, score) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Fehler beim Einfügen des Highscores: " + e.getMessage());
        }
    }
}
