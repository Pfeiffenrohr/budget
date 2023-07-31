package cbudgetbatch.forecastnew;


import cbudgetbatch.DBBatch;
import cbudgetbatch.forecastnew.OverAllTable;
import cbudgetbatch.forecastnew.YearTable;
import sonstiges.MyLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;


public class ForecastWriteDataToFile {
    static String user;
    static String pass;
    static String datenbank;
    DBBatch db = new DBBatch();

    private static final MyLogger logger = new MyLogger();

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("usage: budget_server <user> <password> <datenbank>");
            System.exit(1);
        }
        user = args[0];
        pass = args[1];
        datenbank = args[2];
        // Server example = new Server();
        DBBatch db = new DBBatch();
        if (!db.dataBaseConnect(user, pass, datenbank)) {
            System.err.println("Konnte mich nicht mit der Datenbank verbinden");
            System.exit(1);
        }
        ForecastWriteDataToFile forecast = new ForecastWriteDataToFile();
        forecast.getAllKategoriesWithForecast(db);
        db.closeConnection();
    }

    private void getAllKategoriesWithForecast(DBBatch db) {
        logger.log("Starte Berechung Gewichtung fuer Forecast ...");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calOneYearBack = Calendar.getInstance();
        Calendar calTowYearBack = Calendar.getInstance();
        Calendar calThreeYearBack = Calendar.getInstance();
        Calendar calFourYearBack = Calendar.getInstance();
        Calendar calnow = Calendar.getInstance();
        // Drei Jahre zurück rechnen
        calFourYearBack.add(Calendar.YEAR, -4);
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

               /*
                if (!((String) kategorie.get("name")).equals("Lebensmittel")) {
                    continue;
                }

                if (!((String) konto.get("name")).equals("Sparkasse Giro")) {
                    continue;
                }
                */
                // System.out.println("Berechne Forecast: Kategorie "+ kategorie.get("name")+" Konto = "+konto.get("name"));
                String where = " kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id")
                        + " and planed = 'j' and name like 'Forecast%' ";

                if (kategorie.get("forecast").equals(0)) {
                    // System.out.println("Kategorie "+ kategorie.get("name") + " muss nicht
                    // berechnet werden");
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
                Map<Integer, Double> mapYear3 = new HashMap<Integer, Double>();
                YearTable yt3 = new YearTable();
                Map<Integer, Double> mapYear4 = new HashMap<Integer, Double>();

                mapYear4 = db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calFourYearBack.getTime()),
                        formatter.format(calThreeYearBack.getTime()), where);

                mapYear3 = db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calThreeYearBack.getTime()),
                        formatter.format(calTowYearBack.getTime()), where);

                Map<Integer, Double> mapYear2 = new HashMap<Integer, Double>();

                mapYear2 = db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calTowYearBack.getTime()),
                        formatter.format(calOneYearBack.getTime()), where);
                Map<Integer, Double> mapYear1 = new HashMap<Integer, Double>();

                mapYear1 = db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calOneYearBack.getTime()),
                        formatter.format(calnow.getTime()), where);
                String str = "";
                int limit = 366;
                for (int k = 0; k <= limit; k++) {
                    if (mapYear4.get(k) == null) {
                        mapYear4.put(k, 0.0);
                    }
                    if (mapYear3.get(k) == null) {
                        mapYear3.put(k, 0.0);
                    }
                    if (mapYear2.get(k) == null) {
                        mapYear2.put(k, 0.0);
                    }
                    if (mapYear1.get(k) == null) {
                        mapYear1.put(k, 0.0);
                    }

                    str = str + mapYear4.get(k) + ";" + mapYear3.get(k) + ";" + mapYear2.get(k) + ";" + mapYear1.get(k) + "\n";
                }
                //System.out.println("Write");
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("c:/temp/budgetBar1.csv"));
                    writer.write(str);

                    writer.close();
                } catch (Exception ex) {
                    System.out.println("!!Can not write file");
                }
                Double targetSum = computeSumOfMap(mapYear1);
                if (targetSum * targetSum< 0.001) {
                    continue;
                }
                //System.out.println("TargetSum = " + targetSum);
                //compute middle
                double minDiff = 999999999;
                int targetY4 =1;
                int targetY3 =1;
                int targetY2 =1;
                for (int y4 = 1; y4 <= 100; y4++) {
                    for (int y3 = 1; y3 <= 100; y3++) {
                        for (int y2 = 1; y2 <= 100; y2++) {
                            Map<Integer, Double> mapComputedAvg = new HashMap<Integer, Double>();
                            double teiler = y4 + y3 + y2;
                            for (int k = 0; k <= 366; k++) {
                                double valueMonth = (mapYear4.get(k) * y4 + mapYear3.get(k) * y3 + mapYear2.get(k) * y2) / teiler;
                                mapComputedAvg.put(k, valueMonth);
                            }
                            Double avgSum = computeSumOfMap(mapComputedAvg);
                            //  System.out.println("avgSum = " + avgSum);
                            double differenz = ((avgSum - targetSum) * (avgSum - targetSum));
                            //  System.out.println("differenz = " + differenz);
                            if (differenz < minDiff) {
                                minDiff = differenz;
                                //System.out.println("NeueminDifff = " + minDiff);
                                //System.out.println("y4 = " + y4 + " y3 = " + y3 + " y2 = " + y2);
                                targetY4=y4;
                                targetY3=y3;
                                targetY2=y2;
                            }
                        }
                    }
                }
                System.out.println("In "+kategorie.get("name")+ " und Konto "+ konto.get("name") +" MinDifff = " + minDiff ) ;
                System.out.println("y4 = " + targetY4 + " y3 = " + targetY3 + " y2 = " + targetY2);
            }
        }
        logger.log("Forcast Berechnet :)");
    }

    private Double computeSumOfMap(Map<Integer, Double> map) {
        double sum = 0.0;
        for (int i = 0; i < map.size(); i++) {
            sum = sum + map.get(i);
        }
        return sum;
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
}