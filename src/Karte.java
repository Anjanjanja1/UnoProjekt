public class Karte {
    protected int punkte; //Punktzahl der Karte
    protected String farbe; //Farbe der Karte
    protected String zeichen; //Zeichen der Karte (zahl oder Aktionssymbol)

    //Konstruktor
    public Karte(int punkte, String farbe, String zeichen) {
        this.punkte = punkte;
        this.farbe = farbe;
        this.zeichen = zeichen;
    }

    //Standardkonstruktor
    public Karte(){}

    //Getters und Setters
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

    @Override
    public String toString() {
        return farbe + zeichen;
    }
}
