import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Spiel {
    protected final Scanner input;
    protected final PrintStream output;
    protected ArrayList<Spieler> spielerListe;
    protected Stapel stapel;
    protected Spieler aktuellerSpieler;


    public Spiel(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;
        this.spielerListe = new ArrayList<>();
        this.stapel = new Stapel();
        this.aktuellerSpieler = null;
    }

    //die Gameloop
    public void run() {
        initialisieren();
        benutzernameInput();

        stapel.addKarte();
        stapel.entferneErsteKarte();
        stapel.stapelShuffleUndTeilen(spielerListe, 7);
        aktuellerSpieler = spielerListe.get(0);
        menu();
    }

    public void initialisieren() {
        System.out.println("Wilkommen zu unserem UNO Spiel!");
    }

    public void benutzernameInput() {
        for (int i = 0; i < 4; i++) {
            System.out.println("Bitte gib den Namen von Spieler " + (i + 1) + " ein: ");
            String name = input.nextLine();
            Spieler spieler = new Spieler(name);
            Spieler.addSpieler(spieler);
            spielerListe.add(spieler);
        }
    }

    public void menu() {
        while (true) {
            output.println("\nSpielername: " + aktuellerSpieler.getName());
            output.println("Top Karte: " + stapel.getAblageStapel().getAblageStapel().get(stapel.getAblageStapel().getAblageStapel().size() - 1));
            output.println("Deine Karte: " + aktuellerSpieler.meineKarte);
            output.println("Mögliche Karte: " + gueltigeKarten()); //TODO

            output.println("MENÜ: \n 1. Karte heben \n 2. Karte legen \n 3. Uno sagen \n 4. Nächster Spieler \n Geben Sie Ihre Wahl ein: ");
            int menuAuswahl = input.nextInt();
            input.nextLine();

            switch (menuAuswahl) {
                case 1:
                    karteHeben();
                    break;
                case 2:
                    karteLegen();
                    break;
                case 3:
                    unoSagen();
                    break;
                case 4:
                    naechsterSpieler();
                    break;
                default:
                    output.println("Ungültige Eingabe.");
            }
        }
    }

    public ArrayList<Karte> gueltigeKarten() {
        ArrayList<Karte> gueltigeKarten = new ArrayList<>();

        Karte obersteKarte = stapel.getAblageStapel().getAblageStapel().get(stapel.getAblageStapel().getAblageStapel().size() - 1);

        for (Karte karte : aktuellerSpieler.meineKarte) {
            if (karte.getFarbe().contains(obersteKarte.getFarbe()) || karte.getZeichen().contains(obersteKarte.getZeichen()) || karte.getFarbe().contains("WILD")) {
                gueltigeKarten.add(karte);
            }
        }
        return gueltigeKarten;
    }

    public void karteHeben() {
        if (!stapel.getStapel().isEmpty()) {
            Karte gezogeneKarte = stapel.getStapel().remove(0);
            aktuellerSpieler.addKarten(gezogeneKarte);
            output.println("Du hast die Karte " + gezogeneKarte + " gezogen.");
        } else {
            output.println("Der Stapel ist leer.");
        }
    }


    public void karteLegen() {
        output.println("Welche Karte möchtest du legen? (Index eingeben)");
        int index = input.nextInt();
        input.nextLine();

        if (index >= 0 && index < aktuellerSpieler.meineKarte.size()) {
            Karte gelegteKarte = aktuellerSpieler.meineKarte.remove(index);
            stapel.getAblageStapel().getAblageStapel().add(gelegteKarte);
            output.println("Du hast die Karte " + gelegteKarte + " gelegt.");
        } else {
            output.println("Ungültiger Index!");
        }
    }

    public void unoSagen() {
        output.println("Spieler " + aktuellerSpieler.getName() + " hat UNO gesagt!");
        if (aktuellerSpieler.meineKarte.size() > 1) {
            output.println("Du hast nicht zur richtigen Zeit UNO gesagt. Du bekommst 2 Karten als Strafe!");
        }
//        aktuellerSpieler.meineKarte.add(stapel.getStapel().getFirst());
//        aktuellerSpieler.meineKarte.add(stapel.getStapel().getFirst()); //TODO
        karteHeben();
        karteHeben();
        naechsterSpieler();
    }

    public void naechsterSpieler() {
        int aktuellerIndex = spielerListe.indexOf(aktuellerSpieler);
        aktuellerSpieler = spielerListe.get((aktuellerIndex + 1) % spielerListe.size());
        output.println("Der nächtste Spieler ist: " + aktuellerSpieler.getName());

//        if (stapel.getAblageStapel().equals("U")) {
//            aktuellerSpieler = spielerListe;
//        }
    }

    public void ueberpruefeKarte() {
    }
}
