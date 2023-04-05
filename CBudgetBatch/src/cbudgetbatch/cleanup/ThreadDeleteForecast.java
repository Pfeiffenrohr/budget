package cbudgetbatch.cleanup;

import cbudgetbatch.DBBatch;
import sonstiges.MyLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ThreadDeleteForecast extends Thread {
    private static final MyLogger logger = new MyLogger();
       private DBBatch db;

    public void setDBBatch (DBBatch db) {
        this.db = db;
    }
    public void run(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


        while ( true ) {
            Calendar cal= Calendar.getInstance();
            logger.log("Start deleteOldForecast ..");
            db.deleteOldForecast(formatter.format(cal.getTime()));
            logger.log("End deleteOldForecast ..");
            try {
                TimeUnit.MINUTES.sleep(60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
