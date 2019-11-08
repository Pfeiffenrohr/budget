package cbudgetbatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

public class Forecast {
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
	      Forecast forecast = new Forecast();
	      forecast.getAllKategoriesWithForecast(db);
	      db.closeConnection();
	    }
	 private void getAllKategoriesWithForecast(DBBatch db)
	 {
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar calbegin=Calendar.getInstance();
		 Calendar calnow=Calendar.getInstance();
		 //Drei Jahre zurück rechnen
		 calbegin.add(Calendar.YEAR, -3);
		 //Erst mal alle Kategorien holen.		 
		 Vector kategories =db.getAllKategorien();
		 //Dann alle Konten
		 Vector konten = db.getAllKonto();
		 //Dann von allen Kategorien den Durchschnitt der letzten drei Jahre holen und den Durchschnitt pro Monat ausrechnen.
		 for ( int i=0; i< kategories.size();i++)
		 {
			 for (int j=0; j < konten.size(); j++)
			 {
				 Hashtable kategorie = (Hashtable) kategories.elementAt(i);
				 Hashtable konto = (Hashtable) konten.elementAt(j);
				 String where = " kategorie = " + kategorie.get("id") + " and konto_id = "+ konto.get("id") +" and planed = 'j' and name like 'Forecast%' "; 
				 db.deleteTransaktionWithWhere(where );
				 if (kategorie.get("forecast").equals(0))
				 {
					// System.out.println("Kategorie "+ kategorie.get("name") + " muss nicht berechnet werden");
					 continue;
				 }
				 where ="kategorie = "+kategorie.get("id") + " and konto_id = "+konto.get("id") ;
				 Double wert= db.getKategorienAlleSummeWhere(formatter.format(calbegin.getTime()),formatter.format(calnow.getTime()),where );
				 if (wert != 0.0)
				 {
					 
					Double wertMonth=wert/36;
					wertMonth = Math.round(100.0 * wertMonth) / 100.0;
					//System.out.println(kategorie.get("name")+ " "+ konto.get("name")  +" "+ wertMonth);
					Calendar cal_end= Calendar.getInstance();
					cal_end.add(Calendar.YEAR, 30);
					Calendar calstart= Calendar.getInstance();
					calstart.add(Calendar.MONTH, 1);
					calstart.add(Calendar.DATE, 6);
					 while (calstart.before(cal_end)) 
							//TODO: Hier muss evtl geschaut werde, ob ein Enddatum vorhanden ist.
						{
						 Hashtable trans = new Hashtable();
						    
							//Einfuegen
							//zuerst schauen, ob der Eintrag schon da ist
							trans.put("datum", (String)formatter.format(calstart.getTime()));
							trans.put("user", "Wiederholung");
							trans.put("name", "Forecast "+kategorie.get("name"));
							trans.put("konto", konto.get("id"));
							trans.put("wert", wertMonth.toString());
							trans.put("partner", "");
							trans.put("beschreibung", "");
							trans.put("kategorie", kategorie.get("id"));
							trans.put("kor_id", "0");
							trans.put("cycle", "0");
							trans.put("planed", "j");
							//System.out.println(trans);
							db.insertTransaktionZycl(trans);
							calstart.add(Calendar.MONTH,1);
							
								
						}
					 //--------------------------Eintag in kategorien
				 }
			 }
		 }
		 
	 }
}