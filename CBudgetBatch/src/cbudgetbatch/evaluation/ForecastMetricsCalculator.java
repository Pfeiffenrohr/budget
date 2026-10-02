package cbudgetbatch.evaluation;

import java.util.List;

/**
 * Die Kennzahlenformeln der Forecast-Auswertung, ohne jeden Datenbankzugriff.
 *
 * Bewusst als reine statische Methoden, damit Regeln wie
 * "Forecast nahezu null hat keinen Prozentwert" und "fehlende Ist-Werte
 * zaehlen nicht" direkt getestet werden koennen, statt ueber SQL zu
 * beweisen sein. Die Views in db/forecast_evaluation.sql bilden dieselben
 * Kennzahlen, damit Berechnung und Ausgabe nicht auseinanderlaufen.
 */
public final class ForecastMetricsCalculator {

    /** Zeitraum mit Ist-Daten und sinnvollem Forecast. */
    public static final String STATUS_OK = "ok";
    /** Zeitraum beendet, aber keine Ist-Transaktionen vorhanden. */
    public static final String STATUS_KEIN_IST = "kein_ist";
    /** Forecast nahezu null, Prozentwert waere undefiniert. */
    public static final String STATUS_KEIN_FORECAST = "kein_forecast";
    /** Gar kein Forecast-Stand vor Beginn des Zeitraums vorhanden. */
    public static final String STATUS_KEIN_SNAPSHOT = "kein_snapshot";

    /**
     * Betraege unterhalb dieser Grenze gelten als nahezu null. Der Wert ist
     * bewusst derselbe wie in CalculateWeights.NULL_GRENZE, damit ein
     * Forecast, den die Gewichtungssuche als leer eingestuft hat, auch hier
     * nicht als 0 Prozent Abweichung erscheint.
     */
    public static final double NULL_GRENZE = 0.01;

    /** Fallback, falls settings keinen Wert liefert. */
    public static final double STANDARD_TOLERANZ = 10.0;

    /**
     * Zuschlag beim Vergleich mit der Toleranzgrenze. Deckt den
     * Rundungsfehler ab, den eine Division in Gleitkommazahlen hinterlaesst,
     * ohne die Toleranz praktisch zu verbiegen.
     */
    public static final double GRENZ_TOLERANZ = 1e-9;

    private ForecastMetricsCalculator() {
    }

    public static boolean istNahezuNull(double wert) {
        return Math.abs(wert) < NULL_GRENZE;
    }

    /**
     * Absolute Abweichung mit Vorzeichen: Ist minus Forecast.
     * Positiv heisst, der Forecast lag zu niedrig.
     */
    public static double abweichung(double forecastWert, double istWert) {
        return istWert - forecastWert;
    }

    /**
     * Prozentuale Abweichung: (Ist - Forecast) / Forecast * 100.
     *
     * @return null bei nahezu null Forecast. Der Quotient waere dort
     *         wertlos und wuerde jede Kennzahl sprengen.
     *
     * Achtung bei negativem Forecast: Bei Forecast -100 und Ist -80 ergibt
     * sich -20 Prozent, obwohl 20 Einheiten weniger vorhergesagt wurden. Der
     * Betrag stimmt, das Vorzeichen ist gegenueber der Alltagserwartung
     * gedreht. Das ist die Folge des einheitlichen Quotienten und wird
     * bewusst nicht gesondert behandelt, weil die Views in
     * db/forecast_evaluation.sql genau dieselbe Formel bilden. Beim Lesen der
     * Kennzahlen ist zu beachten, dass der Bias Kategorien mit negativem
     * Forecast gegenueber positiven nicht direkt vergleichbar ist.
     */
    public static Double abweichungProzent(double forecastWert, double istWert) {
        if (istNahezuNull(forecastWert)) {
            return null;
        }
        return (istWert - forecastWert) / forecastWert * 100.0;
    }

    /**
     * Treffer innerhalb der Toleranz. Der Vergleich laeuft ueber die
     * Prozentabweichung, damit er unabhaengig von der Betragshoehe ist.
     *
     * Der Vergleich bekommt einen kleinen Zuschlag. 1.1 gegen 1.0 ergibt in
     * IEEE 754 10.000000000000009 Prozent, waere also nach einem exakten
     * Vergleich kein Treffer, obwohl es fachlich genau 10 Prozent sind. Die
     * Views in db/forecast_evaluation.sql rechnen mit numeric exakt und
     * wuerden denselben Fall als Treffer zaehlen; ohne den Zuschlag wuerden
     * Kennzahl und Tabelle je nach Betragshoehe auseinanderlaufen.
     */
    public static boolean istTreffer(double forecastWert, double istWert, double toleranz) {
        Double prozent = abweichungProzent(forecastWert, istWert);
        if (prozent == null) {
            return false;
        }
        return Math.abs(prozent) <= Math.abs(toleranz) + GRENZ_TOLERANZ;
    }

    /**
     * Bestimmt den Status einer Zeile.
     *
     * Reihenfolge ist fachlich relevant: fehlende Ist-Daten sind ein
     * Datenproblem und werden zuerst genannt, ein nahezu null Forecast
     * waere sonst der Grund fuer einen Prozentwert, den es gar nicht gibt.
     */
    public static String status(double forecastWert, Double istWert) {
        if (istWert == null) {
            return STATUS_KEIN_IST;
        }
        if (istNahezuNull(forecastWert)) {
            return STATUS_KEIN_FORECAST;
        }
        return STATUS_OK;
    }

    /**
     * Fasst beliebig gefilterte Zeilen zu Kennzahlen zusammen.
     */
    public static Aggregat aggregiere(List<Auswertungszeile> zeilen, double toleranz) {
        if (zeilen == null || zeilen.isEmpty()) {
            return Aggregat.leer(toleranz);
        }
        return Aggregat.aus(zeilen, toleranz);
    }
}