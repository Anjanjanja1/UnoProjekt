import java.io.PrintStream;
import java.util.*;

public class Spiel {
    protected final Scanner input; //Zum Lesen von Benutzereingaben
    protected final PrintStream output; //Zum Schreiben von Ausgaben auf die Konsole
    protected final ArrayList<Spieler> spielerListe; //Liste der Spieler zu speichern
    protected final Stapel stapel; //Repräsentiert den Kartenstapel
    protected Spieler aktuellerSpieler; //Repräsentiert den aktuellen Spieler
    protected String gewaehlteFarbe; //Speichert die gewählte Farbe, wenn eine Spezialkarte gespielt wird
    protected int zuZiehendeKarten; //Speichert die Anzahl der Karten, die ein Spieler ziehen muss
    protected boolean karteGespielt; //Prüft, ob in der aktuellen Runde eine Karte gespielt wurde
    protected boolean karteGehoben; //Prüft, ob der Spieler eine Karte gehoben hat
    protected boolean karteReversed; //Prüft, ob die Spielrichtung umgekehrt wurde
    protected boolean karteSkip; //Prüft, ob der nächste Spieler übersprungen wird
    protected boolean unoGesagt; //Prüft, ob UNO gesagt wurde
    protected boolean havingWinner; //Prüft, ob ein Gewinner die Runde feststeht
    protected boolean sessionEnde; //Prüft, ob die Sitzung beendet ist
    public static int round = 1; //Zählt die Runden
    public static int session = 1; //Zählt die Sitzungen

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
        this.unoGesagt = false;
        this.havingWinner = false;
        this.sessionEnde = false;
        DataManager.datenbankErstellen(); //Erstellt die Datenbank
        initialisieren(); //Initialisiert das Spiel

