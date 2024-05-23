import java.util.ArrayList;

public class TopKarte extends Karte{

    protected ArrayList<Karte> ablageStapel;

    public TopKarte() {
        this.ablageStapel = new ArrayList<>();
    }

    public void showAll() {
        for (Karte karte : ablageStapel) {
            System.out.println(karte);
        }
    }

    @Override
    public String toString() {
        return "Top Karte: " + farbe + zeichen;
    }

    public ArrayList<Karte> getAblageStapel() {
        return ablageStapel;
    }

    public void setAblageStapel(ArrayList<Karte> ablageStapel) {
        this.ablageStapel = ablageStapel;
    }
}
