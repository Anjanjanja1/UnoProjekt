import java.io.PrintStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        PrintStream output = System.out;
        Spiel spiel = new Spiel(input, output);

        //hola
        // Startet das Spiel
        spiel.run();
        input.close();
    }
}