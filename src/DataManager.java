import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataManager {
    private static final String DATABASE_NAME = "punkteDatenbank.sqlite";
    private static final String CREATETABLE = "CREATE TABLE IF NOT EXISTS Sessions (Player TEXT NOT NULL, Session INTEGER NOT NULL, Round INTEGER NOT NULL, Score INTEGER NOT NULL, PRIMARY KEY (Player, Session, Round));";
    private static final String INSERT_OR_UPDATE_TEMPLATE = "INSERT INTO Sessions (Player, Session, Round, Score) VALUES ('%1$s', %2$d, %3$d, %4$d) ON CONFLICT(Player, Session, Round) DO UPDATE SET Score = excluded.Score;";
    private static SqliteClient client = null;

    private static void initializeClient() throws SQLException {
        if (client == null) {
            client = new SqliteClient(DATABASE_NAME);
        }
    }

    public static void datenbankErstellen() {
        try {
            initializeClient();
            if (!client.tableExists("Sessions")) {
                client.executeStatement(CREATETABLE);
            }
            System.out.println("Datenbank und Tabelle erfolgreich erstellt.");
        } catch (SQLException ex) {
            System.out.println("Ups! Da ist etwas schiefgelaufen: " + ex.getMessage());
        }
    }

    public static void RekordWinnerInDB(ArrayList<Spieler> spielerListe, int sessionNumber, int round) {
        try {
            initializeClient();

            for (Spieler spieler : spielerListe) {
                String playerName = spieler.getName();
                int score = spieler.getPunkte();

                //Punktzahl in die Datenbank einfügen oder aktualisieren
                client.executeStatement(String.format(INSERT_OR_UPDATE_TEMPLATE, playerName, sessionNumber, round, score));
                System.out.println("Punkte für " + playerName + " wurden in der Datenbank aktualisiert.");
            }
        } catch (SQLException ex) {
            System.out.println("Ups! Da ist etwas schiefgelaufen: " + ex.getMessage());
        }
    }

    public static void resetDatenbank() {
        try {
            SqliteClient client = new SqliteClient(DATABASE_NAME);
            if (client.tableExists("Sessions")) {
                client.executeStatement("DROP TABLE IF EXISTS Sessions;");
            }
            client.executeStatement(CREATETABLE);
            System.out.println("Die Datenbank wurde erfolgreich zurückgesetzt.");
        } catch (SQLException ex) {
            System.out.println("Ups! Da ist etwas schiefgelaufen: " + ex.getMessage());
        }
    }
}
