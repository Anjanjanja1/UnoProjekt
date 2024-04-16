import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Spiel spiel = new Spiel(input, System.out);
        spiel.run();
        input.close();
    }
}