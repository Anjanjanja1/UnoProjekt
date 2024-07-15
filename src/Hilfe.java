import java.io.File;
import java.io.IOException;

public class Hilfe {

    //Öffnet eine HTML-Datei im Standard-Browser des Betriebssystems
    public static void htmlDateiImBrowserOeffnen(String filePath) {
        try {
            //Erstellt ein File-Objekt mit dem angegebenen Dateipfad
            File htmlFile = new File(filePath);
            //Überprüft, ob die Datei existiert
            if (!htmlFile.exists()) {
                System.out.println("File not found: " + filePath);
                return;
            }
            //Ruft den Namen des Betriebssystems ab
            String os = System.getProperty("os.name").toLowerCase();
            //Überprüft, welches Betriebssystem verwendet wird
            if (os.contains("win")) {
                //Führt den Befehl aus, um die Datei im Edge-Browser unter Windows zu öffnen
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start msedge " + htmlFile.toURI()});
            } else if (os.contains("mac")) {
                //Führt den Befehl aus, um die Datei im Edge-Browser auf einem Mac zu öffnen
                Runtime.getRuntime().exec(new String[]{"open", "-a", "Microsoft Edge", htmlFile.toURI().toString()});
            } else if (os.contains("nix") || os.contains("nux")) {
                //Führt den Befehl aus, um die Datei im Edge-Browser auf Linux/Unix zu öffnen
                Runtime.getRuntime().exec(new String[]{"microsoft-edge", htmlFile.toURI().toString()});
            } else {
                //Gibt eine Meldung aus, wenn das Betriebssystem nicht unterstützt wird
                System.out.println("Unsupported operating system: " + os);
            }
        } catch (IOException e) {
            //Gibt den Stack-Trace aus, wenn eine Ausnahme auftritt
            e.printStackTrace();
        }
    }
}