        //Shutdown-Hook hinzufügen, um die Datenbank zurückzusetzen, wenn das Programm endet
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataManager.resetDatenbank();
            System.out.println("Database has been reset on program termination.");
        }));
    }

    //Die Hauptschleife des Spiels → Gameloop
    public void run() {
        if (session == 1 && round == 1) {
            benutzernameInput(); //Fragt nach Benutzernamen und erstellt Spieler
        }
        if (round == 1) {
            stapel.addKarten(); //Fügt Karten zum Stapel hinzu
            stapel.stapelShuffleUndTeilen(spielerListe, 7); //Mischt und teilt die Karten
            aktuellerSpieler = spielerListe.getFirst(); //Setzt den ersten Spieler als aktuellen Spieler
        }

        //Prüft, ob die oberste Karte ein "SKIP" oder "REVERSE" ist und führt entsprechend die Aktion aus
        Karte topKarte = getTopKarte();
        if (topKarte.getZeichen().equals("SKIP")) {
            skipKarte(); //Überspringt den aktuellen Spieler
        } else if (topKarte.getZeichen().equals("REV")) {
            reverseKarte(); //Dreht die Spielrichtung um
        }
        menu(); //Zeigt das Menü an und verarbeitet die Benutzereingaben
    }

    //Initialisiert das Spiel
    public void initialisieren() {
        output.println("Wilkommen zu unserem UNO Spiel!");
    }

    //Fragt nach Benutzernamen und erstellt Spieler
    public void benutzernameInput() {
        output.println("Wie viele Spieler möchten Sie haben? (1-4)");
        int gesamtSpielerAnzahl = input.nextInt();
        input.nextLine();

        if (gesamtSpielerAnzahl < 1 || gesamtSpielerAnzahl > 4) {
            output.println("Ungültige Anzahl von Spielern. Bitte geben Sie eine Zahl zwischen 1 und 4 ein.");
            return;
        }

        output.println("Wie viele menschliche Spieler möchten Sie haben?");
        int menschlicheAnzahl = input.nextInt();
        input.nextLine();

        if (menschlicheAnzahl < 0 || menschlicheAnzahl > gesamtSpielerAnzahl) {
            output.println("Ungültige Anzahl von menschlichen Spielern. Bitte geben Sie eine Zahl zwischen 0 und " + gesamtSpielerAnzahl + " ein.");
            return;
        }

        output.println("Du willst " + menschlicheAnzahl + " menschliche Spieler haben.");

        for (int i = 0; i < menschlicheAnzahl; i++) {
            output.println("Bitte gib den Namen von Spieler " + (i + 1) + " ein: ");
            String name = input.nextLine();
            int punkte = 0;
            Spieler spieler = new Spieler(name, punkte); //Erstellt einen neuen Spieler
            spielerListe.add(spieler); //Fügt den Spieler zur Liste hinzu
        }

        //Fügt Bots hinzu, wenn die Gesamtzahl der Spieler weniger als 4 beträgt
        int botAnzahl = gesamtSpielerAnzahl - menschlicheAnzahl;
        String[] botNames = {"Hansi", "Jon", "Johann", "Fluffy", "Lisa", "Fritz", "Helga", "Ferdi", "George", "Berni", "Terminator", "Rick", "Roger"};

        for (int i = 0; i < botAnzahl; i++) {
            String botName = botNames[i];
            int punkte = 0;
            Spieler bot = new BotSpieler(botName, punkte); //Erstellt einen neuen Bot-Spieler
            spielerListe.add(bot); //Fügt den Bot zur Liste hinzu
        }

        output.println("Spiel startet mit " + spielerListe.size() + " Spielern. Davon Bots: " + botAnzahl);
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
            output.println("MENÜ: \n 1. Karte heben \n 2. Karte legen \n 3. Uno sagen \n 4. Nächster Spieler \n 5. Hilfe \n 6. Punkte anzeigen \n Geben Sie Ihre Wahl ein: ");
            while (!input.hasNextInt()) {
                output.println("Ungültige Eingabe. Bitte eine Zahl zwischen 1 und 6 eingeben.");
                input.next();
            }
            menuAuswahl = input.nextInt();
            if (menuAuswahl < 1 || menuAuswahl > 6) {
                output.println("Ungültige Eingabe. Bitte eine Zahl zwischen 1 und 6 eingeben.");
            }
        } while (menuAuswahl < 1 || menuAuswahl > 6);
        return menuAuswahl;
    }

    //Zeigt das Menü und verwaltet die Auswahl des Spielers
    private void menu() {
        while (!havingWinner && !sessionEnde) {
            aktuellenZustandAnzeigen(); //Zeigt den aktuellen Spielstatus an
            //BotSpieler logik
            if (aktuellerSpieler instanceof BotSpieler) {
                botLogik(); //Logik für Bot-Spieler
                aktuellenZustandAnzeigen(); //Zeigt den aktuellen Spielstatus erneut an
                karteGespielt = false;
                karteGehoben = false;
                unoGesagt = false;
                try {
                    Thread.sleep(2000);  //Wartet 2 Sekunden
                } catch (Exception e) {
                    output.println("Error: " + e.getMessage());
                }
                if (!getTopKarte().getZeichen().equals("REV") && !getTopKarte().getZeichen().equals("SKIP")) {
                    naechsterSpieler(); //Wechselt zum nächsten Spieler
                }
            }
            //Spieler logik
            else {
                int menuAuswahl = benutzermenueauswahl(); //Fragt den Spieler nach der Auswahl im Menü

                switch (menuAuswahl) {
                    case 1:
                        if (karteGehoben) {
                            output.println("Du kannst in diesem Zug keine weitere Karte heben.");
                            break;
                        } else {
                            karteHeben(); //Der Spieler hebt eine Karte
                        }
                        break;
                    case 2:
                        //Stelle sicher, dass der Spieler keine Karte spielen kann, wenn er keine gültigen Karten zum Spielen hat
                        if (gueltigeKarten().isEmpty()) {
                            output.println("Du hast keine gültigen Karten zum Spielen. Bitte ziehe eine Karte.");
                            break;
                        }
                        if (karteGespielt) {
                            output.println("Du kannst in diesem Zug keine weitere Karte legen.");
                            output.println("Du bekommst eine Strafkarte");
                            karteHeben(); //Der Spieler hebt eine Strafkarte
                        } else {
                            karteLegen(); //Der Spieler legt eine Karte ab
                            ueberpruefeObAktuellerSpielerGewinnt(); //Überprüft, ob der Spieler gewonnen hat
                        }
                        break;
                    case 3:
                        unoGesagt = true;
                        unoSagen(); //Der Spieler sagt UNO
                        break;
                    case 4:
                        if (karteGespielt || karteGehoben) {
                            if (aktuellerSpieler.meineKarte.size() == 1 && !unoGesagt) {
                                output.println("Du hast nicht UNO gesagt. Du bekommst 2 Karten als Strafe!");
                                for (int i = 0; i < 2; i++) {
                                    pruefeObStapelLeerIst(); //Prüft, ob der Stapel leer ist
                                    Karte gezogeneKarte = stapel.getStapel().removeFirst(); //Der Spieler zieht eine Karte
                                    aktuellerSpieler.addKarten(gezogeneKarte); //Fügt die gezogene Karte der Hand des Spielers hinzu
                                }
                            }
                            karteGespielt = false;
                            karteGehoben = false;
                            unoGesagt = false;
                            naechsterSpieler(); //Wechselt zum nächsten Spieler
                        } else {
                            output.println("Du musst eine gültige Karte spielen oder eine Karte ziehen, bevor du den Zug beenden kannst!");
                            karteHeben(); //Der Spieler hebt eine Karte als Strafe
                        }
                        break;
                    case 5:
                        Hilfe.htmlDateiImBrowserOeffnen("BENUTZERHANDBUCH.html"); //Öffnet das Benutzerhandbuch im Browser
                        break;
                    case 6:
                        output.println("Aktueller Punktestand: " + aktuellerSpieler.punkte); //Zeigt den aktuellen Punktestand an
                        break;
                    default:
                        output.println("Ungültige Eingabe.");
                }
            }
        }
    }

    //Logik für den Bot-Spieler
    private void botLogik() {
        if (!karteGespielt && !karteGehoben) {
            //Stelle sicher, dass der Spieler keine Karte spielen kann, wenn er keine gültigen Karten zum Spielen hat
            if (gueltigeKarten().isEmpty()) {
                karteHeben(); //Der Bot hebt eine Karte
            }
            if (!gueltigeKarten().isEmpty())
                karteLegen(); //Der Bot legt eine Karte ab
        }
        if (aktuellerSpieler.meineKarte.size() == 1 && !unoGesagt) {
            unoSagen(); //Der Bot sagt UNO
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
        pruefeObStapelLeerIst(); //Prüft, ob der Stapel leer ist
        Karte gezogeneKarte = stapel.getStapel().removeFirst();//Der Spieler zieht eine Karte
        aktuellerSpieler.addKarten(gezogeneKarte); //Fügt die gezogene Karte der Hand des Spielers hinzu
        gueltigeKarten(); //Aktualisiert die gültigen Karten des Spielers
        if (aktuellerSpieler instanceof BotSpieler) {
            output.println("Der Bot " + aktuellerSpieler.getName() + " hat die Karte " + gezogeneKarte + " gezogen.");
        } else {
            output.println("Du hast die Karte " + gezogeneKarte + " gezogen.");
        }
        karteGehoben = true; //Setzt karteGehoben auf true
    }

    //Der aktuelle Spieler legt eine Karte ab
    private void karteLegen() {
        int index;
        if (karteGespielt) {
            output.println("Du kannst in diesem Zug keine weitere Karte legen.");
            return;
        }
        do {
            if (aktuellerSpieler instanceof BotSpieler) {
                index = (int) (Math.random() * gueltigeKarten().size()); //Der Bot wählt eine zufällige Karte
            } else {
                output.println("Welche Karte möchtest du legen? (Index eingeben)");
                while (!input.hasNextInt()) {
                    output.println("Ungültige Eingabe.");
                    input.next();
                }
                index = input.nextInt(); //Der Spieler gibt den Index der Karte ein
            }
        } while (!(index >= 0 && index < aktuellerSpieler.getMeineKarte().size()));

        Karte gelegteKarte;
        try { //Try Catch falls Array kleiner als gewählter Index ist
            gelegteKarte = gueltigeKarten().get(index);
        } catch (IndexOutOfBoundsException e) {
            output.println("Ungültiger Index. Probiere es nochmal!");
            karteLegen();
            return;
        }

        if (ueberpruefeKarte(gelegteKarte, getTopKarte())) { //Prüft, ob die Karte gespielt werden kann

            aktuellerSpieler.getMeineKarte().remove(gelegteKarte); //Entfernt die Karte aus der Hand des Spielers
            stapel.getTopKarte().getAblageStapel().add(gelegteKarte); //Fügt die Karte dem Ablagestapel hinzu
            karteGespielt = true;  //Setze karteGespielt auf true

            if (!gelegteKarte.getFarbe().equals("WILD")) {
                gewaehlteFarbe = ""; //Zurücksetzen der gewaehlteFarbe, wenn eine Nicht-Wild-Karte gespielt wird
            }

            specialKarten(gelegteKarte); //Behandelt alle Spezialeffekte der Karte
            if (aktuellerSpieler instanceof BotSpieler) {
                output.println("Der Bot " + aktuellerSpieler.getName() + " hat die Karte " + gelegteKarte + " gelegt.");
            } else {
                output.println("Du hast die Karte " + gelegteKarte + " gelegt.");
            }

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
                pruefeObStapelLeerIst(); //Prüft, ob der Stapel leer ist
                Karte gezogeneKarte = stapel.getStapel().removeFirst(); //Der Spieler zieht eine Karte
                aktuellerSpieler.addKarten(gezogeneKarte); //Fügt die gezogene Karte der Hand des Spielers hinzu
            }
            naechsterSpieler(); //Wechselt zum nächsten Spieler
        }
    }

    //Wechselt zum nächsten Spieler und behandelt die zu ziehenden Karten
    void naechsterSpieler() {
        int aktuellerIndex = spielerListe.indexOf(aktuellerSpieler); //Ruft den Index des aktuellen Spielers ab

        //Behandelt die Rückwärtsrichtung
        if (karteReversed) {
            if (aktuellerIndex > 0) {
                aktuellerIndex = aktuellerIndex - 1; //Gehe zum vorherigen Spieler, wenn es nicht der Erste ist
            } else {
                aktuellerIndex = spielerListe.size() - 1; //Wenn es der erste Spieler ist, gehe zum letzten Spieler
            }
        }

        //Behandelt das Überspringen
        if (karteSkip) {
            if (karteReversed) {
                //Wenn die Kartenreihenfolge umgekehrt ist, überspringen wir den vorherigen Spieler
                if (aktuellerIndex > 0) {
                    aktuellerIndex = aktuellerIndex - 1;
                } else {
                    aktuellerIndex = spielerListe.size() - 1;
                }
            } else {
                //Ansonsten überspringen wir den nächsten Spieler
                aktuellerIndex = (aktuellerIndex + 2) % spielerListe.size();
            }
            karteSkip = false;
        }
        //Behandelt die normale Rundenumdrehung, wenn nicht übersprungen wird
        else if (!karteReversed) {
            aktuellerIndex++;  //Gehe zum nächsten Spieler
            if (aktuellerIndex >= spielerListe.size()) {
                aktuellerIndex = 0;  //wenn das Ende der Liste erreicht ist, gehe zum Anfang der Liste
            }
        }

        aktuellerSpieler = spielerListe.get(aktuellerIndex); //Legt den nächsten Spieler fest

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
                    karte.getFarbe().equals("WILD") || karte.getFarbe().equals(obersteKarte.getFarbe()) && karte.getZeichen().equals("REV") ||
                    karte.getFarbe().equals(obersteKarte.getFarbe()) && karte.getZeichen().equals("SKIP");
        } else {
            return karte.getFarbe().equals(gewaehlteFarbe) || karte.getFarbe().equals("WILD");
        }
    }


    //Behandelt spezielle Karten (Wilde Karten, +2 Karten, +4 Karten)
    private void specialKarten(Karte gelegteKarte) {
        ueberpruefeObAktuellerSpielerGewinnt(); //Überprüfe, ob der Spieler nach dem Spielen einer +2-Karte gewinnt
        if (gelegteKarte.getFarbe().equals("WILD") && gelegteKarte.getZeichen().isEmpty()) {
            gewaehlteFarbe = farbeWaehlen(); //Fordert den Spieler auf, eine Farbe zu wählen
            output.println("Die Farbe ist " + gewaehlteFarbe);
        }
        if (gelegteKarte.getZeichen().equals("+2")) {
            zuZiehendeKarten += 2; //Addiert 2 zu der Anzahl der zu ziehenden Karten
        }
        if (gelegteKarte.getZeichen().equals("SKIP")) {
            skipKarte(); //Überspringt den nächsten Spieler
        }
        if (gelegteKarte.getZeichen().equals("REV")) {
            reverseKarte(); //Dreht die Spielrichtung um
        }
        if (gelegteKarte.getFarbe().equals("WILD") && gelegteKarte.getZeichen().equals("+4")) {
            //Fordert den Spieler auf, eine Farbe zu wählen und erhöht die Anzahl der zu ziehenden Karten um 4
            gewaehlteFarbe = farbeWaehlen();
            zuZiehendeKarten += 4;
            output.println("Die Farbe ist " + gewaehlteFarbe);
            if (!(aktuellerSpieler instanceof BotSpieler)) {
                naechsterSpieler(); //wenn menschlicher Spieler, zum nächsten Spieler wechseln
            }
        }
    }

    //Fragt den Spieler nach der Farbwahl
    private String farbeWaehlen() {
        //Bot logik
        if (aktuellerSpieler instanceof BotSpieler) {
            String[] botFarbe = {"Y", "R", "B", "G"};
            Random random = new Random();
            int index = random.nextInt(botFarbe.length);

            gewaehlteFarbe = botFarbe[index];
            return gewaehlteFarbe;
        }
        //Spieler logik
        else {
            do {
                output.println("Welche Farbe wählen Sie? \n [Y, R, B, G]: ");
                gewaehlteFarbe = input.next().toUpperCase();

                if (!(gewaehlteFarbe.equals("Y") || gewaehlteFarbe.equals("R") || gewaehlteFarbe.equals("B") || gewaehlteFarbe.equals("G"))) {
                    output.println("Ungültige Eingabe.");
                }
            } while (!(gewaehlteFarbe.equals("Y") || gewaehlteFarbe.equals("R") || gewaehlteFarbe.equals("B") || gewaehlteFarbe.equals("G")));
            return gewaehlteFarbe;
        }
    }

    //Methode zum Anwenden von Strafkarten
    private void strafkartenAnwenden() {
        //Wenn es Karten zu ziehen gibt:
        if (zuZiehendeKarten > 0) {
            output.println(aktuellerSpieler.getName() + " bekommt " + zuZiehendeKarten + " Karten.");
            for (int i = 0; i < zuZiehendeKarten; i++) {
                pruefeObStapelLeerIst(); //Prüft, ob der Stapel leer ist
                Karte gezogeneKarte = stapel.getStapel().removeFirst(); //Der Spieler zieht eine Karte
                aktuellerSpieler.addKarten(gezogeneKarte); //Fügt die gezogene Karte der Hand des Spielers hinzu
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
                strafkartenAnwenden(); //Wendet die Strafkarten an
            }
        }
    }

    //Überspringt den nächsten Spieler
    public void skipKarte() {
        int aktuellerIndex = spielerListe.indexOf(aktuellerSpieler); //Den Index des aktuellen Spielers
        aktuellerSpieler = spielerListe.get((aktuellerIndex + 2) % spielerListe.size());
        output.println("Skip! Der nächtste Spieler ist: " + aktuellerSpieler.getName());

        karteGespielt = false;
        karteGehoben = false;
    }

    //Dreht die Spielrichtung um
    public void reverseKarte() {
        Collections.reverse(spielerListe); //Dreht die Reihenfolge der Spieler um
        int indexOfCurrentPlayer = spielerListe.indexOf(aktuellerSpieler); //Findet den aktuellen Spieler
        int indexOfNextPlayer = (indexOfCurrentPlayer + 1) % spielerListe.size(); //Findet den nächsten Spieler
        aktuellerSpieler = spielerListe.get(indexOfNextPlayer); //Setzt den nächsten Spieler als aktuellen Spieler
        output.println("Reversed! Der naechste Spieler ist: " + aktuellerSpieler.getName());

        karteGespielt = false;
        karteGehoben = false;
    }

    //Prüft, ob der Stapel leer ist und mischt den Ablagestapel bei Bedarf neu
    public void pruefeObStapelLeerIst() {
        if (stapel.getStapel().isEmpty()) {
            stapel.reshuffleAblagestapel(); //Mischt den Ablagestapel neu und setzt ihn als neuen Stapel
        }
    }

    //Überprüft, ob der aktuelle Spieler gewonnen hat
    public void ueberpruefeObAktuellerSpielerGewinnt() {
        if (aktuellerSpieler.getMeineKarte().isEmpty()) {
            output.println("\n--- Ende der Runde ---");
            output.println(aktuellerSpieler.getName() + " hat dieses Spiel gewonnen! Deine Punkte werden addiert.");
            havingWinner = true;

            int gesamtPunkte = anzahlPunkteVomSpieler(); //Berechnet die Gesamtpunkte

            addierePunkteZuSpieler(aktuellerSpieler, gesamtPunkte); //Addiert die Punkte zum Gewinner

            //Die Punkte des Gewinners in die Datenbank eintragen
            DataManager.RekordWinnerInDB(spielerListe, session, round);

            output.println("Der jetzige Punktestand:");
            for (Spieler spieler : spielerListe) {
                output.println(spieler.getName() + ": " + spieler.getPunkte() + " Punkte");
            }

            if (aktuellerSpieler.getPunkte() >= 500) {
                output.println("DU HAST DAS SPIEL GEWONNEN!");
                sessionEnde = true;
                spielFortsetzen(); //Fragt, ob das Spiel fortgesetzt werden soll
            }
            if (!sessionEnde) {
                output.println("\nEine neue Runde beginnt...");
                round++;
                resetFuerNeueRunde(); //Setzt die Spielzustände für eine neue Runde zurück
                run(); //Startet eine neue Runde
            }
        }
    }

    //Methode die die Punkte in Spieler speichert
    public void addierePunkteZuSpieler(Spieler spieler, int punkte) {
        spieler.setPunkte(spieler.getPunkte() + punkte); //Addiert die Punkte zum Spieler
    }

    //Methode erstellen die Punkte zusammenzählt
    public int anzahlPunkteVomSpieler() {
        int gesamtPunkte = 0;
        for (Spieler spieler : spielerListe) {
            int punkte = 0;
            for (Karte karte : spieler.getMeineKarte()) {
                punkte += karte.getPunkte(); //Addiert die Punkte der Karten des Spielers
            }
            gesamtPunkte += punkte; //Addiert die Punkte zum Gesamtpunkte
            spieler.getMeineKarte().clear(); //Leert die Hand des Spielers
        }
        return gesamtPunkte;
    }

    //Relevante Spielzustände zurücksetzen
    private void resetFuerNeueRunde() {
        for (Spieler spieler : spielerListe) {
            spieler.getMeineKarte().clear(); //Leert die Hand jedes Spielers
        }

        stapel.resetStapel(); //Setzt den Stapel zurück
        stapel.addKarten(); //Fügt Karten zum Stapel hinzu
        stapel.stapelShuffleUndTeilen(spielerListe, 7); //Mischt und teilt die Karten

        Karte topKarte = stapel.getStapel().removeFirst(); //Zieht die oberste Karte
        stapel.getTopKarte().getAblageStapel().add(topKarte); //Fügt die oberste Karte dem Ablagestapel hinzu

        gewaehlteFarbe = "";
        zuZiehendeKarten = 0;
        karteGespielt = false;
        karteGehoben = false;
        karteReversed = false;
        karteSkip = false;
        unoGesagt = false;
        havingWinner = false;

        aktuellerSpieler = spielerListe.getFirst(); //Setzt den ersten Spieler als aktuellen Spieler
    }

    //Fragt, ob das Spiel fortgesetzt werden soll
    public void spielFortsetzen() {
        String continueGame;
        do {
            output.println("Möchtest du eine neue Sitzung starten? (Y/N)");
            continueGame = input.next().toUpperCase();

            if (!(continueGame.equals("Y") || continueGame.equals("N"))) {
                output.println("Ungültige Eingabe.");
            }
        } while (!(continueGame.equals("Y") || continueGame.equals("N")));
        if (continueGame.equals("Y")) {
            output.println("Die Sitzung wird zurückgesetzt. Ein neues Spiel beginnt...");
            for (Spieler spieler : spielerListe) {
                spieler.setPunkte(0); //Setzt die Punkte der Spieler zurück
            }
            havingWinner = false;
            sessionEnde = false;
            session++;
            round = 1;

            run(); //Startet das Spiel neu
        } else {
            output.println("Ende des Spiel!");
            System.exit(0); //Beendet das Programm und setzt die Datenbank zurück
        }
    }
}