import java.util.ArrayList;

public class Spieler {

    protected String name;
    protected static ArrayList<Karte> meineKarte;


    public Spieler(String name) {
        this.name = name;
        this.meineKarte = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
