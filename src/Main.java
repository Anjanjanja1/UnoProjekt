import java.io.PrintStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        PrintStream output = System.out;
        Spiel spiel = new Spiel(input, output);

        spiel.run();
        input.close();
    }
}