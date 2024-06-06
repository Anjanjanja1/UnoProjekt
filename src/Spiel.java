import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Spiel {
    private static final String DB_URL = "jdbc:sqlite:UnoProjekt.mydatabase.db";
    private SqliteClient sqliteClient;
    protected final Scanner input; //Zum Lesen von Benutzereingaben
    protected final PrintStream output; //Zum Schreiben von Ausgaben auf die Konsole
    protected final ArrayList<Spieler> spielerListe; //Liste der Spieler zu speichern
    protected final Stapel stapel; //Repräsentiert den Kartenstapel
    protected Spieler aktuellerSpieler; //Repräsentiert den aktuellen Spieler
    protected String gewaehlteFarbe; //Speichert die gewählte Farbe, wenn eine Spezialkarte gespielt wird
    protected int zuZiehendeKarten; //Speichert die Anzahl der Karten, die ein Spieler ziehen muss
    protected boolean karteGespielt; //Prüft, ob in der aktuellen Runde eine Karte gespielt wurde
    protected boolean karteGehoben; //Prüft, ob der Spieler eine Karte gehoben hat
    protected boolean karteReversed;
    protected boolean karteSkip;
    protected boolean unoGesagt;
    protected boolean havingWinner;

    public Spiel(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;
        this.spielerListe = new ArrayList<>();
        this.stapel = new Stapel(this);
        this.gewaehlteFarbe = "";
        this.zuZiehendeKarten = 0;
        this.karteGespielt = false;
        this.karteGehoben = false;
        this.karteReversed = false;
        this.karteSkip = false;
        unoGesagt = false;
        this.havingWinner = false;
        try {
            sqliteClient = new SqliteClient("uno_game.db");
            initialisieren();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Die Hauptschleife des Spiels → Gameloop
    public void run() {
        benutzernameInput();
        stapel.addKarten(); //Fügt Karten zum Stapel hinzu
        stapel.stapelShuffleUndTeilen(spielerListe, 7); //Mischt den Stapel und teilt jedem Spieler 7 Karten aus
        aktuellerSpieler = spielerListe.getFirst(); // Setzt den aktuellen Spieler auf den ersten Spieler in der Liste

        //Prüft, ob die oberste Karte ein "SKIP" oder "REVERSE" ist und führt entsprechend die Aktion aus
        Karte topKarte = getTopKarte();
        if (topKarte.getZeichen().equals("SKIP")) {
            skipKarte(); // überspringt den aktuellen Spieler
        } else if (topKarte.getZeichen().equals("REV")) {
            reverseKarte(); // dreht die Spielrichtung um
        }
        menu(); //Zeigt das Menü an und verarbeitet die Benutzereingaben
    }

    //Initialisiert das Spiel
    private void initialisieren() {
        output.println("Wilkommen zu unserem UNO Spiel!");

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS spieler (name TEXT PRIMARY KEY, punkte INTEGER)";
            stmt.execute(createTableSQL);
        } catch (SQLException ex) {
            output.println("Error creating table: " + ex.getMessage());
        }
    }

    //Nimmt die Benutzernamen für die Spieler entgegen
    private void benutzernameInput() {
        for (int i = 0; i < 4; i++) {
            String[] testNames = {"Sophia", "Ajla", "Anja", "Hansi"};
            //System.out.println("Bitte gib den Namen von Spieler " + (i + 1) + " ein: ");
            //String name = input.nextLine();
            String name = testNames[i];
            int punkte = 0; //TODO punkte übergeben vom letzten spiel
            Spieler spieler = new Spieler(name, punkte); //Erzeugt ein neues Spieler-Objekt mit dem eingegebenen Namen
            spielerListe.add(spieler);
        }
    }

    //Zeigt den aktuellen Zustand des Spiels
    private void aktuellenZustandAnzeigen() {
        output.println("\nSpielername: " + aktuellerSpieler.getName());
        output.println("Top Karte: " + getTopKarte());
        output.println("Deine Karten: " + aktuellerSpieler.getMeineKarte());
        output.println("Mögliche Karten: " + gueltigeKarten());
    }

    //Fragt den Benutzer nach der Menüauswahl
    private int benutzermenueauswahl() {
        int menuAuswahl;
        do {
            output.println("MENÜ: \n 1. Karte heben \n 2. Karte legen \n 3. Uno sagen \n 4. Nächster Spieler \n Geben Sie Ihre Wahl ein: ");
            while (!input.hasNextInt()) {
                System.out.println("Ungültige Eingabe. Bitte eine Zahl zwischen 1 und 4 eingeben.");
                input.next();
            }
            menuAuswahl = input.nextInt();
            if (menuAuswahl < 1 || menuAuswahl > 4) {
                output.println("Ungültige Eingabe. Bitte eine Zahl zwischen 1 und 4 eingeben.");
            }
        } while (menuAuswahl < 1 || menuAuswahl > 4);
        return menuAuswahl;
    }

    //Zeigt das Menü und verwaltet die Auswahl des Spielers
    private void menu() {
        while (!havingWinner) { //TODO -> implement a (second?) Loop that runs until 500 points
            aktuellenZustandAnzeigen(); //Zeigt den aktuellen Spielstatus an
            int menuAuswahl = benutzermenueauswahl();

            switch (menuAuswahl) {
                case 1:
                    karteHeben();
                    break;
                case 2: //Neu Implementiert
                    if (karteGespielt) {
                        output.println("Du kannst in diesem Zug keine weitere Karte legen.");
                        output.println("Du bekommst eine Strafkarte");
                        karteHeben();
                    } else {
                        karteLegen();
                        checkIfCurrentPlayerWin();
                    }
                    break;
                case 3:
                    unoGesagt = true;
                    unoSagen();
                    break;
                case 4:
                    if (karteGespielt || karteGehoben) {
                        if (aktuellerSpieler.meineKarte.size() == 1 && !unoGesagt) {
                            output.println("Du hast nicht UNO gesagt. Du bekommst 2 Karten als Strafe!");
                            for (int i = 0; i < 2; i++) {
                                if (stapel.getStapel().isEmpty()) {
                                    reshuffleAblagestapel();
                                }
                                if (!stapel.getStapel().isEmpty()) {
                                    Karte gezogeneKarte = stapel.getStapel().removeFirst();
                                    aktuellerSpieler.addKarten(gezogeneKarte);
                                }
                            }
                        }
                        karteGespielt = false;
                        karteGehoben = false;
                        unoGesagt = false;
                        naechsterSpieler();
                    } else {
                        output.println("Du musst eine gültige Karte spielen oder eine Karte ziehen, bevor du den Zug beenden kannst!");
                        karteHeben(); //Strafkarte → wenn der Spieler keine Karte hebe oder lege
                    }
                    break;
                default:
                    output.println("Ungültige Eingabe.");
            }
        }
    }

    //Greift auf den Ablagestapel des Stapelobjekts zu und gibt die letzte Karte in der Liste zurück
    private Karte getTopKarte() {
        return stapel.getTopKarte().getAblageStapel().getLast();
    }

    //Gibt die gültigen Karten des aktuellen Spielers zurück
    private ArrayList<Karte> gueltigeKarten() {
        ArrayList<Karte> gueltigeKarten = new ArrayList<>();

        for (Karte karte : aktuellerSpieler.getMeineKarte()) {
            if (ueberpruefeKarte(karte, getTopKarte())) {
                gueltigeKarten.add(karte);
            }
        }
        return gueltigeKarten;
    }

    //Der aktuelle Spieler hebt eine Karte vom Stapel
    private void karteHeben() {
        if (stapel.getStapel().isEmpty()) {
            reshuffleAblagestapel();
        }

        if (!stapel.getStapel().isEmpty()) {
            Karte gezogeneKarte = stapel.getStapel().removeFirst();
            aktuellerSpieler.addKarten(gezogeneKarte);
            gueltigeKarten();
            output.println("Du hast die Karte " + gezogeneKarte + " gezogen.");
            karteGehoben = true;
        }
    }

    //Der aktuelle Spieler legt eine Karte ab
    private void karteLegen() {
        if (karteGespielt) {
            output.println("Du kannst in diesem Zug keine weitere Karte legen.");
            return;
        }

        int index;
        do {
            output.println("Welche Karte möchtest du legen? (Index eingeben)");
            while (!input.hasNextInt()) {
                System.out.println("Ungültige Eingabe.");
                input.next();
            }
            index = input.nextInt();
        } while (!(index >= 0 && index < aktuellerSpieler.getMeineKarte().size()));

        Karte gelegteKarte = aktuellerSpieler.getMeineKarte().get(index); //Holt die Karte mit dem angegebenen Index

        if (ueberpruefeKarte(gelegteKarte, getTopKarte())) { //Prüft, ob die Karte gespielt werden kann
            aktuellerSpieler.getMeineKarte().remove(index); //Entfernt die Karte aus der Hand des Spielers
            stapel.getTopKarte().getAblageStapel().add(gelegteKarte); //Fügt die Karte dem Ablagestapel hinzu
            karteGespielt = true;  //Setze karteGespielt auf true

            if (!gelegteKarte.getFarbe().equals("WILD")) {
                gewaehlteFarbe = ""; //Zurücksetzen der gewaehlteFarbe, wenn eine Nicht-Wild-Karte gespielt wird
            }

            specialKarten(gelegteKarte); //Behandelt alle Spezialeffekte der Karte
            output.println("Du hast die Karte " + gelegteKarte + " gelegt.");

        } else {
            output.println("Ungültige Karte. Probiere es nochmal!");
            karteHeben();
        }
    }

    //Spieler sagt UNO
    private void unoSagen() {
        output.println("Spieler " + aktuellerSpieler.getName() + " hat UNO gesagt!");
        if (aktuellerSpieler.getMeineKarte().size() > 1) {
            output.println("Du hast nicht zur richtigen Zeit UNO gesagt. Du bekommst 2 Karten als Strafe!");
            for (int i = 0; i < 2; i++) {
                if (stapel.getStapel().isEmpty()) {
                    reshuffleAblagestapel();
                }
                if (!stapel.getStapel().isEmpty()) {
                    Karte gezogeneKarte = stapel.getStapel().removeFirst();
                    aktuellerSpieler.addKarten(gezogeneKarte);
                }
            }
            naechsterSpieler();
        }
    }

    //Wechselt zum nächsten Spieler und behandelt die zu ziehenden Karten
    void naechsterSpieler() {
        int aktuellerIndex = spielerListe.indexOf(aktuellerSpieler); //Den Index des aktuellen Spielers
        if (karteReversed) {
            if (aktuellerIndex <= 0) {
                aktuellerIndex = spielerListe.size() - 1;
            }
            aktuellerIndex--;
        }
        if (karteSkip) {
            aktuellerIndex = (aktuellerIndex + 2) % spielerListe.size();
            karteSkip = false;
        } else {
            aktuellerIndex = (aktuellerIndex + 1) % spielerListe.size();
        }

        aktuellerSpieler = spielerListe.get(aktuellerIndex); // Setzt den nächsten

        output.println("Die aktuelle Spieler ist: " + aktuellerSpieler.getName());
        //Wenn es Karten zu ziehen gibt, handle das
        strafkartenBehandeln();
    }

    //Überprüft, ob eine Karte gespielt werden kann
    private boolean ueberpruefeKarte(Karte karte, Karte obersteKarte) {
        if (zuZiehendeKarten > 0 && obersteKarte.getZeichen().equals("+2")) {
            return karte.getZeichen().equals("+2") || karte.getFarbe().equals("+4");
        }
        if (gewaehlteFarbe.isEmpty()) {
            return karte.getFarbe().equals(obersteKarte.getFarbe()) || karte.getZeichen().equals(obersteKarte.getZeichen()) ||
                    karte.getFarbe().equals("WILD") || karte.getFarbe().equals(obersteKarte.getFarbe()) && karte.getZeichen().equals("REV") || karte.getFarbe().equals(obersteKarte.getFarbe()) && karte.getZeichen().equals("SKIP");
        } else {
            return karte.getFarbe().equals(gewaehlteFarbe) || karte.getFarbe().equals("WILD");
        }
    }


    //Behandelt spezielle Karten (Wilde Karten, +2 Karten, +4 Karten)
    private void specialKarten(Karte gelegteKarte) {
        if (gelegteKarte.getFarbe().equals("WILD") && gelegteKarte.getZeichen().isEmpty()) {
            gewaehlteFarbe = farbeWaehlen(); //Fordert den Spieler auf, eine Farbe zu wählen
            output.println("Die Farbe ist " + gewaehlteFarbe);
        }
        if (gelegteKarte.getZeichen().equals("+2")) {
            zuZiehendeKarten += 2; //Addiert 2 zu der Anzahl der zu ziehenden Karten
        }
        if (gelegteKarte.getZeichen().equals("SKIP")) {
            skipKarte();
        }
        if (gelegteKarte.getZeichen().equals("REV")) {
            reverseKarte();
        }
        if (gelegteKarte.getFarbe().equals("WILD") && gelegteKarte.getZeichen().equals("+4")) {
            //Fordert den Spieler auf, eine Farbe zu wählen und erhöht die Anzahl der zu ziehenden Karten um 4
            gewaehlteFarbe = farbeWaehlen();
            zuZiehendeKarten += 4;
            output.println("Die Farbe ist " + gewaehlteFarbe);
            naechsterSpieler();
        }
    }

    //Fragt den Spieler nach der Farbwahl
    private String farbeWaehlen() {
        do {
            output.println("Welche Farbe wählen Sie? \n [Y, R, B, G]: ");
            gewaehlteFarbe = input.next().toUpperCase();

            if (!(gewaehlteFarbe.equals("Y") || gewaehlteFarbe.equals("R") || gewaehlteFarbe.equals("B") || gewaehlteFarbe.equals("G"))) {
                output.println("Ungültige Eingabe.");
            }
        } while (!(gewaehlteFarbe.equals("Y") || gewaehlteFarbe.equals("R") || gewaehlteFarbe.equals("B") || gewaehlteFarbe.equals("G")));
        return gewaehlteFarbe;
    }

    //Methode zum Anwenden von Strafkarten
    private void strafkartenAnwenden() {
        //Wenn es Karten zu ziehen gibt:
        if (zuZiehendeKarten > 0) {
            output.println(aktuellerSpieler.getName() + " bekommt " + zuZiehendeKarten + " Karten.");
            for (int i = 0; i < zuZiehendeKarten; i++) {
                if (stapel.getStapel().isEmpty()) {
                    reshuffleAblagestapel();
                }
                if (!stapel.getStapel().isEmpty()) {
                    Karte gezogeneKarte = stapel.getStapel().removeFirst();
                    aktuellerSpieler.addKarten(gezogeneKarte);
                }
            }
            zuZiehendeKarten = 0;
            karteGespielt = false;
            karteGehoben = false;
        }
    }

    //Methode zum Behandeln von offenen Strafkarten
    private void strafkartenBehandeln() {
        karteGespielt = false;
        if (zuZiehendeKarten > 0) {
            ArrayList<Karte> gueltigeKarten = gueltigeKarten(); //Überprüft, ob der nächste Spieler gültige Karten hat

            boolean kannZiehenVermeiden = false;
            for (Karte karte : gueltigeKarten) {
                if (!getTopKarte().getFarbe().equals("WILD") && karte.getZeichen().equals("+2")) {
                    kannZiehenVermeiden = true;
                    break;
                }
            }
            if (!kannZiehenVermeiden) {
                strafkartenAnwenden();
            }
        }
    }

    public void skipKarte() {
        int aktuellerIndex = spielerListe.indexOf(aktuellerSpieler); //Den Index des aktuellen Spielers
        aktuellerSpieler = spielerListe.get((aktuellerIndex + 2) % spielerListe.size());
        output.println("Skip! Der nächtste Spieler ist: " + aktuellerSpieler.getName());

        karteGespielt = false;
        karteGehoben = false;
    }

    public void reverseKarte() {
        aktuellerSpieler = spielerListe.get((spielerListe.size() - 1) % spielerListe.size());
        Collections.reverse(spielerListe);
        output.println("Reversed! Der nächtste Spieler ist: " + aktuellerSpieler.getName());

        karteGespielt = false;
        karteGehoben = false;
    }

    public void checkIfCurrentPlayerWin() {
        if (aktuellerSpieler.meineKarte.isEmpty()) {
            System.out.println("Du hast dieses Spiel gewonnen. Deine Punkte werden addiert.");
            havingWinner = true;

            int gesamtPunkte = countPointsFromPlayer();

            addPointsToPlayer(aktuellerSpieler, gesamtPunkte);

            updateSpielerPunkte(aktuellerSpieler.getName(), aktuellerSpieler.getPunkte());

            output.println("Der jetzige Punktestand:");
            for (Spieler spieler : spielerListe) {
                output.println(spieler.getName() + ": " + spieler.getPunkte() + " Punkte");
            }
        }
    }

    //  Methode erstellen die Punkte zusammenzählt
    public int countPointsFromPlayer() {
        int gesamtPunkte = 0;
        for (Spieler spieler : spielerListe) {
            int punkte = 0;
            for (Karte karte : spieler.getMeineKarte()) {
                punkte += karte.getPunkte();
            }
            gesamtPunkte += punkte;
            spieler.getMeineKarte().clear();
        }
        return gesamtPunkte;
    }

    private void updateSpielerPunkte(String spielerName, int punkte) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement updateStmt = conn.prepareStatement("INSERT INTO spieler (name, punkte) VALUES (?, ?) ON CONFLICT(name) DO UPDATE SET punkte = excluded.punkte")) {
            updateStmt.setString(1, spielerName);
            updateStmt.setInt(2, punkte);
            updateStmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    //  Methode die die Punkte in Spieler speichert
    public void addPointsToPlayer(Spieler spieler, int punkte) {
        spieler.setPunkte(spieler.getPunkte() + punkte);
    }

    public void reshuffleAblagestapel() {
        ArrayList<Karte> ablageStapel = stapel.getTopKarte().getAblageStapel();
        if (ablageStapel.size() > 1) {
            Karte topKarte = ablageStapel.remove(ablageStapel.size() - 1); // Keep the top card
            Collections.shuffle(ablageStapel);
            stapel.getStapel().addAll(ablageStapel);
            ablageStapel.clear();
            ablageStapel.add(topKarte);
            output.println("Der Ablagestapel wurde gemischt und in den Stapel zurückgelegt.");
        } else {
            output.println("Keine Karten im Ablagestapel zum Mischen.");
        }
    }
}




