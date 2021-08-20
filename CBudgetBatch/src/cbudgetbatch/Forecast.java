package cbudgetbatch;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import cbudgetbatch.DBBatch;
import cbudgetbatch.forecastnew.OverAllTable;
import cbudgetbatch.forecastnew.YearTable;

public class Forecast {
	static String user;
	static String pass;
	static String datenbank;
	DBBatch db = new DBBatch();

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
		Forecast forecast = new Forecast();
		forecast.getAllKategoriesWithForecast(db);
		db.closeConnection();
	}

	private void getAllKategoriesWithForecast(DBBatch db) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calOneYearBack = Calendar.getInstance();
		Calendar calTowYearBack = Calendar.getInstance();
		Calendar calThreeYearBack = Calendar.getInstance();
		Calendar calnow = Calendar.getInstance();
		// Drei Jahre zurück rechnen
		calThreeYearBack.add(Calendar.YEAR, -3);
		calTowYearBack.add(Calendar.YEAR, -2);
		calOneYearBack.add(Calendar.YEAR, -1);
		// Erst mal alle Kategorien holen.
		Vector kategories = db.getAllKategorien();
		// Dann alle Konten
		Vector konten = db.getAllKonto();
		Hashtable settings=db.getSettings();
		// Dann von allen Kategorien den Durchschnitt der letzten drei Jahre holen und
		// den Durchschnitt pro Monat ausrechnen.
		
		for (int i = 0; i < kategories.size(); i++) {
		  
	        
			for (int j = 0; j < konten.size(); j++) {
				Hashtable kategorie = (Hashtable) kategories.elementAt(i);
				Hashtable konto = (Hashtable) konten.elementAt(j);
				  double inflation=0.0;
			        double inflationDay=0.0;
				
				if (!((String) kategorie.get("name")).equals("Laufende Kosten")) {
					continue;
				}
                
				if (!((String) konto.get("name")).equals("Paypal")) {
					continue;
				}
				
				 System.out.println("Berechne Forecast: Kategorie "+ kategorie.get("name")+" Konto = "+konto.get("name"));
				String where = " kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id")
						+ " and planed = 'j' and name like 'Forecast%' ";
				db.deleteTransaktionWithWhere(where);
				if (kategorie.get("forecast").equals(0)) {
					// System.out.println("Kategorie "+ kategorie.get("name") + " muss nicht
					// berechnet werden");
					continue;
				}
				if ((Integer)kategorie.get("inflation") == 1)
				{
					inflationDay= new Double ((String)settings.get("inflation"));
				    inflationDay=inflationDay/365;
				    inflationDay=inflationDay/100;
				}
				where = "kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id") + "and cycle = 0";

				double[][] montharry = new double[12][3];
               //Hier machen wir das Ganze 3 Mal hintereinander für jedes Jahr. Das sollte man eigentlich besser machen.
				OverAllTable oat = new OverAllTable();
				Map <Integer,Double >mapYear3 = new HashMap<Integer, Double>();
				YearTable yt3 = new YearTable();
				
				
				mapYear3=db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calThreeYearBack.getTime()),
						formatter.format(calTowYearBack.getTime()), where);
				yt3.setMapYear(mapYear3);
				yt3.computeSum();
				
				Double wertYear3 = db.getKategorienAlleSummeWhere(formatter.format(calThreeYearBack.getTime()),
						formatter.format(calTowYearBack.getTime()), where);

				Calendar calmonth_start = (Calendar) calThreeYearBack.clone();
				Calendar calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);
				calmonth_end.add(Calendar.DATE,-1);
   
				for (int k = 0; k < 12; k++) {
					montharry[getMonth(calmonth_start)][0] = db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where);
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				double sum = 0.0;
				for (int k = 0; k < 12; k++) {

					sum = sum + montharry[k][0];
				}
				Map <Integer,Double >mapYear2 = new HashMap<Integer, Double>();
				YearTable yt2 = new YearTable();
				mapYear2=db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calTowYearBack.getTime()),
						formatter.format(calOneYearBack.getTime()), where);
				yt2.setMapYear(mapYear2);
				yt2.computeSum();
				
			
				Map <Integer,Double >mapYear1 = new HashMap<Integer, Double>();
				YearTable yt1 = new YearTable();
				mapYear1=db.getKategorienAlleSummeWhereAsMapPerDay(formatter.format(calOneYearBack.getTime()),
						formatter.format(calnow.getTime()), where);
				yt1.setMapYear(mapYear1);
				yt1.computeSum();
				
				
			
				Double wertMapungewichtet = yt3.getSumOfYear()+yt2.getSumOfYear()+yt1.getSumOfYear();
				oat.setSummeUngewichtet(yt3.getSumOfYear()+yt2.getSumOfYear()+yt1.getSumOfYear()); ;
				oat.setSummeGewichtet((3 * yt1.getSumOfYear() + 2 * yt2.getSumOfYear() + yt3.getSumOfYear()) / 6);
				for (int k=1; k< 366; k++ )
				{
					if (mapYear1.get(k)==null ) mapYear1.put(k,0.0);
					if (mapYear2.get(k)==null ) mapYear2.put(k,0.0);
					if (mapYear3.get(k)==null ) mapYear3.put(k,0.0);
					oat.computeProzentDay(k, mapYear1.get(k), mapYear2.get(k), mapYear3.get(k));
				}
				//oat.printSumProzent();
				oat.computeDayGewichtet();
				
				
					/*
					 System.out.println("Year3" + kategorie.get("name")+ " "+ konto.get("name") +" "+
					+ wertYear3 );
					 System.out.println("Year2" + kategorie.get("name")+ " "+ konto.get("name") +" "+
					 + wertYear2);
					 System.out.println("Year1" + kategorie.get("name")+ " "+ konto.get("name") +" "+
					 + wertYear1);
         			 System.out.println("WertMonth "+
					 wertMonth);
         			 System.out.println("WertUngewichtet "+
         					wertUngewichtet);
         			 System.out.println("WertUngewichtetMap "+
          					wertUngewichtet);
         			 System.out.println("Wert "+
        					 wert);
					 
					 
					 */
					 
					Calendar cal_end = Calendar.getInstance();
					cal_end.add(Calendar.YEAR, 30);
					Calendar calstart = Calendar.getInstance();
					//calstart.add(Calendar.MONTH, 1);
					//calstart.add(Calendar.DATE, 6);
					oat.printSumProzent();
					//System.out.println("Wert gewichtet = " +oat.getSummeGewichtet());
					while (calstart.before(cal_end))
					// TODO: Hier muss evtl geschaut werde, ob ein Enddatum vorhanden ist.
				
					{
						
						
						Hashtable trans = new Hashtable();
                      
						//double myWert=wert * prozent[getMonth(calstart)] *inflation;
						
                        
                        int dayOfYear = calstart.get(Calendar.DAY_OF_YEAR); 
                        if (isLeapYear (calstart.get(Calendar.YEAR)) &&  (dayOfYear > 59 ))
                        {
                        	dayOfYear = dayOfYear -1;
                        }
                        double myWert=oat.getDayGewichtet(dayOfYear) + (oat.getDayGewichtet(dayOfYear) *inflation);
                        inflation=inflation+ inflationDay;
                        /*

                        System.out.println("Wert ohne inflation = "+wert * prozent[getMonth(calstart)] );
                        System.out.println("Wert mit inflation = "+myWert );

                        System.out.println("Datum = " + (String) formatter.format(calstart.getTime()));
                        System.out.println("Inflation = " +inflation );
                        System.out.println("Prozentwert = "+prozent[getMonth(calstart)]);
                        System.out.println("Inflationswert =  " + wert * prozent[getMonth(calstart)] *inflation )
*/
                     //   System.out.println("Wert = " +(oat.getDayGewichtet(dayOfYear) ));
                     //   System.out.println("Wert mit inflation = " + myWert);
                        
                    //    System.out.println();
                        
						// Einfuegen
						// zuerst schauen, ob der Eintrag schon da ist
						trans.put("datum", (String) formatter.format(calstart.getTime()));
						trans.put("user", "Forecast");
						trans.put("name", "Forecast " + kategorie.get("name"));
						trans.put("konto", konto.get("id"));
						// trans.put("wert", wertMonth.toString());
						//trans.put("wert", myWert);
						//trans.put("wert", wert * prozent[getMonth(calstart)]);
						trans.put("wert",myWert );
						trans.put("partner", "");
						trans.put("beschreibung", "");
						trans.put("kategorie", kategorie.get("id"));
						trans.put("kor_id", "0");
						trans.put("cycle", "0");
						trans.put("planed", "j");
						// System.out.println("Transwert "+trans.get("wert"));
						if (oat.getDayGewichtet(dayOfYear) > 0.001 || oat.getDayGewichtet(dayOfYear) < -0.001) {
							db.insertTransaktionZycl(trans);
						}
						//calstart.add(Calendar.MONTH, 1);
						calstart.add(Calendar.DATE, 1);
					}
					// --------------------------Eintag in kategorien
				}
			}

		}



	private int getMonth(Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM");

		return new Integer(formatter.format(cal.getTime())) - 1;
	}
	
	  public static boolean isLeapYear(int year){
	       Calendar cal = Calendar.getInstance(); //gets Calendar based on local timezone and locale
	       cal.set(Calendar.YEAR, year); //setting the calendar year
	       int noOfDays = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
	     
	       if(noOfDays > 365){
	           return true;
	       }
	     
	       return false;
	   }
}