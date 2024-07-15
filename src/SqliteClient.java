import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SqliteClient {
    private Connection connection = null; //Verbindung zur SQLite-Datenbank

    //Konstruktor zum Herstellen der Verbindung zur Datenbank
    public SqliteClient(String dbName) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    //Überprüft, ob eine Tabelle mit dem gegebenen Namen existiert
    public boolean tableExists(String tableName) throws SQLException {
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
        return executeQuery(query).size() > 0; //Führt die Abfrage aus und prüft, ob Ergebnisse zurückgegeben werden
    }

    //Führt eine SQL-Anweisung aus, die keine Ergebnisse zurückgibt (z.B. CREATE, INSERT, UPDATE)
    public void executeStatement(String sqlStatement) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30); //Setzt das Timeout auf 30 Sekunden
            statement.executeUpdate(sqlStatement); //Führt die SQL-Anweisung aus
        }
    }

    //Führt eine SQL-Abfrage aus und gibt die Ergebnisse als Liste von HashMaps zurück
    public ArrayList<HashMap<String, String>> executeQuery(String sqlQuery) throws SQLException {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(30); //Setzt das Timeout auf 30 Sekunden
            try (ResultSet rs = statement.executeQuery(sqlQuery)) { //Führt die Abfrage aus und erhält das ResultSet
                ResultSetMetaData rsmd = rs.getMetaData(); //Meta-Daten des ResultSets
                int columns = rsmd.getColumnCount(); //Anzahl der Spalten im ResultSet
                while (rs.next()) {
                    HashMap<String, String> map = new HashMap<>();
                    for (int i = 1; i <= columns; i++) {
                        String value = rs.getString(i); //Wert der aktuellen Spalte
                        String key = rsmd.getColumnName(i); //Name der aktuellen Spalte
                        map.put(key, value); //Fügt das Paar (Spaltenname, Wert) zur Map hinzu
                    }
                    result.add(map); //Fügt die Map zur Ergebnisliste hinzu
                }
            }
        }
        return result;
    }
}
