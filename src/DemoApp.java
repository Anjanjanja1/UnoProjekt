import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class DemoApp {
    private static final String CREATETABLE = "CREATE TABLE Sessions (Player varchar(100) NOT NULL, Session int NOT NULL, Round int NOT NULL, Score int NOT NULL, CONSTRAINT PK_Sessions PRIMARY KEY (Player, Session, Round));";
    private static final String INSERT_TEMPLATE= "INSERT INTO Sessions (Player, Session, Round, Score) VALUES ('%1s', %2d, %3d, %4d);";
    private static final String SELECT_BYPLAYERANDSESSION = "SELECT Player, SUM(Score) AS Score FROM Sessions WHERE Player = '%1s' AND Session = %2d;";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        PrintStream output = System.out;
        Spiel spiel = new Spiel(input, output);
        System.out.println("Spiel....");
        // Startet das Spiel
        spiel.run();
        ArrayList<Spieler> spielerListe = spiel.getSpielerListe();

        System.out.println();

        try {
            SqliteClient client = new SqliteClient("demodatabase.sqlite");
            if (client.tableExists("Sessions")){
                client.executeStatement("DROP TABLE Sessions;");
            }
            client.executeStatement(CREATETABLE);

            int sessionNumber = 1; // Du musst die richtige Sessionnummer herausfinden
            for (Spieler spieler : spielerListe) {
                String playerName = spieler.getName();
                int round = 1; // Du musst die richtige Runde herausfinden
                int score = spieler.getPunkte(); // Du musst die richtige Punktzahl herausfinden

                client.executeStatement(String.format(INSERT_TEMPLATE, playerName, sessionNumber, round, score));
            }

            for (Spieler spieler : spielerListe) {
                String playerName = spieler.getName();

                ArrayList<HashMap<String, String>> results = client.executeQuery(String.format(SELECT_BYPLAYERANDSESSION, playerName, sessionNumber));

                for (HashMap<String, String> map : results) {
                    System.out.println(map.get("Player") + " hat derzeit:  " + map.get("Score") + " Punkte");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Ups! Something went wrong:" + ex.getMessage());
        }
    }
}
