import java.util.ArrayList;

public class Spieler {

    protected String name;
    protected ArrayList<Karte> meineKarte;
    protected static ArrayList<Spieler> spielerListe = new ArrayList<>();


    public Spieler(String name) {
        this.name = name;
        this.meineKarte = new ArrayList<>();
    }


    public ArrayList<Karte> getMeineKarte() {
        return meineKarte;
    }

    public void setMeineKarte(ArrayList<Karte> meineKarte) {
        this.meineKarte = meineKarte;
    }

    public ArrayList<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public void setSpielerListe(ArrayList<Spieler> spielerListe) {
        this.spielerListe = spielerListe;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void addSpieler(Spieler spieler) {
        spielerListe.add(spieler);
    }

    public static void printSpieler() {
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
        return name + " " + meineKarte;
    }


}
