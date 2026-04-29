package cbudgetbatch;

import cbudgetbase.DB;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class CalculatePlanInOutHistory {

    public void planInOutHistory(DBBatch db) {

        Vector allPlanungen = db.getAllPlanungen();
        for (int j = 0; j < allPlanungen.size(); j++) {
            String plan_id = ((Hashtable) allPlanungen.elementAt(j)).get("plan_id").toString();
            String planName = ((Hashtable) allPlanungen.elementAt(j)).get("name").toString();
            System.out.println(" Bearbeite Plan " +  planName);
            try {
                Double einnahmeSumme = 0.0;
                Double ausgabeSumme = 0.0;
                Double einnahmeSummeGeplant = 0.0;
                Double ausgabeSummeGeplant = 0.0;
                //FileHandling fh = new FileHandling();

                Calendar cal_akt = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String akt_datum = formatter.format(cal_akt.getTime());


                String zeit = "monat";

                Hashtable hash_plan = db.getPlanungen(plan_id);
                String batch = (String) hash_plan.get("batch");
                //Alle Kategorien in dieser Planung
                //Vector kat_aus=db.getAllPlanungsKategorien(plan_id);
                Vector kat_aus = db.getAllKategorien("ausgabe");
                Vector kat_ein = db.getAllKategorien("einnahme");
                String rule_id = ((Integer) hash_plan.get("rule_id")).toString();
                String rule;

                if (rule_id.equals("-1")) {
                    //dummy

                    rule = "";
                } else {
                    rule = " AND " + db.getRuleCommand(new Integer(rule_id));
                }
                Integer avgDuration = db.getAvgCumputaionTimeForPlanningJobs(plan_id);
                Calendar cal_begin = Calendar.getInstance();
                Calendar cal_end = Calendar.getInstance();
                cal_begin.setTime((Date) hash_plan.get("startdatum"));
                cal_end.setTime((Date) hash_plan.get("enddatum"));


                if (cal_akt.after(cal_end)) {
                    System.out.println("Ende de Planungszeitraums erreicht");
                    cal_akt = (Calendar) cal_end.clone();
                    akt_datum = formatter.format(cal_akt.getTime());
                }
                if (zeit.equals("monat")) {
                    //Get last Date of Plan
                    Date lastDate = (Date) hash_plan.get("enddatum");
                    cal_akt.setTime(lastDate);
                    //cal_akt.set(Calendar.DAY_OF_MONTH, cal_akt.getActualMaximum(Calendar.DAY_OF_MONTH));
                    akt_datum = formatter.format(cal_akt.getTime());
                    System.out.println("Akt_datum = " + akt_datum);
                }
                Long akt_zeit = cal_akt.getTimeInMillis();
                Long begin_zeit = cal_begin.getTimeInMillis();
                long vergangene_tage = (akt_zeit - begin_zeit) / (3600000 * 24);
                Long end_zeit = cal_end.getTimeInMillis();
                long gesamte_tage = (end_zeit - begin_zeit) / (3600000 * 24);
                //double faktor=((double)vergangene_tage*100)/(double)gesamte_tage;
                double faktor = ((double) vergangene_tage) / (double) gesamte_tage;
                System.out.println("faktor = " + faktor);

                Double wert_relativ;
                Double summe;
                Double prozent;
                for (int i = 0; i < kat_aus.size(); i++) {
                    String kategorie = (String) ((Hashtable) kat_aus.elementAt(i)).get("name");
                    wert_relativ = db.getKategorienAlleRecursivPlanung(kategorie, new Integer(plan_id), faktor);
                    summe = db.getKategorienAlleRecursivSumme(kategorie, formatter.format(hash_plan.get("startdatum")), akt_datum, rule, new Integer(plan_id));
                    // prozent=0.0;
                    if (wert_relativ == 0.0) {
                        prozent = 0.0;
                    } else {
                        prozent = (summe * 100 / wert_relativ);
                    }

                }
                //alle Ausgaben
                wert_relativ = db.getPlanungAllWhere(new Integer(plan_id), faktor, buildWhere(db, "ausgabe", plan_id, ""));
                summe = db.getKategorienAlleSummeWhere(formatter.format(hash_plan.get("startdatum")), akt_datum, buildWhere(db, "ausgabe", plan_id, rule));
                prozent = 0.0;
                if (wert_relativ == 0.0) {
                    prozent = 0.0;
                } else {
                    prozent = (summe * 100 / wert_relativ);
                }
                if (wert_relativ != 0.0) {
                    ausgabeSumme = summe;
                    ausgabeSummeGeplant = wert_relativ;

                }


                for (int i = 0; i < kat_ein.size(); i++) {
                    String kategorie = (String) ((Hashtable) kat_ein.elementAt(i)).get("name");
                    wert_relativ = db.getKategorienAlleRecursivPlanung(kategorie, new Integer(plan_id), faktor);
                    summe = db.getKategorienAlleRecursivSumme(kategorie, formatter.format(hash_plan.get("startdatum")), akt_datum, rule);
                    prozent = 0.0;
                    if (wert_relativ == 0.0) {
                        prozent = 0.0;
                    } else {
                        prozent = (summe * 100 / wert_relativ);
                    }
                }

                //alle Einnahmen
                wert_relativ = db.getPlanungAllWhere(new Integer(plan_id), faktor, buildWhere(db, "einnahme", plan_id, ""));
                summe = db.getKategorienAlleSummeWhere(formatter.format(hash_plan.get("startdatum")), akt_datum, buildWhere(db, "einnahme", plan_id, rule));
                prozent = 0.0;
                if (wert_relativ == 0.0) {
                    prozent = 0.0;
                } else {
                    prozent = (summe * 100 / wert_relativ);
                }
                if (wert_relativ != 0.0) {

                    einnahmeSumme = summe;
                    einnahmeSummeGeplant = wert_relativ;

                }

                Double differenzGeplant = ausgabeSummeGeplant + einnahmeSummeGeplant;

                Double differenz = ausgabeSumme + einnahmeSumme;
                System.out.println("Die Differenz von " +planName + " ist "+ formater(differenz));

            } catch (Throwable theException) {
                theException.printStackTrace();
            }
        }
    }

    private String formater(Double d) {
        String str = "";
        DecimalFormat f = new DecimalFormat("#0.00");
        str = str + f.format(d);
        return str;

    }

    String buildWhere(DB db, String mode, String plan_id, String rule) {
        Vector all = db.getAllKategorien(mode);
        Vector allIds = new Vector();
        for (int i = 0; i < all.size(); i++) {
            if (!db.getPlanWertNull(plan_id, (Integer) ((Hashtable) all.elementAt(i)).get("id"))) {
                allIds.add((Integer) ((Hashtable) all.elementAt(i)).get("id"));
                //hf.searchSub(allIds,(String)((Hashtable)all.elementAt(i)).get("name") , (Integer)((Hashtable)all.elementAt(i)).get("id"), all,0);
            }
        }
        //System.err.println(allIds);
        String where = "(";
        boolean first = true;
        for (int i = 0; i < all.size(); i++) {
            if (allIds.contains((Integer) ((Hashtable) all.elementAt(i)).get("id"))) {
                if (!first) {
                    where = where + " or ";
                } else {
                    first = false;
                }
                where = where + "kategorie= " + ((Hashtable) all.elementAt(i)).get("id");
            }
        }
        where = where + ")" + rule;
        if (where.equals("()"))
            where = "(kategorie= -1)";

        return where;
    }

}
