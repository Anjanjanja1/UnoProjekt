import java.util.ArrayList;
import java.util.Collections;

public class Stapel {

    protected ArrayList<Karte> stapel; //Liste der Karten im Stapel
    protected TopKarte topKarte; //Repräsentiert die oberste Karte im Ablagestapel
    private final Spiel spiel; //Referenz auf das Spielobjekt

    //Konstruktor
    public Stapel(Spiel spiel) {
        this.stapel = new ArrayList<>(108); //Initialisiert die Kartenliste mit einer Kapazität von 108 Karten
        this.topKarte = new TopKarte(); //Initialisiert die TopKarte
        this.spiel = spiel; //Setzt die Spielreferenz
    }

    //Getters und Setters
    public ArrayList<Karte> getStapel() {
        return stapel;
    }

    public TopKarte getTopKarte() {
        return topKarte;
    }

    //Methode zum Hinzufügen von Karten zum Stapel
    public void addKarten() {
        stapel.clear(); //Löscht vorhandene Karten im Stapel

        //Hinzufügen der Karten für jede Farbe
        String[] farben = {"R", "B", "Y", "G"};
        for (String farbe : farben) {
            stapel.add(new Karte(0, farbe, "0")); //Hinzufügen der 0-Karten
            for (int i = 0; i < 2; i++) {
                stapel.add(new Karte(20, farbe, "REV")); //Hinzufügen der REVERSE-Karten
                stapel.add(new Karte(20, farbe, "SKIP")); //Hinzufügen der SKIP-Karten
                stapel.add(new Karte(20, farbe, "+2")); //Hinzufügen der +2-Karten
                for (int j = 1; j < 10; j++) {
                    stapel.add(new Karte(j, farbe, Integer.toString(j))); //Hinzufügen der Zahlenkarten
                }
            }
        }

        //Hinzufügen der wilden Karten
        for (int i = 0; i < 4; i++) {
            stapel.add(new Karte(50, "WILD", "+4")); //Hinzufügen der +4 Wild-Karten
            stapel.add(new Karte(50, "WILD", "")); //Hinzufügen der Wild-Karten ohne Zeichen
        }
    }

    //Methode zum Mischen und Verteilen der Karten an die Spieler
    public void stapelShuffleUndTeilen(ArrayList<Spieler> spielerListe, int anzahl) {
        if (stapel.isEmpty()) {
            System.out.println("Der Stapel ist leer und kann nicht gemischt und verteilt werden.");
            return;
        }

        Collections.shuffle(stapel); //Mischt die Karten im Stapel

        //Verteilt die Karten an die Spieler
        for (Spieler spieler : spielerListe) {
            for (int j = 0; j < anzahl; j++) {
                if (!stapel.isEmpty()) {
                    spieler.addKarten(stapel.removeFirst());  //Entfernt eine Karte vom Stapel und fügt sie der Hand des Spielers hinzu
                }
            }
        }
        entferneErsteKarte(); //Entfernt die erste Karte vom Stapel
    }

    //Methode zum Neumischen des Ablagestapels und Zurücklegen in den Stapel
    public void reshuffleAblagestapel() {
        ArrayList<Karte> ablageStapel = this.getTopKarte().getAblageStapel();
        if (ablageStapel.size() > 1) {
            Karte topKarte = ablageStapel.removeLast(); //Entfernt die oberste Karte
            Collections.shuffle(ablageStapel); //Mischt die restlichen Karten im Ablagestapel
            this.getStapel().addAll(ablageStapel); //Fügt die gemischten Karten zurück in den Stapel
            ablageStapel.clear(); //Leert den Ablagestapel
            ablageStapel.add(topKarte); //Fügt die oberste Karte wieder hinzu
            System.out.println("Der Ablagestapel wurde gemischt und in den Stapel zurückgelegt.");
        } else {
            System.out.println("Keine Karten im Ablagestapel zum Mischen.");
        }
    }

    //Methode zum Entfernen der ersten Karte vom Stapel und Verwalten spezieller Karten
    public void entferneErsteKarte() {
        Karte ersteKarte;
        do {
            ersteKarte = stapel.removeFirst(); //Entfernt die erste Karte vom Stapel
            if (ersteKarte.getFarbe().equals("WILD") || ersteKarte.getZeichen().equals("+2")) {
                stapel.add(ersteKarte); //Fügt die Karte wieder an das Ende des Stapels ein, wenn sie unerwünscht ist
                System.out.println("Die erste Karte war eine unerwünschte Karte, wurde entfernt und am Ende des Stapels eingefügt.");
            }
        } while (ersteKarte.getFarbe().equals("WILD") || ersteKarte.getZeichen().equals("+2"));

        topKarte.getAblageStapel().add(ersteKarte); //Fügt die erste Karte dem Ablagestapel hinzu

        //Prüfen, ob die erste Karte eine "Skip"- oder "Reverse"-Karte ist
        if (ersteKarte.getZeichen().equals("SKIP")) {
            spiel.naechsterSpieler(); //Überspringt den nächsten Spieler
        } else if (ersteKarte.getZeichen().equals("REV")) {
            spiel.reverseKarte(); //Dreht die Spielrichtung um
        }
    }

    //Methode zum Zurücksetzen des Stapels und des Ablagestapels
    public void resetStapel() {
        stapel.clear(); //Leert den Stapel
        topKarte.getAblageStapel().clear(); //Leert den Ablagestapel
    }

    @Override
    public String toString() {
        return "Stapel " + stapel + "\nAblageStapel " + topKarte;
    }
}
