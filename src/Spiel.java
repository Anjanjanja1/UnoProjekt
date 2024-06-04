import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

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
    protected boolean karteReversed;
    protected boolean karteSkip;


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
    }

    //Die Hauptschleife des Spiels → Gameloop
    public void run() {
        initialisieren();
        benutzernameInput();

        stapel.addKarten(); //Fügt Karten zum Stapel hinzu
        stapel.stapelShuffleUndTeilen(spielerListe, 7); //Mischt den Stapel und teilt jedem Spieler 7 Karten aus
        aktuellerSpieler = spielerListe.get(0); // Setzt den aktuellen Spieler auf den ersten Spieler in der Liste
        // Prüft, ob die oberste Karte ein "SKIP" oder "REVERSE" ist und führt entsprechend die Aktion aus
        Karte topKarte = getTopKarte();
        if (topKarte.getZeichen().contains("SKIP")) {
            skipKarte(); // überspringt den aktuellen Spieler
        } else if (topKarte.getZeichen().contains("REV")) {
            reverseKarte(); // dreht die Spielrichtung um
           // naechsterSpieler(); // geht zum nächsten Spieler in der neuen Richtung
        }
        // aktuellerSpieler = spielerListe.getFirst(); //Setzt den aktuellen Spieler auf den ersten Spieler in der Liste
        menu(); //Zeigt das Menü an und verarbeitet die Benutzereingaben
    }



    //Initialisiert das Spiel
    private void initialisieren() {
        System.out.println("Wilkommen zu unserem UNO Spiel!");
    }

    //Nimmt die Benutzernamen für die Spieler entgegen
    private void benutzernameInput() {
        for (int i = 0; i < 4; i++) {
            String[] testNames = {"Sophia", "Ajla", "Anja", "Hansi"};
            //System.out.println("Bitte gib den Namen von Spieler " + (i + 1) + " ein: ");
            //String name = input.nextLine();
            String name = testNames[i];
            Spieler spieler = new Spieler(name); //Erzeugt ein neues Spieler-Objekt mit dem eingegebenen Namen
            //Spieler.addSpieler(spieler); POSSIBLY NOT NECESSARY
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
        output.println("MENÜ: \n 1. Karte heben \n 2. Karte legen \n 3. Uno sagen \n 4. Nächster Spieler \n Geben Sie Ihre Wahl ein: ");
        int menuAuswahl = input.nextInt();
        input.nextLine();
        return menuAuswahl;
    }

    //Zeigt das Menü und verwaltet die Auswahl des Spielers
    private void menu() {
        while (true) { //TODO -> change true condition in points!
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
                    }
                    break;
                case 3:
                    unoSagen();
                    break;
                case 4:
                    if (karteGespielt || karteGehoben) {
                        karteGespielt = false;
                        karteGehoben = false;
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
        if (!stapel.getStapel().isEmpty()) {
            Karte gezogeneKarte = stapel.getStapel().removeFirst();
            aktuellerSpieler.addKarten(gezogeneKarte);
            gueltigeKarten();
            output.println("Du hast die Karte " + gezogeneKarte + " gezogen.");
            karteGehoben = true;
        } else {
            output.println("Der Stapel ist leer.");
        }
    }

    //Der aktuelle Spieler legt eine Karte ab
    private void karteLegen() {
//NEU
        if (karteGespielt) {
            output.println("Du kannst in diesem Zug keine weitere Karte legen.");
            return;
        }


        output.println("Welche Karte möchtest du legen? (Index eingeben)");
        int index = input.nextInt();
        input.nextLine();

        if (index >= 0 && index < aktuellerSpieler.getMeineKarte().size()) { //Prüft, ob die Indexeingabe gültig ist
            Karte gelegteKarte = aktuellerSpieler.getMeineKarte().get(index); //Holt die Karte mit dem angegebenen Index


            if (ueberpruefeKarte(gelegteKarte, getTopKarte())) { //Prüft, ob die Karte gespielt werden kann
                aktuellerSpieler.getMeineKarte().remove(index); //Entfernt die Karte aus der Hand des Spielers
                stapel.getTopKarte().getAblageStapel().add(gelegteKarte); //Fügt die Karte dem Ablagestapel hinzu
                karteGespielt = true;  //Setze karteGespielt auf true
                specialKarten(gelegteKarte); //Behandelt alle Spezialeffekte der Karte
                output.println("Du hast die Karte " + gelegteKarte + " gelegt.");

            } else {
                output.println("Ungültige Karte. Probiere es nochmal!");
            }
        } else {
            output.println("Ungültiger Index!");
        }
    }

    //Spieler sagt UNO
    private void unoSagen() {
        output.println("Spieler " + aktuellerSpieler.getName() + " hat UNO gesagt!");
        if (aktuellerSpieler.getMeineKarte().size() > 1) {
            output.println("Du hast nicht zur richtigen Zeit UNO gesagt. Du bekommst 2 Karten als Strafe!");
            for (int i = 0; i < 2; i++) {
                if (!stapel.getStapel().isEmpty()) {
                    Karte gezogeneKarte = stapel.getStapel().removeFirst();
                    aktuellerSpieler.addKarten(gezogeneKarte);
                } else {
                    output.println("Der Stapel ist leer.");
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
        if (zuZiehendeKarten > 0 && obersteKarte.getZeichen().contains("+2")) {
            return karte.getZeichen().contains("+2") || karte.getFarbe().contains("WILD");
        }
        if (obersteKarte.getZeichen().contains("+4")) {
            return karte.getFarbe().contains(gewaehlteFarbe) || karte.getZeichen().contains("+4") || karte.getFarbe().contains("WILD");
        }
        if (gewaehlteFarbe.isEmpty() && zuZiehendeKarten == 0 && !obersteKarte.getFarbe().contains("WILD")) {
            return karte.getFarbe().contains(obersteKarte.getFarbe()) || karte.getZeichen().contains(obersteKarte.getZeichen()) ||
                    karte.getFarbe().contains("WILD");
        } else {
            return karte.getFarbe().contains(gewaehlteFarbe) || karte.getFarbe().contains("WILD");
        }
    }

    //Behandelt spezielle Karten (Wilde Karten, +2 Karten, +4 Karten)
    private void specialKarten(Karte gelegteKarte) {
        if (gelegteKarte.getFarbe().contains("WILD") && gelegteKarte.getZeichen().isEmpty()) {
            gewaehlteFarbe = farbeWaehlen(); //Fordert den Spieler auf, eine Farbe zu wählen
            output.println("Die Farbe ist " + gewaehlteFarbe);
        }
        if (gelegteKarte.getZeichen().contains("+2")) {
            zuZiehendeKarten += 2; //Addiert 2 zu der Anzahl der zu ziehenden Karten
        }
        if (gelegteKarte.getZeichen().contains("SKIP")) {
            skipKarte();
        }
        if (gelegteKarte.getZeichen().contains("REV")) {
            reverseKarte();
        }
        if (gelegteKarte.getFarbe().contains("WILD") && gelegteKarte.getZeichen().contains("+4")) {
            //Fordert den Spieler auf, eine Farbe zu wählen und erhöht die Anzahl der zu ziehenden Karten um 4
            gewaehlteFarbe = farbeWaehlen();
            zuZiehendeKarten += 4;
            output.println("Die Farbe ist " + gewaehlteFarbe);
            naechsterSpieler();
        }
    }

    //Fragt den Spieler nach der Farbwahl
    private String farbeWaehlen() {
        System.out.println("Welche Farbe wählen Sie? \n [Y, R, B, G]: ");
        return input.nextLine().toUpperCase();
    }

    //Methode zum Anwenden von Strafkarten
    private void strafkartenAnwenden() {
        //Wenn es Karten zu ziehen gibt:
        if (zuZiehendeKarten > 0) {
            output.println(aktuellerSpieler.getName() + " bekommt " + zuZiehendeKarten + " Karten.");
            for (int i = 0; i < zuZiehendeKarten; i++) {
                if (!stapel.getStapel().isEmpty()) {
                    Karte gezogeneKarte = stapel.getStapel().removeFirst();
                    aktuellerSpieler.addKarten(gezogeneKarte);
                } else {
                    output.println("Der Stapel ist leer.");
                    break;
                }
            }
            zuZiehendeKarten = 0;
            karteGespielt = false;
            karteGehoben = false;

        }
    }

    //Methode zum Behandeln von offenen Strafkarten
    private void strafkartenBehandeln() {
        if (zuZiehendeKarten > 0) {
            ArrayList<Karte> gueltigeKarten = gueltigeKarten(); //Überprüft, ob der nächste Spieler gültige Karten hat

            boolean kannZiehenVermeiden = false;
            for (Karte karte : gueltigeKarten) {
                if (karte.getZeichen().contains("+2")) {
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
        aktuellerSpieler = spielerListe.get((aktuellerIndex + 1) % spielerListe.size());
        output.println("Skip! Der nächtste Spieler ist: " + aktuellerSpieler.getName());

        karteGespielt = false;
        karteGehoben = false;
    }

    public void reverseKarte() {
        int aktuellerIndex = spielerListe.indexOf(aktuellerSpieler); //Den Index des aktuellen Spielers
        aktuellerSpieler = spielerListe.get((spielerListe.size() - 1) % spielerListe.size());
        Collections.reverse(spielerListe);
        output.println("Reversed! Der nächtste Spieler ist: " + aktuellerSpieler.getName());
    }
}
