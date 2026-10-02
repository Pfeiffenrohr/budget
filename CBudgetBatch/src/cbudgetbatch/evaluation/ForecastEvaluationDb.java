package cbudgetbatch.evaluation;

import cbudgetbatch.DBBatch;
import sonstiges.MyLogger;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Saemtlicher SQL-Zugriff der Forecast-Auswertung.
 *
 * Abhaengig von DBBatch nur, um die bereits offene Connection zu erben.
 * Alle Statements sind PreparedStatements mit gesetzten Parametern. Das ist
 * hier nicht nur Hygiene: die Werte stammen aus Forecast-Zeilen und
 * Kategorienamen, die selbst per String-Konkatenation entstanden sind.
 *
 * Abweichend von DBBatch wird jede SQLException als Ausnahme weitergegeben
 * statt geschluckt, damit ein stiller Leerlauf im Aufräum-Job auffaellt.
 */
public class ForecastEvaluationDb {

    private static final MyLogger logger = new MyLogger();

    private final DBBatch db;

    public ForecastEvaluationDb(DBBatch db) {
        this.db = db;
    }

    private Connection con() {
        return db.con;
    }

    /**
     * Schluessel fuer Kategorie/Konto-Paare. Equals und hashCode sind noetig,
     * weil die Paare in Maps als Schluessel dienen.
     */
    public static final class KategorieKonto {
        private final int kategorie;
        private final int konto;

        public KategorieKonto(int kategorie, int konto) {
            this.kategorie = kategorie;
            this.konto = konto;
        }

        public int getKategorie() {
            return kategorie;
        }

