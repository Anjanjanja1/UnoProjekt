import java.util.ArrayList;
import java.util.Collections;

public class Stapel extends Karte {

    protected ArrayList<Karte> stapel;
    protected ArrayList<Karte> ablageStapel;

    public Stapel() {
        this.stapel = new ArrayList<>(108);
        this.ablageStapel = new ArrayList<>();
    }

    public ArrayList<Karte> getStapel() {
        return stapel;
    }

    public ArrayList<Karte> addKarte() {
        stapel.add(new Karte(0, "R", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "R", "R"));
            stapel.add(new Karte(20, "R", "S"));
            stapel.add(new Karte(20, "R", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "R", Integer.toString(j)));
            }
        }
        stapel.add(new Karte(0, "B", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "B", "R"));
            stapel.add(new Karte(20, "B", "S"));
            stapel.add(new Karte(20, "B", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "B", Integer.toString(j)));
            }
        }
        stapel.add(new Karte(0, "Y", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "Y", "R"));
            stapel.add(new Karte(20, "Y", "S"));
            stapel.add(new Karte(20, "Y", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "Y", Integer.toString(j)));
            }
        }
        stapel.add(new Karte(0, "G", "0"));
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "G", "R"));
            stapel.add(new Karte(20, "G", "S"));
            stapel.add(new Karte(20, "G", "+2"));
            for (int j = 1; j < 10; j++) {
                stapel.add(new Karte(j, "G", Integer.toString(j)));
            }
        }
        for (int i = 0; i < 4; i++) {
            stapel.add(new Karte(50, "W", "4"));
            stapel.add(new Karte(50, "W", ""));

        }
        return stapel;
    }

    public ArrayList<Karte> printAll() {
        for (Karte k : stapel) {
            System.out.print(k + " ");
        }
        System.out.println(stapel.size());
        return stapel;
    }

    public ArrayList<Karte> stapelSuffleUndTeilen(ArrayList<Karte> stapel, ArrayList<Spieler> spielerListe, int anzahl) {
        Collections.shuffle(stapel);
        System.out.println("S " + stapel);


        ablageStapel.add(stapel.getFirst());

        System.out.println("------");
        System.out.println("A " + ablageStapel);
        System.out.println("S " + stapel);
        stapel.remove(stapel.getFirst());
        System.out.println("------removed s");
        System.out.println("A " + ablageStapel);
        System.out.println("S " + stapel);
        for (Spieler s : spielerListe) {
            for (int j = 0; j < anzahl; j++) {
                s.meineKarte.add(stapel.get(j));
            }

        }
        System.out.println("M " + Spieler.meineKarte);
        System.out.println("S " + stapel);
        return stapel;
    }

    @Override
    public String toString() {
        return "Stapel " + stapel + "\n ablageStapel " + ablageStapel;
    }
}
