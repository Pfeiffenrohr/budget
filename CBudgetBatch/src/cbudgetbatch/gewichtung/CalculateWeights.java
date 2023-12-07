package cbudgetbatch.gewichtung;

import cbudgetbatch.DBBatch;
import sonstiges.MyLogger;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class CalculateWeights {
    private Map<Integer, YearTable> maps;

    public Map<Integer, YearTable> getMaps() {
        return maps;
    }

    public void setMaps(Map<Integer, YearTable> maps) {
        this.maps = maps;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getKonto() {
        return konto;
    }

    public void setKonto(int konto) {
        this.konto = konto;
    }

    private int category;
    private int konto;
    private DBBatch db;

    public DBBatch getDb() {
        return db;
    }

    public void setDb(DBBatch db) {
        this.db = db;
    }

    private static final MyLogger logger = new MyLogger();

    public CalculateWeights(Map<Integer, YearTable> maps, int category, int konto, DBBatch db) {
        this.maps = maps;
        this.category = category;
        this.konto = konto;
        this.db = db;
    }
    public CalculateWeights() {
    }

    public boolean calculate(Map<Integer, YearTable> maps) {
        double differenzMax = 999999999;
        Double targetSum;
        this.maps = maps;
       /* if (targetSum * targetSum< 0.001) {
            continue;
        }*/
        Map<Integer, Double> mapComputedAvg = new HashMap<Integer, Double>();
        Map<Integer, Double> map2022 = maps.get(2022).getMapYear();
        Map<Integer, Double> map2021 = maps.get(2021).getMapYear();
        Map<Integer, Double> map2020 = maps.get(2020).getMapYear();
        Map<Integer, Double> map2019 = maps.get(2019).getMapYear();
        Map<Integer, Double> map2018 = maps.get(2018).getMapYear();
        /*
        Wenn drei Jahre die Summe null war, dann ist die Wahrscheinlichkeit groß,
        dass sie auch null wird
        */

        if (Math.abs(computeSumOfMap(map2022)) < 0.01 &&
                Math.abs(computeSumOfMap(map2021)) < 0.01 &&
                Math.abs(computeSumOfMap(map2020)) < 0.01) {

            return true;
        }
        // System.out.println("Summme 2021 " +computeSumOfMap(map2021)+ " Summme 2020 " +computeSumOfMap(map2020) +" Summme 2019 " +computeSumOfMap(map2019));
        int y1max = 0;
        int y2max = 0;
        int y3max = 0;
        for (int y1 = 0; y1 < 100; y1++) {
            for (int y2 = 0; y2 < 100; y2++) {
                for (int y3 = 0; y3 < 100; y3++) {
                    //String str ="";
                    Double differenz[] = new Double[2];
                    Double differenzAll;
                    for (int cycle = 2022; cycle > 2020; cycle--) {
                        mapComputedAvg.clear();
                        targetSum = computeSumOfMap(maps.get(cycle).getMapYear());
                        for (int k = 0; k <= 366; k++) {
                            double valueMonth = maps.get(cycle - 1).getMapYear().get(k) * (y1 / 50.0) + maps.get(cycle - 2).getMapYear().get(k) * (y2 / 50.0) + maps.get(cycle - 3).getMapYear().get(k) * (y3 / 50.0);
                            mapComputedAvg.put(k, valueMonth);
                            //str = str   + map2021.get(k) +";"+map2020.get(k) +";"+ map2019.get(k)+"; "+mapComputedAvg.get(k)  + "\n";
                        }
                        Double avgSum = computeSumOfMap(mapComputedAvg);
                        differenz[2022 - cycle] = ((avgSum - targetSum) * (avgSum - targetSum));
                    }
                    differenzAll = sum(differenz);
                    if (differenzAll < differenzMax) {
                        y1max = y1;
                        y2max = y2;
                        y3max = y3;
                        differenzMax = differenzAll;
                    }
                }
            }

        }
        if (y1max == 0 && y2max == 0 && y3max == 0) {
            y1max = 50;
        }
        logger.log(" y1 = " + y1max + " y2 = " + y2max + " y3 = " + y3max + " avgSum = " + differenzMax);

        Hashtable hash = new Hashtable();
        hash.put("category", category);
        hash.put("konto", konto);
        hash.put("y1", y1max);
        hash.put("y2", y2max);
        hash.put("y3", y3max);
        hash.put("precision", differenzMax);
        db.insertForecastWeights(hash);
        return true;
    }

    private Double computeSumOfMap(Map<Integer, Double> map) {
        double sum = 0.0;
        for (int i = 0; i < map.size(); i++) {
            sum = sum + map.get(i);
        }
        return sum;
    }
    private double sum (Double diff []) {
        double mysum=0.0;
        for (int i=0; i< diff.length; i++)
        {
            mysum=mysum+diff[i];
        }
        return mysum;
    }
}
