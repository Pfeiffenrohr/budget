package cbudgetbatch.evaluation;

import java.time.LocalDate;

/**
 * Ein Forecast-Stand fuer genau einen Zeitraum, eine Kategorie und ein Konto.
 *
 * Wird sowohl aus der Datenbank gelesen als auch in Tests direkt erzeugt.
 */
public class Snapshot {

    private final LocalDate monatsBeginn;
    private final LocalDate erstelltTag;
    private final int kategorie;
    private final int konto;
    private final double wert;

    public Snapshot(LocalDate monatsBeginn, LocalDate erstelltTag, int kategorie, int konto, double wert) {
        this.monatsBeginn = monatsBeginn;
        this.erstelltTag = erstelltTag;
        this.kategorie = kategorie;
        this.konto = konto;
        this.wert = wert;
    }

    /** Fuer Tests und Auswertung ohne Kategoriebezug. */
    public Snapshot(LocalDate monatsBeginn, LocalDate erstelltTag, double wert) {
        this(monatsBeginn, erstelltTag, -1, -1, wert);
    }

    /** Erster Tag des prognostizierten Zeitraums. */
    public LocalDate getMonatsBeginn() {
        return monatsBeginn;
    }

    /** Tag, an dem der Forecast berechnet wurde. */
    public LocalDate getErstelltTag() {
        return erstelltTag;
    }

    public int getKategorie() {
        return kategorie;
    }

    public int getKonto() {
        return konto;
    }

    /** Prognostizierter Betrag fuer den Zeitraum. */
    public double getWert() {
        return wert;
    }

    @Override
    public String toString() {
        return "Snapshot[monat=" + monatsBeginn + " erstellt=" + erstelltTag
                + " kategorie=" + kategorie + " konto=" + konto + " wert=" + wert + "]";
    }
}