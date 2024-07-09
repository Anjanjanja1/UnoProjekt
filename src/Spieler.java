import java.util.ArrayList;
import java.util.HashMap;

public class Spieler {

    protected String name;
    protected ArrayList<Karte> meineKarte;
    protected static ArrayList<Spieler> spielerListe = new ArrayList<>();
    protected int punkte;


    public Spieler(String name, int punkte) {
        this.name = name;
        this.punkte = punkte;
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

    public int getPunkte() {
        return punkte;
    }

    public void setPunkte(int punkte) {
        this.punkte = punkte;
    }

    public static void addSpieler(Spieler spieler) {
        spielerListe.add(spieler);
    }

    public void addKarten(Karte karten) {
        this.meineKarte.add(karten);
    }

    public static void printSpieler() {
        for (Spieler spieler : spielerListe) {
            System.out.println(spieler);
        }
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
