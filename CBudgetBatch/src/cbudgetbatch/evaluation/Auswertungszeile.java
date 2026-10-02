package cbudgetbatch.evaluation;

import java.time.LocalDate;

/**
 * Ein Soll-Ist-Vergleich fuer genau einen Zeitraum, eine Kategorie und ein Konto.
 *
 * Enthaelt bewusst nur Rohdaten. Abweichung und Prozentwert werden daraus
 * berechnet (ForecastMetricsCalculator), damit beide Stellen garantiert
 * dieselbe Formel verwenden.
 */
public class Auswertungszeile {

    private final int jahr;
    private final int monat;
    private final String monatKey;
    private final int kategorie;
    private final int konto;
    private final LocalDate snapshotTag;
    private final double forecastWert;
    private final Double istWert;
    private final String status;

    public Auswertungszeile(int jahr, int monat, String monatKey, int kategorie, int konto,
                            LocalDate snapshotTag, double forecastWert, Double istWert, String status) {
        this.jahr = jahr;
        this.monat = monat;
        this.monatKey = monatKey;
        this.kategorie = kategorie;
        this.konto = konto;
        this.snapshotTag = snapshotTag;
        this.forecastWert = forecastWert;
        this.istWert = istWert;
        this.status = status;
    }

    public int getJahr() {
        return jahr;
    }

    public int getMonat() {
        return monat;
    }

    public String getMonatKey() {
        return monatKey;
    }

    public int getKategorie() {
        return kategorie;
    }

    public int getKonto() {
        return konto;
    }

    /**
     * Der Forecast-Stand, gegen den verglichen wurde. Entspricht der Regel
     * "Forecast immer gegen den Stand vor Beginn des prognostizierten
     * Zeitraums". null, wenn gar kein Stand vorlag.
     */
    public LocalDate getSnapshotTag() {
        return snapshotTag;
    }

    public double getForecastWert() {
        return forecastWert;
    }

    /**
     * Tatsaechlicher Wert des Zeitraums. null bedeutet "keine Ist-Daten",
     * nicht "Wert ist 0".
     */
    public Double getIstWert() {
        return istWert;
    }

    public String getStatus() {
        return status;
    }

    public boolean istAuswertbar() {
        return ForecastMetricsCalculator.STATUS_OK.equals(status);
    }

    /**
     * Ist minus Forecast. Nur definiert, wenn Ist-Daten vorliegen.
     */
    public Double getAbweichung() {
        if (istWert == null) {
            return null;
        }
        return ForecastMetricsCalculator.abweichung(forecastWert, istWert);
    }

    /**
     * Prozentuale Abweichung. Nahezu null Forecasts liefern null, weil der
     * Quotient dann nicht sinnvoll ist.
     */
    public Double getAbweichungProzent() {
        if (istWert == null) {
            return null;
        }
        return ForecastMetricsCalculator.abweichungProzent(forecastWert, istWert);
    }

    @Override
    public String toString() {
        return "Auswertungszeile[" + monatKey + " kat=" + kategorie + " konto=" + konto
                + " forecast=" + forecastWert + " ist=" + istWert + " status=" + status + "]";
    }
}