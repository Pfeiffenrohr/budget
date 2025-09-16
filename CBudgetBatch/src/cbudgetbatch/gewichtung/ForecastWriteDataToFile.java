package cbudgetbatch.gewichtung;


import cbudgetbatch.DBBatch;
import sonstiges.MyLogger;

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

    public void startCalculateWeights(DBBatch db) {
        logger.log("Starte CalculateWeights ...");
        //Calendar sehen so aus [2023,2022,2021,2020,2019,2018]
        Calendar []  calendars = new Calendar[8];
        for (int i =0 ; i < calendars.length; i++ ) {
            calendars [i] = Calendar.getInstance();
            calendars [i].add(Calendar.YEAR, -i);
        }
        CalculateWeights calculateWeights = new CalculateWeights();
        calculateYears (db, calendars, calculateWeights);
        logger.log("Ende CalculateWeights ...");
    }

    public void calculateYears (DBBatch db,Calendar []  calendars ,CalculateWeights calculateWeights ) {
        Vector kategories = db.getAllKategorien();
        Vector konten = db.getAllKonto();
        Hashtable settings = db.getSettings();
        for (int i = 0; i < kategories.size(); i++) {


            for (int j = 0; j < konten.size(); j++) {

                Hashtable kategorie = (Hashtable) kategories.elementAt(i);
                Hashtable konto = (Hashtable) konten.elementAt(j);
/*
                if (!((String) kategorie.get("name")).equals("Katzen")) {
                    continue;
                }

                if (!((String) konto.get("name")).equals("Consors Giro")) {
                    continue;
                }

 */
                if (kategorie.get("forecast").equals(0)) {
                   // System.out.println("Kategorie "+ kategorie.get("name") + " muss nicht berechnet werden");
                    continue;
                }


                String  where = "kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id") + " and cycle = 0";

                Map<Integer, YearTable>  maps =  getMapsAllYear( db, calendars, where);
                logger.log("Bearbeite "+kategorie.get("name")+" Konto = "+ konto.get("name"));
                calculateWeights.setCategory((int)kategorie.get("id"));
                calculateWeights.setKonto((int)konto.get("id"));
                calculateWeights.setDb(db);
                calculateWeights.calculate(maps);
               // calculateWeights(maps,(int)kategorie.get("id"),(int)konto.get("id"),db);

            }
        }
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
}