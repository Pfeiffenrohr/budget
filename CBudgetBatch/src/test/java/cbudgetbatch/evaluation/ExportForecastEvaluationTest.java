package cbudgetbatch.evaluation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Aufbau der CSV. Geprueft wird die Spaltenreihenfolge, weil eine verschobene
 * Spalte beim Wiedereinlesen still falsche Zahlen ergaebe.
 */
public class ExportForecastEvaluationTest {

    @Test
    public void kopfzeileEnthaeltAlleSpaltenInDerVereinbartenReihenfolge() {
        assertEquals("jahr;monat;monat_key;kategorie_id;kategorie;konto_id;konto;"
                        + "snapshot_tag;forecast_wert;ist_wert;abweichung;abweichung_prozent;status",
                ExportForecastEvaluation.kopfzeile());
    }

    @Test
    public void spaltenanzahlStimmtMitDerKopfzeileUeberein() {
        assertEquals(13, ExportForecastEvaluation.SPALTEN.size());
        assertEquals(ExportForecastEvaluation.SPALTEN.size(),
                ExportForecastEvaluation.kopfzeile().split(";").length);
    }

    @Test
    public void fehlendeWerteWerdenLeerExportiert() {
        Map<String, String> zeile = new HashMap<String, String>();
        zeile.put("jahr", "2024");
        zeile.put("monat", "3");
        zeile.put("monat_key", "2024-03");

        assertEquals("2024;3;2024-03;;;;;;;;;;", ExportForecastEvaluation.zeile(zeile));
    }

    @Test
    public void kompletteZeileWirdVollstaendigExportiert() {
        Map<String, String> werte = new LinkedHashMap<String, String>();
        werte.put("jahr", "2024");
        werte.put("monat", "3");
        werte.put("monat_key", "2024-03");
        werte.put("kategorie_id", "7");
        werte.put("kategorie", "Lebensmittel");
        werte.put("konto_id", "1");
        werte.put("konto", "Girokonto");
        werte.put("snapshot_tag", "2024-02-20");
        werte.put("forecast_wert", "100.00");
        werte.put("ist_wert", "120.00");
        werte.put("abweichung", "20.00");
        werte.put("abweichung_prozent", "20.00");
        werte.put("status", "ok");

        assertEquals("2024;3;2024-03;7;Lebensmittel;1;Girokonto;2024-02-20;"
                        + "100.00;120.00;20.00;20.00;ok",
                ExportForecastEvaluation.zeile(werte));
    }

    @Test
    public void leereZeilenlisteLiefertNurDieKopfzeile() {
        List<String> csv = ExportForecastEvaluation.alsCsv(Collections.<Map<String, String>>emptyList());

        assertEquals(1, csv.size());
        assertEquals(ExportForecastEvaluation.kopfzeile(), csv.get(0));
    }

    @Test
    public void nullLiefertNurDieKopfzeile() {
        assertEquals(1, ExportForecastEvaluation.alsCsv(null).size());
    }

    @Test
    public void kopfzeileStehtVorAllenDatenzeilen() {
        List<String> csv = ExportForecastEvaluation.alsCsv(Arrays.asList(
                datenzeile("2024-01"), datenzeile("2024-02")));

        assertEquals(3, csv.size());
        assertEquals(ExportForecastEvaluation.kopfzeile(), csv.get(0));
        assertEquals("2024-01", csv.get(1).split(";")[2]);
        assertEquals("2024-02", csv.get(2).split(";")[2]);
    }

    @Test
    public void reihenfolgeDerZeilenBleibtErhalten() {
        // Die Datenbank liefert nach Jahr, Monat, Kategorie, Konto sortiert.
        // Der Export darf diese Reihenfolge nicht umsortieren.
        List<String> csv = ExportForecastEvaluation.alsCsv(Arrays.asList(
                datenzeile("2023-12"), datenzeile("2024-01"), datenzeile("2024-02")));

        assertEquals("2023-12", csv.get(1).split(";")[2]);
        assertEquals("2024-01", csv.get(2).split(";")[2]);
        assertEquals("2024-02", csv.get(3).split(";")[2]);
    }

    private static Map<String, String> datenzeile(String monatKey) {
        Map<String, String> werte = new HashMap<String, String>();
        for (String spalte : ExportForecastEvaluation.SPALTEN) {
            werte.put(spalte, "");
        }
        werte.put("monat_key", monatKey);
        return werte;
    }
}