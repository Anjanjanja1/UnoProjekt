import java.io.File;
import java.io.IOException;

public class Hilfe {

    public static void htmlDateiImBrowserOeffnen(String filePath) {
        try {
            File htmlFile = new File(filePath);
            if (!htmlFile.exists()) {
                System.out.println("File not found: " + filePath);
                return;
            }
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start msedge " + htmlFile.toURI()});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", "-a", "Microsoft Edge", htmlFile.toURI().toString()});
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec(new String[]{"microsoft-edge", htmlFile.toURI().toString()});
            } else {
                System.out.println("Unsupported operating system: " + os);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
