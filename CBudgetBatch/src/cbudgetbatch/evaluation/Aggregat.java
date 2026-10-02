package cbudgetbatch.evaluation;

import java.util.List;

/**
 * Zusammenfassung der Kennzahlen fuer einen beliebigen Filter (Jahr,
 * Kategorie, Konto).
 *
 * Wird bewusst nur aus Zeilen mit Status "ok" gebildet. Fehlende Ist-Werte
 * und nahezu null Forecasts bleiben aussen vor, so wie es die fachliche
 * Anforderung verlangt. Die Zahl der ausgeschlossenen Zeilen wird
 * mitgefuehrt, damit die Detailtabelle das erklaeren kann.
 */
public class Aggregat {

    private final int anzahlGesamt;
    private final int anzahlAusgewertet;
    private final int anzahlOhneIst;
    private final int anzahlOhneForecastwert;
    private final Double mittelAbweichung;
    private final Double mittelAbsAbweichung;
    private final Double biasProzent;
    private final Double mittelAbsProzent;
    private final Double trefferquoteProzent;
    private final Double staerksteUeberschatzung;
    private final Double staerksteUnterschaetzung;
    private final double toleranz;

    Aggregat(int anzahlGesamt, int anzahlAusgewertet, int anzahlOhneIst, int anzahlOhneForecastwert,
             Double mittelAbweichung, Double mittelAbsAbweichung, Double biasProzent,
             Double mittelAbsProzent, Double trefferquoteProzent,
             Double staerksteUeberschatzung, Double staerksteUnterschaetzung, double toleranz) {
        this.anzahlGesamt = anzahlGesamt;
        this.anzahlAusgewertet = anzahlAusgewertet;
        this.anzahlOhneIst = anzahlOhneIst;
        this.anzahlOhneForecastwert = anzahlOhneForecastwert;
        this.mittelAbweichung = mittelAbweichung;
        this.mittelAbsAbweichung = mittelAbsAbweichung;
        this.biasProzent = biasProzent;
        this.mittelAbsProzent = mittelAbsProzent;
        this.trefferquoteProzent = trefferquoteProzent;
        this.staerksteUeberschatzung = staerksteUeberschatzung;
        this.staerksteUnterschaetzung = staerksteUnterschaetzung;
        this.toleranz = toleranz;
    }

    public int getAnzahlGesamt() {
        return anzahlGesamt;
    }

    public int getAnzahlAusgewertet() {
        return anzahlAusgewertet;
    }

    public int getAnzahlOhneIst() {
        return anzahlOhneIst;
    }

    public int getAnzahlOhneForecastwert() {
        return anzahlOhneForecastwert;
    }

    /** Mittlere Abweichung mit Vorzeichen. Positiv = ueberschaetzt. */
    public Double getMittelAbweichung() {
        return mittelAbweichung;
    }

    public Double getMittelAbsAbweichung() {
        return mittelAbsAbweichung;
    }

    /** Durchschnittliche Ueber- oder Unterschaetzung in Prozent. */
    public Double getBiasProzent() {
        return biasProzent;
    }

    public Double getMittelAbsProzent() {
        return mittelAbsProzent;
    }

    /** Anteil der auswertbaren Zeitraeume innerhalb der Toleranz, in Prozent. */
    public Double getTrefferquoteProzent() {
        return trefferquoteProzent;
    }

    public Double getStaerksteUeberschatzung() {
        return staerksteUeberschatzung;
    }

    public Double getStaerksteUnterschaetzung() {
        return staerksteUnterschaetzung;
    }

    public double getToleranz() {
        return toleranz;
    }

    /**
     * Liefert ein leeres Aggregat. Kennzahlen sind dann null statt 0, damit
     * die Oberflaeche "keine Daten" von "0 Prozent Abweichung" unterscheiden kann.
     */
    public static Aggregat leer(double toleranz) {
        return new Aggregat(0, 0, 0, 0, null, null, null, null, null, null, null, toleranz);
    }

    static Aggregat aus(List<Auswertungszeile> zeilen, double toleranz) {
        int anzahlGesamt = zeilen.size();
        int anzahlAusgewertet = 0;
        int anzahlProzent = 0;
        int anzahlOhneIst = 0;
        int anzahlOhneForecastwert = 0;
        double summeAbweichung = 0.0;
        double summeAbsAbweichung = 0.0;
        double summeBias = 0.0;
        double summeAbsProzent = 0.0;
        int treffer = 0;
        Double staerksteUeber = null;
        Double staerksteUnter = null;

        for (Auswertungszeile zeile : zeilen) {
            if (ForecastMetricsCalculator.STATUS_KEIN_IST.equals(zeile.getStatus())) {
                anzahlOhneIst++;
                continue;
            }
            if (ForecastMetricsCalculator.STATUS_KEIN_FORECAST.equals(zeile.getStatus())) {
                anzahlOhneForecastwert++;
                continue;
            }
            if (!zeile.istAuswertbar()) {
                continue;
            }
            anzahlAusgewertet++;

            Double abweichung = zeile.getAbweichung();
            if (abweichung != null) {
                summeAbweichung += abweichung;
                summeAbsAbweichung += Math.abs(abweichung);
                if (staerksteUeber == null || abweichung > staerksteUeber) {
                    staerksteUeber = abweichung;
                }
                if (staerksteUnter == null || abweichung < staerksteUnter) {
                    staerksteUnter = abweichung;
                }
            }
            Double prozent = zeile.getAbweichungProzent();
            if (prozent != null) {
                anzahlProzent++;
                summeBias += prozent;
                summeAbsProzent += Math.abs(prozent);
                if (ForecastMetricsCalculator.istTreffer(zeile.getForecastWert(), zeile.getIstWert(), toleranz)) {
                    treffer++;
                }
            }
        }

        if (anzahlAusgewertet == 0) {
            return new Aggregat(anzahlGesamt, 0, anzahlOhneIst, anzahlOhneForecastwert,
                    null, null, null, null, null, null, null, toleranz);
        }

        Double mittelAbweichung = summeAbweichung / anzahlAusgewertet;
        Double mittelAbsAbweichung = summeAbsAbweichung / anzahlAusgewertet;
        if (anzahlProzent == 0) {
            return new Aggregat(anzahlGesamt, anzahlAusgewertet, anzahlOhneIst, anzahlOhneForecastwert,
                    mittelAbweichung, mittelAbsAbweichung,
                    null, null, null, staerksteUeber, staerksteUnter, toleranz);
        }
        return new Aggregat(anzahlGesamt, anzahlAusgewertet, anzahlOhneIst, anzahlOhneForecastwert,
                mittelAbweichung, mittelAbsAbweichung,
                summeBias / anzahlProzent,
                summeAbsProzent / anzahlProzent,
                100.0 * treffer / anzahlProzent,
                staerksteUeber, staerksteUnter, toleranz);
    }
}