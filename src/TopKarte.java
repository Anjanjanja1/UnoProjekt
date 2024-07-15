import java.util.ArrayList;

public class TopKarte extends Karte{

    protected ArrayList<Karte> ablageStapel; //Liste der Karten im Ablagestapel

    //Konstruktor
    public TopKarte() {
        this.ablageStapel = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Top Karte: " + farbe + zeichen;
    }

    //Getter und Setter
    public ArrayList<Karte> getAblageStapel() {
        return ablageStapel;
    }

    public void setAblageStapel(ArrayList<Karte> ablageStapel) {
        this.ablageStapel = ablageStapel;
    }
}
