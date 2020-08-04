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
		 Calendar cal3back=Calendar.getInstance();
		//Drei Jahre zurück rechnen
		 cal3back.add(Calendar.YEAR, -3);
		 Calendar cal2back=Calendar.getInstance();
		//Zwei Jahre zurück rechnen
		 cal2back.add(Calendar.YEAR, -2);
		 Calendar cal1back=Calendar.getInstance();
		//Ein Jahr zurück rechnen
		 cal1back.add(Calendar.YEAR, -1);
		 //Heute
		 Calendar calnow=Calendar.getInstance();
		
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
				 where ="kategorie = "+kategorie.get("id") + " and konto_id = "+konto.get("id") +"and cycle = 0";
				
				 Double wert1= db.getKategorienAlleSummeWhere(formatter.format(cal3back.getTime()),formatter.format(cal2back.getTime()),where );
				 Double wert2= db.getKategorienAlleSummeWhere(formatter.format(cal2back.getTime()),formatter.format(cal1back.getTime()),where );
				 Double wert3= db.getKategorienAlleSummeWhere(formatter.format(cal1back.getTime()),formatter.format(calnow.getTime()),where );
				//TODO: Hier ein bischen "KI"
				 Double wertMonth;
				 if (wert3 < 0.01)
				 {
			       //Wenn im letzten jahr nichts Ausgegeben wurde ist die Wahrscheilichkeit groß, dass jetzt auch nichts mehr
				   //ausgegeben wird
					 wertMonth=0.0;
				 }
				 else
				 {
					 if (wert1 < 0.01 && wert2 < 0.01)
					 { 
						 
						 wertMonth=wert3 * 3;
					 }
				 }
				 wertMonth=(wert1+wert2+wert3)/36;
				 
				 if (wertMonth > 0.0)
				 {
					 
					
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