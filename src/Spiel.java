import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Spiel {
    protected final Scanner input;
    protected final PrintStream output;
    protected ArrayList<Spieler> spielerListe;
    protected ArrayList<Karte> stapel;
    protected Spieler aktuellerSpieler;


    public Spiel(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;
        this.spielerListe = new ArrayList<>();
        this.stapel = new ArrayList<>();
        this.aktuellerSpieler = null;
    }


    //die Gameloop
    public void run() {
        initialisieren();
        benutzernameInput();

        Stapel stapel = new Stapel();
        //stapel.addKarte();
        stapel.stapelSuffleUndTeilen(stapel.addKarte(), spielerListe, 7);
        menu();

        while (true) {
            benutzernameInput();

        }
    }

    public void initialisieren() {
        System.out.println("Wilkommen zu unserem UNO Spiel!");
    }

    public void benutzernameInput() {
        for (int i = 0; i < 4; i++) {
            System.out.println("Bitte gib den Namen von Spieler " + (i + 1) + " ein: ");
            String name = input.nextLine();
            Spieler spieler = new Spieler(name);
            spieler.addSpieler(spieler);
            if (i == 0) {
                aktuellerSpieler = spieler;
            }
        }
    }

//    public void stapelTeilen() {
//        for (Spieler s : spieler) {
//            spieler.add(new ArrayList<Karte>(stapel));
//        }
//    }

    public void menu() {
        System.out.println("\nSpielername: " + aktuellerSpieler.getName());
        System.out.println("Top Karte: " ); //TODO
        System.out.println("Deine Karte: " + Spieler.meineKarte); //TODO
        System.out.println("Mögliche Karte: "); //TODO

        System.out.println("MENÜ: \n 1. Karte heben \n 2. Karte legen \n 3. Uno sagen \n 4. Nächster Spieler \n Geben Sie Ihre Wahl ein: ");
        int menuAuswahl = input.nextInt();

        while (true) {

            switch (menuAuswahl) {
                case 1:
                    //TODO
                    break;
                case 2:
                    //TODO
                    break;
                case 3:
                    //TODO
                    break;
                case 4:
                    //TODO
                    break;
                default:
                    System.out.println("Ungültige Eingabe.");
            }
            System.out.println("Geben Sie Ihre Wahl ein: ");
            menuAuswahl = input.nextInt();
        }
    }
}
