package cbudgetbatch.cleanup;

import cbudgetbatch.DBBatch;
import cbudgetbatch.evaluation.ForecastEvaluationDb;
import sonstiges.MyLogger;

//import budget.HeaderFooter;


public class DeleteOldHistoryFiles {
	static String user;
	static String pass;
	static String datenbank;
	boolean debug=false;
	
	
	 private static final MyLogger logger = new MyLogger();  
	
	 /**
	 * Schmeisst die alten Cache Jobs raus, die Alt sind und nicht mehr gebraucht werden.
	 */
	private void cleanOldHistoryFiles(DBBatch dbbatch)
	{


		//SÃ¤ubert alle alten Transaktion_historie EintrÃ¤ge, die keineRefernnz mehr haben
	 logger.log("Start deleteOldtransHistorie ..");	
	 dbbatch.deleteOldtransHistorie();
	 logger.log("DeleteOldtransHistorie done!");
		
	}
	

	public static void main(String[] args) {
		if (args.length != 3)
    	{
    		System.out.println("usage: DeleteOldHistoryFiles <user> <password> <connectstring,z.B: jdbc:postgresql://192.168.2.28/>   ");
    		System.exit(1);
    	}
		user = args[0];
		pass = args[1];
		datenbank = args [2];
		DBBatch db = new DBBatch();
		db.dataBaseConnect(user, pass, datenbank);
		ThreadDeleteForecast deleteForecast = new ThreadDeleteForecast();
		deleteForecast.setDBBatch(db);
		deleteForecast.start();
		DBBatch dbHis = new DBBatch();
		dbHis.dataBaseConnect(user, pass, datenbank);
		ThreadDeleteOldHistoryFiles deleteOldHistoryFiles = new ThreadDeleteOldHistoryFiles();
		deleteOldHistoryFiles.setDBBatch(dbHis);
		deleteOldHistoryFiles.start();
		/*
		 * forecast_snapshot waechst um etwa dreihunderttausend Zeilen pro Jahr,
		 * weil jeder Forecast-Lauf fuer 24 rollierende Monate je Kategorie und
		 * Konto wieder einen Stand schreibt. Ohne diesen Thread bliebe nur die
		 * Wahl zwischen unbegrenztem Wachstum und einer festen Abschneidegrenze
		 * im Code.
		 */
		DBBatch dbSnapshot = new DBBatch();
		dbSnapshot.dataBaseConnect(user, pass, datenbank);
		new ThreadDeleteForecastSnapshots(new ForecastEvaluationDb(dbSnapshot)).start();
		//db.closeConnection();
    }
}
