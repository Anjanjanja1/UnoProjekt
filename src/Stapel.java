import java.util.ArrayList;
import java.util.Collections;

public class Stapel {

    protected ArrayList<Karte> stapel;
    protected TopKarte topKarte;
    private final Spiel spiel;

    public Stapel(Spiel spiel) {
        this.stapel = new ArrayList<>(108);
        this.topKarte = new TopKarte();
        this.spiel = spiel;
    }

    public ArrayList<Karte> getStapel() {
        return stapel;
    }

    public TopKarte getTopKarte() {
        return topKarte;
    }

    public void addKarten() {
        stapel.clear(); // Clear existing cards

        // Adds cards for each color
        String[] farben = {"R", "B", "Y", "G"};
        for (String farbe : farben) {
            stapel.add(new Karte(0, farbe, "0"));
            for (int i = 0; i < 2; i++) {
                stapel.add(new Karte(20, farbe, "REV")); // REVERSE
                stapel.add(new Karte(20, farbe, "SKIP")); // SKIP
                stapel.add(new Karte(20, farbe, "+2")); // +2
                for (int j = 1; j < 10; j++) {
                    stapel.add(new Karte(j, farbe, Integer.toString(j)));
                }
            }
        }

        // Adds wild cards
        for (int i = 0; i < 4; i++) {
            stapel.add(new Karte(50, "WILD", "+4"));
            stapel.add(new Karte(50, "WILD", ""));
        }
    }

    public void stapelShuffleUndTeilen(ArrayList<Spieler> spielerListe, int anzahl) {
        if (stapel.isEmpty()) {
            System.out.println("Der Stapel ist leer und kann nicht gemischt und verteilt werden.");
            return;
        }

        Collections.shuffle(stapel);

        for (Spieler spieler : spielerListe) {
            for (int j = 0; j < anzahl; j++) {
                if (!stapel.isEmpty()) {
                    spieler.addKarten(stapel.remove(0));
                }
            }
        }
        entferneErsteKarte();
    }

    public void entferneErsteKarte() {
        Karte ersteKarte;
        do {
            ersteKarte = stapel.remove(0);
            if (ersteKarte.getFarbe().equals("WILD") || ersteKarte.getZeichen().equals("+2")) {
                stapel.add(ersteKarte);
                System.out.println("Die erste Karte war eine unerwünschte Karte, wurde entfernt und am Ende des Stapels eingefügt.");
            }
        } while (ersteKarte.getFarbe().equals("WILD") || ersteKarte.getZeichen().equals("+2"));

        topKarte.getAblageStapel().add(ersteKarte);

        // Prüfen, ob die erste Karte eine "Skip"- oder "Reverse"-Karte ist
        if (ersteKarte.getZeichen().equals("SKIP")) {
            spiel.naechsterSpieler();
        } else if (ersteKarte.getZeichen().equals("REV")) {
            spiel.reverseKarte(); // Korrekturen: naechsterSpieler ist nicht korrekt für reverse
        }
    }

    public void resetStapel() {
        stapel.clear();
        topKarte.getAblageStapel().clear();
    }

    @Override
    public String toString() {
        return "Stapel " + stapel + "\nAblageStapel " + topKarte;
    }
}
