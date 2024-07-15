import java.util.ArrayList;

public class Spieler {

    protected String name; //Name des Spielers
    protected ArrayList<Karte> meineKarte; //Liste der Karten, die der Spieler besitzt
    protected static ArrayList<Spieler> spielerListe = new ArrayList<>(); //Liste aller Spieler
    protected int punkte; //Punktestand des Spielers

    //Konstruktor
    public Spieler(String name, int punkte) {
        this.name = name;
        this.punkte = punkte;
        this.meineKarte = new ArrayList<>(); //Initialisiert die Kartenliste des Spielers
    }

    //Getters und Setters
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

    //Fügt eine Karte zur Kartenliste des Spielers hinzu
    public void addKarten(Karte karten) {
        this.meineKarte.add(karten);
    }

    @Override
    public String toString() {
        return name + " " + meineKarte;
    }
}
