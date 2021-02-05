package sonstiges;

import cbudgetbatch.Forecast;

public class CleanDatabase {

	static String user;
	static String pass;
	static String datenbank;

	public static void main(String[] args) {
		int buffer=1000;
		if (args.length != 3) {
			System.out.println("usage: budget_server <user> <password> <datenbank>");
			System.exit(1);
		}
		user = args[0];
		pass = args[1];
		datenbank = args[2];
		// Server example = new Server();
		CleanDB db = new CleanDB();
		if (!db.dataBaseConnect(user, pass, datenbank)) {
			System.err.println("Konnte mich nicht mit der Datenbank verbinden");
			System.exit(1);
		}
		int rows = db.getAnzTransHist();
		int counter=0;
		System.out.println("Found "+rows+" rows");
		while (counter < rows)
		{
			db.deleteTransID(buffer,counter);
			counter+=buffer;
		}
		db.closeConnection();
	}

}
