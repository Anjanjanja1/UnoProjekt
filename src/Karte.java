import java.util.ArrayList;

public class Karte {
    protected int punkte;
    protected String farbe;
    protected String zeichen;

    public Karte(int punkte, String farbe, String zeichen) {
        this.punkte = punkte;
        this.farbe = farbe;
        this.zeichen = zeichen;
    }

    public String getZeichen() {
        return zeichen;
    }

    public void setZeichen(String zeichen) {
        this.zeichen = zeichen;
    }

    public String getFarbe() {
        return farbe;
    }

    public void setFarbe(String farbe) {
        this.farbe = farbe;
    }

    public int getPunkte() {
        return punkte;
    }

    public void setPunkte(int punkte) {
        this.punkte = punkte;
    }
}
