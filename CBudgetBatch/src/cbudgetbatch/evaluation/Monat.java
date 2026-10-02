package cbudgetbatch.evaluation;

import java.time.LocalDate;

/**
 * Rechnen mit Kalendermonaten in der Form 'YYYY-MM'.
 *
 * Der Schluessel ist zugleich der Wert fuer monat_key in beiden neuen
 * Tabellen. Die Umrechnung ist abhaengig von keiner Default-Zeitzone, weil
 * LocalDate benutzt wird; ein Datum wie 2024-01-31 kann damit nicht auf
 * den 29.02. umspringen.
 */
public final class Monat {

    private final int jahr;
    private final int monat;

    private Monat(int jahr, int monat) {
        this.jahr = jahr;
        this.monat = monat;
    }

    public static Monat from(LocalDate datum) {
        return new Monat(datum.getYear(), datum.getMonthValue());
    }

    /**
     * @param key Schluessel der Form 'YYYY-MM'
     * @throws IllegalArgumentException bei ungueltigem Format
     */
    public static Monat fromKey(String key) {
        if (key == null || key.length() != 7 || key.charAt(4) != '-') {
            throw new IllegalArgumentException("Monatsschluessel muss YYYY-MM sein, war: " + key);
        }
        try {
            return new Monat(Integer.parseInt(key.substring(0, 4)), Integer.parseInt(key.substring(5, 7)));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Monatsschluessel muss YYYY-MM sein, war: " + key, e);
        }
    }

    public int getJahr() {
        return jahr;
    }

    public int getMonat() {
        return monat;
    }

    public String key() {
        return String.format("%04d-%02d", jahr, monat);
    }

    /** Erster Tag des Monats. */
    public LocalDate start() {
        return LocalDate.of(jahr, monat, 1);
    }

    /** Erster Tag des Folgemonats, also exklusives Ende. */
    public LocalDate endeExklusiv() {
        return start().plusMonths(1);
    }

    public Monat plus(int monate) {
        return from(start().plusMonths(monate));
    }

    public boolean istVor(LocalDate grenze) {
        return start().isBefore(grenze);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Monat)) {
            return false;
        }
        Monat other = (Monat) obj;
        return jahr == other.jahr && monat == other.monat;
    }

    @Override
    public int hashCode() {
        return jahr * 12 + monat;
    }

    @Override
    public String toString() {
        return key();
    }
}