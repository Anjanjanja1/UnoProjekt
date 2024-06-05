import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PunkteDatenbank {

    // parameter need to be configured according to your database server
    private static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String USER = "username";
    private static final String PASSWORD = "password";
    private static final String UPDATE_PUNKTE_SQL = "UPDATE spieler SET punkte = ? WHERE name = ?";

    public static void updateSpielerPunkte(String spielerName, int punkte) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_PUNKTE_SQL);
            preparedStatement.setInt(1, punkte);
            preparedStatement.setString(2, spielerName);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}