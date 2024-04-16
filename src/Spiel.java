import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Spiel {
    protected final Scanner input;
    protected final PrintStream output;
    protected ArrayList<Spieler> spieler;
    protected ArrayList<Karte> stapel;

    public Spiel(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;
        this.spieler = new ArrayList<>();
        this.stapel = new ArrayList<>();
    }


    //die Gameloop
    public void run() {
        initialisieren();
        stapelSuffle();

        while (true) {
            benutzernameInput();

        }
    }

    public void initialisieren() {
        System.out.println("Wilkommen zu unserem UNO Spiel!");


    }

    public void benutzernameInput() {
        for (int i = 0; i <= 4; i++) {
            System.out.println("Bitte gib den Namen von Spieler " + (i + 1) + " ein: ");
            String name = input.nextLine();
            spieler.add(new Spieler(name));
        }
    }

    public void stapelSuffle(){
        Stapel stapel = new Stapel();
        stapel.addKarte();
        stapel.printAll(stapel);
    }


}
