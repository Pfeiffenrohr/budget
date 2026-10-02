package cbudgetbatch.evaluation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Waeht aus mehreren Forecast-Staenden denjenigen aus, der fuer einen
 * Zeitraum historisch korrekt ist.
 *
 * Fachliche Regel: "Ein Forecast wird immer gegen den Stand verglichen, der
 * vor Beginn des prognostizierten Zeitraums erstellt wurde." Also gilt
 * strikt erstelltTag < monatsBeginn. Ein Stand, der exakt auf den ersten Tag
 * des Monats faellt, zaehlt nicht mehr, weil er die Daten des Monats schon
 * kennen koennte.
 *
 * Damit spaetere Berechnungen das Ergebnis von frueheren nicht nachtraeglich
 * veraendern koennen, gewinnt der Stand mit dem neuesten Datum. Die Auswahl
 * ist fuer eine Zeitraum/Kategorie/Konto-Kombination eindeutig, weil die
 * Datenbank je (erstellt_tag, kategorie, konto, monat_key) nur eine Zeile
 * zulässt (forecast_snapshot_uq). Ein Gleichstand kann deshalb nicht
 * auftreten; die Reihenfolge der Eingabe spielt keine Rolle.
 */
public final class SnapshotSelector {

    private SnapshotSelector() {
    }

    /**
     * @return der gueltige Stand, oder null wenn kein Stand vor Zeitraumbeginn existiert
     */
    public static Snapshot select(Collection<Snapshot> kandidaten, LocalDate monatsBeginn) {
        Snapshot bester = null;
        if (kandidaten == null) {
            return null;
        }
        for (Snapshot kandidat : kandidaten) {
            if (kandidat == null || kandidat.getErstelltTag() == null) {
                continue;
            }
            if (!kandidat.getErstelltTag().isBefore(monatsBeginn)) {
                continue;
            }
            if (bester == null || !kandidat.getErstelltTag().isBefore(bester.getErstelltTag())) {
                bester = kandidat;
            }
        }
        return bester;
    }

    /**
     * Gruppiert nach Zeitraum und waeht je Zeitraum den gueltigen Stand.
     * Die Eingabemenge darf mehrere Kategorien und Konten enthalten.
     *
     * @return je Monatsbeginn der Listen mit den gueltigen Staenden; Zeitraeume
     *         ohne gueltigen Stand fehlen in der Antwort
     */
    public static List<Snapshot> selectJeZeitraum(Collection<Snapshot> kandidaten) {
        List<Snapshot> ergebnis = new ArrayList<Snapshot>();
        List<LocalDate> monate = new ArrayList<LocalDate>();
        for (Snapshot kandidat : kandidaten) {
            if (kandidat != null && kandidat.getMonatsBeginn() != null
                    && !monate.contains(kandidat.getMonatsBeginn())) {
                monate.add(kandidat.getMonatsBeginn());
            }
        }
        for (LocalDate monat : monate) {
            List<Snapshot> desMonats = new ArrayList<Snapshot>();
            for (Snapshot kandidat : kandidaten) {
                if (kandidat != null && monat.equals(kandidat.getMonatsBeginn())) {
                    desMonats.add(kandidat);
                }
            }
            Snapshot gewaehlt = select(desMonats, monat);
            if (gewaehlt != null) {
                ergebnis.add(gewaehlt);
            }
        }
        return ergebnis;
    }
}