        public int getKonto() {
            return konto;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof KategorieKonto)) {
                return false;
            }
            KategorieKonto other = (KategorieKonto) obj;
            return kategorie == other.kategorie && konto == other.konto;
        }

        @Override
        public int hashCode() {
            return 31 * kategorie + konto;
        }

        @Override
        public String toString() {
            return "KategorieKonto[kategorie=" + kategorie + ", konto=" + konto + "]";
        }
    }

    /**
     * Eine Zeile, die in forecast_snapshot geschrieben werden soll.
     */
    public static final class SnapshotZeile {
        private final int kategorie;
        private final int konto;
        private final LocalDate monatsBeginn;
        private final double wert;

        public SnapshotZeile(int kategorie, int konto, LocalDate monatsBeginn, double wert) {
            this.kategorie = kategorie;
            this.konto = konto;
            this.monatsBeginn = monatsBeginn;
            this.wert = wert;
        }
    }

    // ------------------------------------------------------------------
    //  settings
    // ------------------------------------------------------------------

    public String getSetting(String parameter, String fallback) {
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = con().prepareStatement("select wert from settings where parameter = ?");
            stmt.setString(1, parameter);
            res = stmt.executeQuery();
            if (res.next()) {
                String wert = res.getString(1);
                if (wert != null && wert.trim().length() > 0) {
                    return wert.trim();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Setting " + parameter + " konnte nicht gelesen werden", e);
        } finally {
            close(res);
            close(stmt);
        }
        return fallback;
    }

    public int getIntSetting(String parameter, int fallback) {
        try {
            return Integer.parseInt(getSetting(parameter, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            logger.log("Setting " + parameter + " ist keine Zahl, benutze " + fallback);
            return fallback;
        }
    }

    public double getDoubleSetting(String parameter, double fallback) {
        try {
            return Double.parseDouble(getSetting(parameter, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            logger.log("Setting " + parameter + " ist keine Zahl, benutze " + fallback);
            return fallback;
        }
    }

    // ------------------------------------------------------------------
    //  Snapshot schreiben
    // ------------------------------------------------------------------

    /**
     * Schreibt die Monatsprognosen eines Laufs in einem Rutsch.
     *
     * Pro (erstellt_tag, kategorie, konto, monat_key) erlaubt die Datenbank
     * nur eine Zeile. Ein zweiter Lauf am selben Tag - Container-Neustart
     * oder von Hand ausgeloest - darf deshalb keinen Constraint-Verstoss
     * erzeugen, sondern ueberschreibt den Stand des Tages. Sonst waere der
     * Snapshot des Tages still verloren, weil Forecast das Auswerten als
     * Fehler behandelt und nur protokolliert.
     *
     * @return Anzahl geschriebener Zeilen
     */
    public int insertSnapshots(List<SnapshotZeile> zeilen, String modell, LocalDate heute) {
        if (zeilen == null || zeilen.isEmpty()) {
            return 0;
        }
        Timestamp jetzt = Timestamp.valueOf(heute.atStartOfDay());
        PreparedStatement stmt = null;
        try {
            stmt = con().prepareStatement(
                    "insert into forecast_snapshot (erstellt_tag, erstellt_am, kategorie, konto,"
                            + " monat_tag, monat_key, jahr, wert, modell)"
                            + " values (?,?,?,?,?,?,?,?,?)"
                            + " on conflict (erstellt_tag, kategorie, konto, monat_key) do update set"
                            + " erstellt_am = excluded.erstellt_am,"
                            + " wert = excluded.wert,"
                            + " modell = excluded.modell");
            for (SnapshotZeile zeile : zeilen) {
                stmt.setDate(1, Date.valueOf(heute));
                stmt.setTimestamp(2, jetzt);
                stmt.setInt(3, zeile.kategorie);
                stmt.setInt(4, zeile.konto);
                stmt.setDate(5, Date.valueOf(zeile.monatsBeginn));
                stmt.setString(6, Monat.from(zeile.monatsBeginn).key());
                stmt.setInt(7, zeile.monatsBeginn.getYear());
                stmt.setDouble(8, zeile.wert);
                stmt.setString(9, modell);
                stmt.addBatch();
            }
            int[] ergebnis = stmt.executeBatch();
            logger.log("Snapshot geschrieben: " + ergebnis.length + " Zeilen fuer " + heute);
            return ergebnis.length;
        } catch (SQLException e) {
            throw new IllegalStateException("Snapshot konnte nicht geschrieben werden", e);
        } finally {
            close(stmt);
        }
    }

    public void insertSnapshotMeta(LocalDate heute, long laufDauerMs, boolean computeWeights, String algorithmus) {
        PreparedStatement stmt = null;
        try {
            stmt = con().prepareStatement(
                    "insert into forecast_snapshot_meta (erstellt_tag, erstellt_am, lauf_dauer_ms,"
                            + " compute_weights, algorithmus) values (?,?,?,?,?)"
                            + " on conflict (erstellt_tag) do update set"
                            + " erstellt_am = excluded.erstellt_am,"
                            + " lauf_dauer_ms = excluded.lauf_dauer_ms,"
                            + " compute_weights = excluded.compute_weights,"
                            + " algorithmus = excluded.algorithmus");
            stmt.setDate(1, Date.valueOf(heute));
            stmt.setTimestamp(2, Timestamp.valueOf(heute.atStartOfDay()));
            stmt.setLong(3, laufDauerMs);
            stmt.setBoolean(4, computeWeights);
            stmt.setString(5, algorithmus);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Snapshot-Metadaten konnten nicht geschrieben werden", e);
        } finally {
            close(stmt);
        }
    }

    // ------------------------------------------------------------------
    //  Auswertung lesen
    // ------------------------------------------------------------------

    /**
     * Zeitraeume, die vollstaendig in der Vergangenheit liegen und fuer die
     * es Snapshots gibt. Der laufende Monat fehlt bewusst: die Ist-Daten
     * waeren noch unvollstaendig, und das wuerde jeden Vergleich verfaelschen.
     *
     * @param grenze erster Tag des laufenden Monats
     * @param anzahl so viele der juengsten Zeitraeume, wie bearbeitet werden sollen
     */
    public List<LocalDate> getAuswertbareZeitraeume(LocalDate grenze, int anzahl) {
        List<LocalDate> zeitraeume = new ArrayList<LocalDate>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = con().prepareStatement(
                    "select distinct monat_tag from forecast_snapshot where monat_tag < ?"
                            + " order by monat_tag desc limit ?");
            stmt.setDate(1, Date.valueOf(grenze));
            stmt.setInt(2, anzahl);
            res = stmt.executeQuery();
            while (res.next()) {
                zeitraeume.add(res.getDate(1).toLocalDate());
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Zeitraeume konnten nicht gelesen werden", e);
        } finally {
            close(res);
            close(stmt);
        }
        return zeitraeume;
    }

    /**
     * Alle Staende eines Zeitraums. Die Auswahl des gueltigen Standes
     * uebernimmt SnapshotSelector, nicht das SQL, damit die Regel an einer
     * Stelle steht und testbar ist.
     */
    public List<Snapshot> getSnapshots(LocalDate monatsBeginn) {
        List<Snapshot> snapshots = new ArrayList<Snapshot>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = con().prepareStatement(
                    "select erstellt_tag, kategorie, konto, wert from forecast_snapshot"
                            + " where monat_tag = ? order by kategorie, konto, erstellt_tag");
            stmt.setDate(1, Date.valueOf(monatsBeginn));
            res = stmt.executeQuery();
            while (res.next()) {
                snapshots.add(new Snapshot(
                        monatsBeginn,
                        res.getDate(1).toLocalDate(),
                        res.getInt(2),
                        res.getInt(3),
                        res.getDouble(4)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Snapshots konnten nicht gelesen werden", e);
        } finally {
            close(res);
            close(stmt);
        }
        return snapshots;
    }

    /**
     * Tatsaechliche Werte des Zeitraums je Kategorie und Konto.
     *
     * Forecast-Zeilen sind ausgeschlossen, erkennbar am Muster, mit dem sie
     * Forecast.java und DBBatch.deleteOldForecast sie selbst wieder loeschen.
     * Andernfalls wuerde der Soll-Wert als Ist-Wert verrechnet.
     *
     * @return je Paar die Summe; fehlende Kombinationen fehlen in der Map
     *         und gelten als "keine Ist-Daten"
     */
    public Map<KategorieKonto, Double> getIstWerte(LocalDate monatsBeginn) {
        LocalDate monatsEnde = monatsBeginn.plusMonths(1);
        Map<KategorieKonto, Double> werte = new LinkedHashMap<KategorieKonto, Double>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = con().prepareStatement(
                    "select kategorie, konto_id, sum(wert) from transaktionen"
                            + " where datum >= ? and datum < ?"
                            + " and coalesce(name, '') not like '%Forecast%'"
                            + " and coalesce(kategorie, -1) <> -1 and konto_id is not null"
                            + " group by kategorie, konto_id");
            stmt.setDate(1, Date.valueOf(monatsBeginn));
            stmt.setDate(2, Date.valueOf(monatsEnde));
            res = stmt.executeQuery();
            while (res.next()) {
                werte.put(new KategorieKonto(res.getInt(1), res.getInt(2)), res.getDouble(3));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Ist-Werte konnten nicht gelesen werden", e);
        } finally {
            close(res);
            close(stmt);
        }
        return werte;
    }

    /**
     * Schreibt das Ergebnis eines Zeitraums. Wiederholte Laeufe ueberschreiben
     * vorhandene Zeilen, statt zu duplizieren.
     */
    public void speichereAuswertung(Auswertungszeile zeile, LocalDate berechnetAm) {
        PreparedStatement stmt = null;
        try {
            stmt = con().prepareStatement(
                    "insert into forecast_evaluation (monat_key, jahr, monat, kategorie, konto,"
                            + " snapshot_tag, forecast_wert, ist_wert, abweichung, abweichung_prozent,"
                            + " status, berechnet_am) values (?,?,?,?,?,?,?,?,?,?,?,?)"
                            + " on conflict (monat_key, kategorie, konto) do update set"
                            + " jahr = excluded.jahr,"
                            + " monat = excluded.monat,"
                            + " snapshot_tag = excluded.snapshot_tag,"
                            + " forecast_wert = excluded.forecast_wert,"
                            + " ist_wert = excluded.ist_wert,"
                            + " abweichung = excluded.abweichung,"
                            + " abweichung_prozent = excluded.abweichung_prozent,"
                            + " status = excluded.status,"
                            + " berechnet_am = excluded.berechnet_am");
            Monat monat = Monat.fromKey(zeile.getMonatKey());
            stmt.setString(1, monat.key());
            stmt.setInt(2, zeile.getJahr());
            stmt.setInt(3, zeile.getMonat());
            stmt.setInt(4, zeile.getKategorie());
            stmt.setInt(5, zeile.getKonto());
            stmt.setDate(6, zeile.getSnapshotTag() == null ? null : Date.valueOf(zeile.getSnapshotTag()));
            stmt.setDouble(7, zeile.getForecastWert());
            setNullableDouble(stmt, 8, zeile.getIstWert());
            setNullableDouble(stmt, 9, zeile.getAbweichung());
            setNullableDouble(stmt, 10, zeile.getAbweichungProzent());
            stmt.setString(11, zeile.getStatus());
            stmt.setTimestamp(12, Timestamp.valueOf(berechnetAm.atStartOfDay()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Auswertung konnte nicht gespeichert werden: " + zeile, e);
        } finally {
            close(stmt);
        }
    }

    // ------------------------------------------------------------------
    //  Aufraeumen
    // ------------------------------------------------------------------

    /**
     * Loescht Snapshots, die aelter als die Aufbewahrungsgrenze sind.
     */
    public int deleteSnapshotsAelterAls(LocalDate grenze) {
        PreparedStatement stmt = null;
        try {
            stmt = con().prepareStatement("delete from forecast_snapshot where erstellt_tag < ?");
            stmt.setDate(1, Date.valueOf(grenze));
            int anzahl = stmt.executeUpdate();
            logger.log("Snapshots aelter als " + grenze + " geloescht: " + anzahl);
            return anzahl;
        } catch (SQLException e) {
            throw new IllegalStateException("Snapshots konnten nicht geloescht werden", e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Loescht Snapshot-Zeilen zu Kategorien oder Konten, die es nicht mehr gibt.
     * Ohne das wachsen ueber die Jahre Zeilen auf, deren JOIN in den Views
     * ins Leere laeuft.
     */
    public int deleteVerwaisteSnapshots() {
        PreparedStatement stmt = null;
        try {
            stmt = con().prepareStatement(
                    "delete from forecast_snapshot s where s.id in ("
                            + " select s.id from forecast_snapshot s"
                            + " where not exists (select 1 from kategorien k where k.id = s.kategorie)"
                            + "    or not exists (select 1 from konten ko where ko.id = s.konto))");
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Verwaiste Snapshots konnten nicht geloescht werden", e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Loescht Auswertungszeilen, deren Zeitraum keine Ist-Daten hat und
     * auch keine je bekommen wird. Bei Kategorien, die nie gebucht wurden,
     * waechst die Tabelle sonst ohne Nutzen.
     */
    public int deleteAuswertungOhneDaten(LocalDate grenzeMonat) {
        PreparedStatement stmt = null;
        try {
            stmt = con().prepareStatement(
                    "delete from forecast_evaluation where monat_key < ? and ist_wert is null");
            stmt.setString(1, Monat.from(grenzeMonat.minusMonths(1)).key());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Auswertung konnte nicht aufgeraeumt werden", e);
        } finally {
            close(stmt);
        }
    }

    /**
     * Liefert die Zeilen der Detailtabelle fuer einen CSV-Export.
     *
     * @param jahr null bedeutet "alle Jahre"
     */
    public List<Map<String, String>> getExportZeilen(Integer jahr, Integer kategorie, Integer konto) {
        StringBuilder sql = new StringBuilder(
                "select e.jahr, e.monat, e.monat_key, e.kategorie, k.name, e.konto, ko.kontoname,"
                        + " e.snapshot_tag, e.forecast_wert, e.ist_wert, e.abweichung,"
                        + " e.abweichung_prozent, e.status"
                        + " from forecast_evaluation e"
                        + " left join kategorien k on k.id = e.kategorie"
                        + " left join konten ko on ko.id = e.konto"
                        + " where 1 = 1");
        List<Object> parameter = new ArrayList<Object>();
        if (jahr != null) {
            sql.append(" and e.jahr = ?");
            parameter.add(jahr);
        }
        if (kategorie != null) {
            sql.append(" and e.kategorie = ?");
            parameter.add(kategorie);
        }
        if (konto != null) {
            sql.append(" and e.konto = ?");
            parameter.add(konto);
        }
        sql.append(" order by e.jahr, e.monat, e.kategorie, e.konto");

        List<Map<String, String>> zeilen = new ArrayList<Map<String, String>>();
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = con().prepareStatement(sql.toString());
            for (int i = 0; i < parameter.size(); i++) {
                stmt.setObject(i + 1, parameter.get(i));
            }
            res = stmt.executeQuery();
            String[] spalten = {"jahr", "monat", "monat_key", "kategorie_id", "kategorie",
                    "konto_id", "konto", "snapshot_tag", "forecast_wert", "ist_wert",
                    "abweichung", "abweichung_prozent", "status"};
            while (res.next()) {
                Map<String, String> zeile = new HashMap<String, String>();
                for (int i = 0; i < spalten.length; i++) {
                    zeile.put(spalten[i], res.getString(i + 1));
                }
                zeilen.add(zeile);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Exportdaten konnten nicht gelesen werden", e);
        } finally {
            close(res);
            close(stmt);
        }
        return zeilen;
    }

    private static void setNullableDouble(PreparedStatement stmt, int index, Double wert) throws SQLException {
        if (wert == null) {
            stmt.setNull(index, java.sql.Types.NUMERIC);
        } else {
            stmt.setDouble(index, wert);
        }
    }

    private static void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            logger.log("Ressource konnte nicht geschlossen werden: " + e);
        }
    }
}