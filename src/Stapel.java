import java.util.ArrayList;

public class Stapel {

    protected ArrayList<Karte> stapel;

    public Stapel() {
        this.stapel = new ArrayList<>(108);
    }

    public ArrayList<Karte> getStapel() {
        return stapel;
    }

    public void addKarte() {
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "R", "R"));
            stapel.add(new Karte(20, "R", "S"));
            stapel.add(new Karte(20, "R", "+2"));
            for (int j = 0; j < 10; j++) {
                stapel.add(new Karte(j, "R", Integer.toString(j)));
            }
        }
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "B", "R"));
            stapel.add(new Karte(20, "B", "S"));
            stapel.add(new Karte(20, "B", "+2"));
            for (int j = 0; j < 10; j++) {
                stapel.add(new Karte(j, "B", Integer.toString(j)));
            }
        }

        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "Y", "R"));
            stapel.add(new Karte(20, "Y", "S"));
            stapel.add(new Karte(20, "Y", "+2"));
            for (int j = 0; j < 10; j++) {
                stapel.add(new Karte(j, "Y", Integer.toString(j)));
            }
        }
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(20, "G", "R"));
            stapel.add(new Karte(20, "G", "S"));
            stapel.add(new Karte(20, "G", "+2"));
            for (int j = 0; j < 10; j++) {
                stapel.add(new Karte(j, "G", Integer.toString(j)));
            }
        }
        for (int i = 0; i < 2; i++) {
            stapel.add(new Karte(50, "W", "4"));
            stapel.add(new Karte(50, "W", ""));

        }
    }

    public void printAll(Stapel s) {
        s.addKarte();
        for (int i = 0; i < stapel.size(); i++) {
            System.out.print(stapel.get(i) + " ");
        }
    }
}
