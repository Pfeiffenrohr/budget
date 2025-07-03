package cbudgetbatch;

import cbudgetbatch.gewichtung.OverAllTable;
import cbudgetbatch.gewichtung.YearTable;
import sonstiges.MyLogger;
import cbudgetbatch.gewichtung.ForecastWriteDataToFile;

import java.text.SimpleDateFormat;
import java.util.*;


public class Forecast {
    static String user;
    static String pass;
    static String datenbank;
    static String computeWeights;
    DBBatch db = new DBBatch();

    private static final MyLogger logger = new MyLogger();

    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("usage: budget_server <user> <password> <datenbank> <computeWeights");
            System.exit(1);
        }
        user = args[0];
        pass = args[1];
        datenbank = args[2];
        computeWeights = args[3];

        // Server example = new Server();
        DBBatch db = new DBBatch();
        if (!db.dataBaseConnect(user, pass, datenbank)) {
            System.err.println("Konnte mich nicht mit der Datenbank verbinden");
            System.exit(1);
        }
        if (computeWeights.equals("ja")) {
            ForecastWriteDataToFile fwdtf = new ForecastWriteDataToFile();
            fwdtf.startCalculateWeights(db);
        }
        Forecast forecast = new Forecast();
        forecast.getAllKategoriesWithForecast(db);
        db.closeConnection();
    }

    private void getAllKategoriesWithForecast(DBBatch db) {
        logger.log("Starte Berechung Forecast ...");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calOneYearBack = Calendar.getInstance();
        Calendar calTowYearBack = Calendar.getInstance();
        Calendar calThreeYearBack = Calendar.getInstance();
        Calendar calnow = Calendar.getInstance();
        // Drei Jahre zurück rechnen
        calThreeYearBack.add(Calendar.YEAR, -3);
        calTowYearBack.add(Calendar.YEAR, -2);
        calOneYearBack.add(Calendar.YEAR, -1);
        // Erst mal alle Kategorien holen.
        Vector kategories = db.getAllKategorien();
        // Dann alle Konten
        Vector konten = db.getAllKonto();
        Hashtable settings = db.getSettings();
        // Dann von allen Kategorien den Durchschnitt der letzten drei Jahre holen und
        // den Durchschnitt pro Monat ausrechnen.

        for (int i = 0; i < kategories.size(); i++) {


            for (int j = 0; j < konten.size(); j++) {
                Hashtable kategorie = (Hashtable) kategories.elementAt(i);
                Hashtable konto = (Hashtable) konten.elementAt(j);
                double inflation = 0.0;
                double inflationDay = 0.0;
                String where = " kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id")
                        + " and planed = 'j' and name like 'Forecast%' ";
                db.deleteTransaktionWithWhere(where);
                if (kategorie.get("forecast").equals(0)) {
                    continue;
                }
                if ((Integer) kategorie.get("inflation") == 1) {
                    inflationDay = new Double((String) settings.get("inflation"));
                    inflationDay = inflationDay / 365;
                    inflationDay = inflationDay / 100;
                }
                where = "kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id") + "and cycle = 0";

                double[][] montharry = new double[12][3];
                //Hier machen wir das Ganze 3 Mal hintereinander für jedes Jahr. Das sollte man eigentlich besser machen.
                OverAllTable oat = new OverAllTable();
                Map<Integer, Double> mapYear3 = new HashMap<>();
                YearTable yt3 = new YearTable();
                mapYear3 = db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calThreeYearBack.getTime()),
                        formatter.format(calTowYearBack.getTime()), where);
                yt3.setMapYear(mapYear3);
                yt3.computeSum();
                Calendar calmonth_start = (Calendar) calThreeYearBack.clone();
                Calendar calmonth_end = (Calendar) calmonth_start.clone();
                calmonth_end.add(Calendar.MONTH, 1);
                calmonth_end.add(Calendar.DATE, -1);

                for (int k = 0; k < 12; k++) {
                    montharry[getMonth(calmonth_start)][0] = db.getKategorienAlleSummeWhere(
                            formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
                            where);
                    calmonth_start.add(Calendar.MONTH, 1);
                    calmonth_end.add(Calendar.MONTH, 1);
                }
                double sum = 0.0;
                for (int k = 0; k < 12; k++) {

                    sum = sum + montharry[k][0];
                }
                Map<Integer, Double> mapYear2 = new HashMap<Integer, Double>();
                YearTable yt2 = new YearTable();
                mapYear2 = db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calTowYearBack.getTime()),
                        formatter.format(calOneYearBack.getTime()), where);
                yt2.setMapYear(mapYear2);
                yt2.computeSum();


                Map<Integer, Double> mapYear1 = new HashMap<Integer, Double>();
                YearTable yt1 = new YearTable();
                mapYear1 = db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calOneYearBack.getTime()),
                        formatter.format(calnow.getTime()), where);
                yt1.setMapYear(mapYear1);
                yt1.computeSum();


                oat.setSummeUngewichtet(yt3.getSumOfYear() + yt2.getSumOfYear() + yt1.getSumOfYear());
                ;
                Hashtable weights = db.getForecastWeihts((int) kategorie.get("id"), (int) konto.get("id"));
                oat.gewichteWert(yt1, yt2, yt3, weights);

                for (int k = 1; k < 366; k++) {
                    if (mapYear1.get(k) == null) mapYear1.put(k, 0.0);
                    if (mapYear2.get(k) == null) mapYear2.put(k, 0.0);
                    if (mapYear3.get(k) == null) mapYear3.put(k, 0.0);
                    oat.computeProzentDay(k, mapYear1.get(k), mapYear2.get(k), mapYear3.get(k), yt1.getAnzOfDaysNotZero(), yt2.getAnzOfDaysNotZero(), yt3.getAnzOfDaysNotZero());
                }
                oat.computeDayGewichtet();
                Calendar cal_end = Calendar.getInstance();
                cal_end.add(Calendar.YEAR, 30);
                Calendar calstart = Calendar.getInstance();
                calstart.add(Calendar.DATE, 1);
                while (calstart.before(cal_end))
                // TODO: Hier muss evtl geschaut werde, ob ein Enddatum vorhanden ist.

                {
                    //Ganz schmutzig hatcoded :>>
                     if (! isCalendarBeforeYear(calstart, 2030) && kategorie.equals("Rentenversicherung"))
                     {
                         break;
                     }

                    Hashtable trans = new Hashtable();
                    int dayOfYear = calstart.get(Calendar.DAY_OF_YEAR);
                    if (isLeapYear(calstart.get(Calendar.YEAR)) && (dayOfYear > 59)) {
                        dayOfYear = dayOfYear - 1;
                    }
                    double myWert = oat.getDayGewichtet(dayOfYear);
                    if ((Integer) kategorie.get("inflation") == 1) {
                        myWert = oat.getDayGewichtet(dayOfYear) + (oat.getDayGewichtet(dayOfYear) * inflation);
                        inflation = inflation + inflationDay;
                    }

                    trans.put("datum", (String) formatter.format(calstart.getTime()));
                    trans.put("user", "Forecast");
                    trans.put("name", "Forecast " + kategorie.get("name"));
                    trans.put("konto", konto.get("id"));
                    trans.put("wert", myWert);
                    trans.put("partner", "");
                    trans.put("beschreibung", "");
                    trans.put("kategorie", kategorie.get("id"));
                    trans.put("kor_id", "0");
                    trans.put("cycle", "0");
                    trans.put("planed", "j");

                    if (oat.getDayGewichtet(dayOfYear) > 0.001 || oat.getDayGewichtet(dayOfYear) < -0.001) {
                        db.insertTransaktionZycl(trans);
                    }
                    calstart.add(Calendar.DATE, 1);
                }
                // --------------------------Eintag in kategorien
            }
        }
        logger.log("Forcast Berechnet :)");
    }


    private int getMonth(Calendar cal) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM");

        return new Integer(formatter.format(cal.getTime())) - 1;
    }

    public static boolean isLeapYear(int year) {
        Calendar cal = Calendar.getInstance(); //gets Calendar based on local timezone and locale
        cal.set(Calendar.YEAR, year); //setting the calendar year
        int noOfDays = cal.getActualMaximum(Calendar.DAY_OF_YEAR);

        if (noOfDays > 365) {
            return true;
        }

        return false;
    }

    // Methode zum Prüfen, ob das Jahr kleiner als jahrGrenze ist
    public static boolean isCalendarBeforeYear(Calendar calendar, int jahrGrenze) {
        return calendar.get(Calendar.YEAR) < jahrGrenze;
    }
}