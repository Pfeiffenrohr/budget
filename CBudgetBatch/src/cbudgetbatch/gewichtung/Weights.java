package cbudgetbatch.gewichtung;

/**
 * Ergebnis der Gewichtssuche.
 *
 * Die Gewichte werden als Ganzzahl 0..99 gespeichert und von
 * OverAllTable.gewichteWert durch Division mit 50 in den Bereich
 * 0..1.98 umgerechnet. Die Spalten y1/y2/y3 in forecast_weights
 * bekommen daher keine Prozentwerte, sondern diese Rohgewichte.
 */
public class Weights {

    private final double y1;
    private final double y2;
    private final double y3;
    private final double precision;

    public Weights(double y1, double y2, double y3, double precision) {
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
        this.precision = precision;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public double getY3() {
        return y3;
    }

    /**
     * Quadratische Abweichung des besten gefundenen Gewichtsvektors.
     * Dient als Guete-Mass ("precision" in forecast_weights).
     */
    public double getPrecision() {
        return precision;
    }

    @Override
    public String toString() {
        return "Weights[y1=" + y1 + ", y2=" + y2 + ", y3=" + y3 + ", precision=" + precision + "]";
    }
}