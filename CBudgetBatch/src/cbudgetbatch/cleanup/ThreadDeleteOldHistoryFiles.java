package cbudgetbatch.cleanup;

import cbudgetbatch.DBBatch;
import sonstiges.MyLogger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ThreadDeleteOldHistoryFiles  extends Thread{
    private static final MyLogger logger = new MyLogger();
    private void cleanOldHistoryFiles(DBBatch dbbatch)
    {


        //SÃ¤ubert alle alten Transaktion_historie EintrÃ¤ge, die keineRefernnz mehr haben
        logger.log("Start deleteOldtransHistorie ..");
        dbbatch.deleteOldtransHistorie();
        logger.log("End deleteOldtransHistorie ");

    }

    private DBBatch db;

    public void setDBBatch (DBBatch db) {
        this.db = db;
    }
    public void run(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


        while ( true ) {

            cleanOldHistoryFiles(db);
            try {
                TimeUnit.MINUTES.sleep(600);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

