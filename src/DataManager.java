import java.sql.SQLException;
import java.util.ArrayList;

public class DataManager {
    //Name der SQLite-Datenbankdatei
    private static final String DATABASE_NAME = "punkteDatenbank.sqlite";
    //SQL-Befehl zum Erstellen der Tabelle, wenn sie nicht existiert
    private static final String CREATETABLE = "CREATE TABLE IF NOT EXISTS Sessions (Player TEXT NOT NULL, Session INTEGER NOT NULL, Round INTEGER NOT NULL, Score INTEGER NOT NULL, PRIMARY KEY (Player, Session, Round));";
    //SQL-Vorlage zum Einfügen oder Aktualisieren von Datensätzen in der Tabelle
    private static final String INSERT_OR_UPDATE_TEMPLATE = "INSERT INTO Sessions (Player, Session, Round, Score) VALUES ('%1$s', %2$d, %3$d, %4$d) ON CONFLICT(Player, Session, Round) DO UPDATE SET Score = excluded.Score;";
    //SQL-Client zum Verbinden mit der Datenbank
    protected static SqliteClient client = null;

    //Initialisiert den SQL-Client, wenn er noch nicht initialisiert ist
    private static void initializeClient() throws SQLException {
        if (client == null) {
            client = new SqliteClient(DATABASE_NAME);
        }
    }

    //Erstellt die Datenbank und die Tabelle, wenn sie nicht existiert
    public static void datenbankErstellen() {
        try {
            initializeClient(); //Initialisiert den SQL-Client
            if (!client.tableExists("Sessions")) {
                client.executeStatement(CREATETABLE); //Führt den SQL-Befehl zum Erstellen der Tabelle aus
            }
            System.out.println("Datenbank und Tabelle erfolgreich erstellt.");
        } catch (SQLException ex) {
            System.out.println("Ups! Da ist etwas schiefgelaufen: " + ex.getMessage());
        }
    }

    //Fügt die Punktzahl des Gewinners in die Datenbank ein oder aktualisiert sie
    public static void RekordWinnerInDB(ArrayList<Spieler> spielerListe, int sessionNumber, int round) {
        try {
            initializeClient(); //Initialisiert den SQL-Client

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

    //Setzt die Datenbank zurück, indem die Tabelle gelöscht und neu erstellt wird
    public static void resetDatenbank() {
        try {
            SqliteClient client = new SqliteClient(DATABASE_NAME);
            if (client.tableExists("Sessions")) {
                client.executeStatement("DROP TABLE IF EXISTS Sessions;"); //Löscht die Tabelle, wenn sie existiert
            }
            client.executeStatement(CREATETABLE); //Erstellt die Tabelle neu
            System.out.println("Die Datenbank wurde erfolgreich zurückgesetzt.");
        } catch (SQLException ex) {
            System.out.println("Ups! Da ist etwas schiefgelaufen: " + ex.getMessage());
        }
    }
}
