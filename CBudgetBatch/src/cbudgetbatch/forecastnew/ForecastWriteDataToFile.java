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
        //forecast.getAllKategoriesWithForecast(db);
        forecast.startCalculateWeights(db);
        db.closeConnection();
    }

    private void startCalculateWeights(DBBatch db) {
        logger.log("Starte CalculateWeights ...");
        //Calendar sehen so aus [2023,2022,2021,2020,2019,2018]
        Calendar []  calendars = new Calendar[8];
        for (int i =0 ; i < calendars.length; i++ ) {
            calendars [i] = Calendar.getInstance();
            calendars [i].add(Calendar.YEAR, -i);
        }
        calculateYears (db, calendars);
        logger.log("Ende CalculateWeights ...");
    }

    private void calculateYears (DBBatch db,Calendar []  calendars ) {
        Vector kategories = db.getAllKategorien();
        Vector konten = db.getAllKonto();
        Hashtable settings = db.getSettings();
        for (int i = 0; i < kategories.size(); i++) {


            for (int j = 0; j < konten.size(); j++) {

                Hashtable kategorie = (Hashtable) kategories.elementAt(i);
                Hashtable konto = (Hashtable) konten.elementAt(j);
                if (!((String) kategorie.get("name")).equals("Lebensmittel")) {
                    continue;
                }

                if (!((String) konto.get("name")).equals("Sparkasse Giro")) {
                    continue;
                }
                if (kategorie.get("forecast").equals(0)) {
                    // System.out.println("Kategorie "+ kategorie.get("name") + " muss nicht
                    // berechnet werden");
                    continue;
                }
                String  where = "kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id") + "and cycle = 0";

                Map<Integer, YearTable>  maps =  getMapsAllYear( db, calendars, where);
                calculateWeights(maps);

            }
        }
    }

    private void calculateWeights( Map<Integer, YearTable>  maps) {
        double differenzMax = 999999999;
        Double targetSum ;
       /* if (targetSum * targetSum< 0.001) {
            continue;
        }*/
        Map<Integer, Double> mapComputedAvg = new HashMap<Integer, Double>();
        Map<Integer, Double>   map2022=maps.get(2022).getMapYear();
        Map<Integer, Double>   map2021=maps.get(2021).getMapYear();
        Map<Integer, Double>   map2020=maps.get(2020).getMapYear();
        Map<Integer, Double>   map2019=maps.get(2019).getMapYear();
        Map<Integer, Double>   map2018=maps.get(2018).getMapYear();
        // Ausgabe
        /*String str="2018;2019;2020;2021;2022\n";
        for (int k=0; k < 366;k++) {
            str = str + map2018.get(k) + ";" + map2019.get(k) + ";" + map2020.get(k) + ";" + map2021.get(k) +";" + map2022.get(k)+ "\n";
        }
        str=str.replace(".",",");
    //System.out.println("Write");
                try {
        BufferedWriter writer = new BufferedWriter(new FileWriter("c:/temp/comp2.csv"));
        writer.write(str);

        writer.close();
    } catch (Exception ex) {
        System.out.println("!!Can not write file");
    }   */
        //Ausgabe Ende
        System.out.println("Summme 2021 " +computeSumOfMap(map2021)+ " Summme 2020 " +computeSumOfMap(map2020) +" Summme 2019 " +computeSumOfMap(map2019));
        for (int y1 =0; y1 < 100; y1++) {
            for (int y2 =0; y2 < 100; y2++) {
                for (int y3 =0; y3 < 100; y3++) {
                    //String str ="";
                    Double differenz [] = new Double[4];
                    Double differenzAll ;
                    for (int cycle = 2022; cycle > 2018; cycle --) {
                        mapComputedAvg.clear();
                        targetSum = computeSumOfMap(maps.get(cycle).getMapYear());
                        for (int k = 0; k <= 366; k++) {
                            double valueMonth = maps.get(cycle - 1).getMapYear().get(k) * (y1 / 50.0) + maps.get(cycle - 2).getMapYear().get(k) * (y2 / 50.0) + maps.get(cycle - 3).getMapYear().get(k) * (y3 / 50.0);
                            mapComputedAvg.put(k, valueMonth);
                            //str = str   + map2021.get(k) +";"+map2020.get(k) +";"+ map2019.get(k)+"; "+mapComputedAvg.get(k)  + "\n";
                        }
                        Double avgSum = computeSumOfMap(mapComputedAvg);
                        differenz[2022 -cycle] = ((avgSum - targetSum) * (avgSum - targetSum));
                    }
                    differenzAll=sum(differenz);
                        if (differenzAll < differenzMax) {
                            differenzMax = differenzAll;
                            System.out.println(" differnzMax = " + differenzMax + " y1 = " + y1 + " y2 = " + y2 + " y3 = " + y3 + " avgSum = " + differenzAll);
                    }
                }
            }
        }
    }

    private double sum (Double diff []) {
        double mysum=0.0;
        for (int i=0; i< diff.length; i++)
        {
            mysum=mysum+diff[i];
        }
        return mysum;
    }
    private Map<Integer, YearTable> getMapsAllYear( DBBatch db, Calendar []  calendars,String where) {
        Map<Integer, YearTable > map = new HashMap<>();
        for (int i = 1;  i< calendars.length; i++) {
            map.put(calendars[i].get(Calendar.YEAR),getMapsYear(db,calendars[i],calendars[i-1],where));
        }
        return map;
    }
    private YearTable  getMapsYear (DBBatch db, Calendar start, Calendar end, String where) {
        int limit = 366;
        YearTable yt = new YearTable();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Map<Integer, Double> map=db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(start.getTime()),formatter.format(end.getTime()), where);
        for (int k = 0; k <= limit; k++) {
            if (map.get(k) == null) {
                map.put(k, 0.0);
            }
        }
        yt.setMapYear(map);
        yt.computeSum();
        return  yt;

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
        //calnow.add(Calendar.YEAR, -1);
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


                if (!((String) kategorie.get("name")).equals("Lebensmittel")) {
                    continue;
                }

                if (!((String) konto.get("name")).equals("Sparkasse Giro")) {
                    continue;
                }

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
                    BufferedWriter writer = new BufferedWriter(new FileWriter("c:/temp/budget4.csv"));
                    writer.write(str);

                    writer.close();
                } catch (Exception ex) {
                    System.out.println("!!Can not write file");
                }
                Double targetSum = computeSumOfMap(mapYear1);
                if (targetSum * targetSum< 0.001) {
                    continue;
                }
                System.out.println("TargetSum = " + targetSum);
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
                                double valueMonth = (mapYear4.get(k) * y4/50 + mapYear3.get(k) * y3/50 + mapYear2.get(k) * y2)/50 ;
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