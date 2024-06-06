import java.util.ArrayList;
import java.util.Collections;

public class Stapel extends Karte {

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

    public ArrayList<Karte> addKarten() {
        stapel.add(new Karte(0, "R-", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "R", "REV")); //REVERSE
            stapel.add(new Karte(20, "R", "SKIP")); //SKIP
            stapel.add(new Karte(20, "R", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "R", Integer.toString(j)));
            }
        }
        stapel.add(new Karte(0, "B", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "B", "REV"));
            stapel.add(new Karte(20, "B", "SKIP"));
            stapel.add(new Karte(20, "B", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "B", Integer.toString(j)));
            }
        }
        stapel.add(new Karte(0, "Y", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "Y", "REV"));
            stapel.add(new Karte(20, "Y", "SKIP"));
            stapel.add(new Karte(20, "Y", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "Y", Integer.toString(j)));
            }
        }
        stapel.add(new Karte(0, "G", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "G", "REV"));
            stapel.add(new Karte(20, "G", "SKIP"));
            stapel.add(new Karte(20, "G", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "G", Integer.toString(j)));
            }
        }
        for (int i = 0; i < 4; i++) {
            stapel.add(new Karte(50, "WILD", "+4"));
            stapel.add(new Karte(50, "WILD", ""));
        }
        return stapel;
    }


    public ArrayList<Karte> stapelShuffleUndTeilen(ArrayList<Spieler> spielerListe, int anzahl) {

        if (stapel.isEmpty()) {
            System.out.println("Der Stapel ist leer und kann nicht gemischt und verteilt werden.");
            return stapel;
        }

        Collections.shuffle(stapel);

        entferneErsteKarte();

        for (Spieler s : spielerListe) {
            for (int j = 0; j < anzahl; j++) {
                if (!stapel.isEmpty()) {
                    s.addKarten(stapel.removeFirst());
                }
            }
        }
        return stapel;
    }


    public void entferneErsteKarte() {
        Karte ersteKarte;
        do {
            ersteKarte = stapel.removeFirst();
            if (ersteKarte.getFarbe().contains("WILD") || ersteKarte.getZeichen().contains("+2")) {
                stapel.addLast(ersteKarte);
                System.out.println("Die erste Karte war eine unerwünschte Karte, wurde entfernt und am Ende des Stapels eingefügt.");
            }
        } while (ersteKarte.getFarbe().contains("WILD") || ersteKarte.getZeichen().contains("+2"));
        topKarte.getAblageStapel().add(ersteKarte);

        // Prüfen, ob die erste Karte eine "Skip"-Karte ist
        if (ersteKarte.getZeichen().contains("SKIP")) {
            spiel.naechsterSpieler();
        }
        // Prüfen, ob die erste Karte eine "Rev"-Karte ist
        else if (ersteKarte.getZeichen().contains("REV")) {
            spiel.naechsterSpieler(); //Invertiere die Spielreihenfolge
        }
    }


    @Override
    public String toString() {
        return "Stapel " + stapel + "\n ablageStapel " + topKarte;
    }


}
