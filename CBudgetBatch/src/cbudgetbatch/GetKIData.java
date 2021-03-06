package cbudgetbatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

public class GetKIData {
	
	static String user;
	static String pass;
	static String datenbank;
	 DBBatch db = new DBBatch();
	
	 public static void main(String[] args) {
	    	
	    	if (args.length != 3)
	    	{
	    		System.out.println("usage: budget_server <user> <password> <datenbank>");
	    		System.exit(1);
	    	}
	    	user = args[0];
			pass = args[1];
			datenbank = args [2];
	        //Server example = new Server();
	        DBBatch db = new DBBatch();
         if (! db.dataBaseConnect(user, pass, datenbank))
         {
         	System.err.println("Konnte mich nicht mit der Datenbank verbinden");
         	System.exit(1);
         }
         GetKIData kidata = new GetKIData();
         kidata.getData(db);
	      //Forecast forecast = new Forecast();
	      //forecast.getAllKategoriesWithForecast(db);
	      db.closeConnection();
	    }
	 
	 private void getData(DBBatch db)
	 {
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		
	
		
		 Calendar calstart=Calendar.getInstance();
		//Drei Jahre zur�ck rechnen
		 calstart.add(Calendar.YEAR, -4);
		 Calendar cal1back=Calendar.getInstance();
		
		 Calendar calnow=Calendar.getInstance();
		 calnow.add(Calendar.MONTH, -14);
		
		 //Erst mal alle Kategorien holen.		 
		 Vector kategories =db.getAllKategorien();
		 //Dann alle Konten
		 Vector konten = db.getAllKonto();
		 //boolean breakNextRun = false;
		 for ( int i=0; i< kategories.size();i++)
		 {
			 //System.out.println("I = " +i);
			 for (int j=0; j < konten.size(); j++)
			 {
				 Hashtable kategorie = (Hashtable) kategories.elementAt(i);
				 Hashtable konto = (Hashtable) konten.elementAt(j);
				 String where ="kategorie = "+kategorie.get("id") + " and konto_id = "+konto.get("id") +"and cycle = 0";
				// Calendar calbegin=Calendar.getInstance();
				 Calendar calzaehler=(Calendar)calstart.clone();
				 
				 while (calzaehler.before(calnow)) 
				 {
					 
					 //Hole erst mal alle Daten dazu aus der Datenbank
				     //Fangen wir an die letzten f�nf Monate 
					 Calendar calbegin= (Calendar) calzaehler.clone();
					 Calendar calend= (Calendar) calzaehler.clone();
					 calbegin.add(Calendar.MONTH, -4);
					 calend.add(Calendar.MONTH, -1);
					 Double sum =0.0;
					 Double wert = 0.0;
					 Boolean found = false;
					 //if (breakNextRun)
						// db.debug=true;
					 for (int k=0; k< 5; k++)
					 {
						
						 
					 wert= db.getKategorienAlleSummeWhere(formatter.format(calbegin.getTime()),formatter.format(calend.getTime()),where );
					 sum+=wert;
					 wert = wert/3;
					if ( wert > 0.01 || found)
					{
						 
						found=true;
						 System.out.print(wert+" ");
					} 	
					 calbegin.add(Calendar.MONTH, 1);
					 calend.add(Calendar.MONTH, 1);
					 }
					//Hole nun den aktuellen Monat
					calbegin=(Calendar)calend.clone();
					calbegin.add(Calendar.MONTH, -1);
					// db.debug =true;
					 if ( found)
					 {
						// db.debug =true;
					 }
					 wert= db.getKategorienAlleSummeWhere(formatter.format(calbegin.getTime()),formatter.format(calend.getTime()),where );
					 //db.debug =false;
					 if ( found)
					 {
					
					 System.out.print("     " +wert);
					 System.out.println();
					 //if (breakNextRun)
					//	 System.exit(0);
					 //breakNextRun=true;
					 }
					 
					 calzaehler.add(Calendar.MONTH, 1);
				 }
			 }
				 
		 }
	 
	 }
}
