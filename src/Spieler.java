import java.util.ArrayList;

public class Spieler {

    protected String name;
    protected static ArrayList<Karte> meineKarte;
    protected static ArrayList<Spieler> spielerListe;


    public Spieler(String name) {
        this.name = name;
        this.meineKarte = new ArrayList<>();
        this.spielerListe = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSpieler(Spieler spieler) {
        this.spielerListe.add(spieler);
    }

    public void printSpieler() {
        for (Spieler spieler : spielerListe) {
            System.out.println(spieler);
        }
    }

    public void addKarten(Karte karten) {
        this.meineKarte.add(karten);
    }

    public void printKarten() {
        for (Karte k : meineKarte) {
            System.out.println(k);
        }
    }

    @Override
    public String toString() {
        return name + " " + meineKarte + " " + spielerListe;
    }
}
