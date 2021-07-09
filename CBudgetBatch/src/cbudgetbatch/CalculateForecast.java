package cbudgetbatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

public class CalculateForecast {
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
		CalculateForecast forecast = new CalculateForecast();
		forecast.getAllKategoriesWithForecast(db);
		db.closeConnection();
	}

	private void getAllKategoriesWithForecast(DBBatch db) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calOneYearBack = Calendar.getInstance();
		Calendar calTowYearBack = Calendar.getInstance();
		Calendar calThreeYearBack = Calendar.getInstance();
		Calendar calnow = Calendar.getInstance();
		int YearBack= -1;
		// Drei Jahre zurück rechnen
		calnow.add(Calendar.YEAR, YearBack);
		calThreeYearBack.add(Calendar.YEAR, -3+YearBack);
		calTowYearBack.add(Calendar.YEAR, -2+YearBack);
		calOneYearBack.add(Calendar.YEAR, -1+YearBack);
		// Erst mal alle Kategorien holen.
		Vector kategories = db.getAllKategorien();
		// Dann alle Konten
		Vector konten = db.getAllKonto();
		// Dann von allen Kategorien den Durchschnitt der letzten drei Jahre holen und
		// den Durchschnitt pro Monat ausrechnen.
		for (int i = 0; i < kategories.size(); i++) {
			for (int j = 0; j < konten.size(); j++) {
				Hashtable kategorie = (Hashtable) kategories.elementAt(i);
				Hashtable konto = (Hashtable) konten.elementAt(j);
				
				if (!((String) kategorie.get("name")).equals("Bekleidung")) {
					continue;
				}
				//System.out.println(((String) kategorie.get("name")));
				if (!((String) konto.get("name")).equals("Sparkasse Giro")) {
					continue;
				}
				String where = " kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id")
						+ " and planed = 'j' and name like 'Forecast%' ";
				//db.deleteTransaktionWithWhere(where);
				if (kategorie.get("forecast").equals(0)) {
					// System.out.println("Kategorie "+ kategorie.get("name") + " muss nicht
					// berechnet werden");
					continue;
				}
				where = "kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id") + "and cycle = 0";

				//double[][] montharry = new double[12][3];
				Map <Integer,ResultObject> ros  = new HashMap<Integer,ResultObject>();
               //Hier machen wir das Ganze 3 Mal hintereinander für jedes Jahr. Das sollte man eigentlich besser machen.
				Double wertYear3 = db.getKategorienAlleSummeWhere(formatter.format(calThreeYearBack.getTime()),
						formatter.format(calTowYearBack.getTime()), where);

				Calendar calmonth_start = (Calendar) calThreeYearBack.clone();
				Calendar calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);

				for (int k = 0; k < 12; k++) {
					ResultObject ro = new ResultObject(); 
					 
					 ro.setYearback1(db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where));
					 
					 ro.setMonth(getMonth(calmonth_start));
					ros.put(getMonth(calmonth_start),ro);
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				
				
				Double wertYear2 = db.getKategorienAlleSummeWhere(formatter.format(calTowYearBack.getTime()),
						formatter.format(calOneYearBack.getTime()), where);
				calmonth_start = (Calendar) calTowYearBack.clone();
				calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);

				for (int k = 0; k < 12; k++) {
					ResultObject ro = ros.get(getMonth(calmonth_start));
					ro.setYearback2(db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where));
					
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				Double wertYear1 = db.getKategorienAlleSummeWhere(formatter.format(calOneYearBack.getTime()),
						formatter.format(calnow.getTime()), where);

				calmonth_start = (Calendar) calOneYearBack.clone();
				calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);

				for (int k = 0; k < 12; k++) {
					ResultObject ro = ros.get(getMonth(calmonth_start));
					ro.setYearback3(db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where));
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				
				
				//Berechn gesamtWert für jedes Ro
				for (int k = 0; k < 12; k++) {
					//System.out.println("K = " +k);
					ResultObject ro = ros.get(k);
					
					ro.setGesamt(ro.getYearback1()+ro.getYearback2()+ro.getYearback3());
					
				}
				Double wertUngewichtet = wertYear1 + wertYear2 + wertYear3;
				Double wert = (3 * wertYear1 + 2 * wertYear2 + wertYear3) / 6;
				if (wert > 0.001 || wert < -0.001) {
					// Rechne Prozentwert aus
					double[] prozent = new double[12];
					double gesamtprozent = 0.0;
					for (int k = 0; k < 12; k++) {
						//double prozentwertwert = (montharry[k][0] + montharry[k][1] + montharry[k][2]);	
					
						ResultObject ro = ros.get(k);
						double prozentwertwert = (ro.getGesamt());
						prozent[k] = prozentwertwert / wertUngewichtet;
						gesamtprozent = gesamtprozent + prozent[k];
					}
					System.out.println("Gesamtprozent= "+ gesamtprozent);
					Double wertMonth = wert / 12;
					wertMonth = Math.round(100.0 * wertMonth) / 100.0;
					// System.out.println("Year3" + kategorie.get("name")+ " "+ konto.get("name") +" "+
					//+ wertYear3 );
					// System.out.println("Year2" + kategorie.get("name")+ " "+ konto.get("name") +" "+
					// + wertYear2);
					// System.out.println("Year1" + kategorie.get("name")+ " "+ konto.get("name") +" "+
					// + wertYear1);

					// System.out.println(kategorie.get("name")+ " "+ konto.get("name") +" "+
					// wertMonth);
					Calendar cal_end = Calendar.getInstance();
					cal_end.add(Calendar.YEAR, 30);
					Calendar calstart = Calendar.getInstance();
					calstart.add(Calendar.MONTH, 1);
					calstart.add(Calendar.DATE, 6);
					
					//Ab hier beginnt die Auswertung
					Calendar cal = Calendar.getInstance();
					calmonth_start = (Calendar) cal.clone();
				    calmonth_end = (Calendar) cal.clone();
					calmonth_end.add(Calendar.MONTH, 1);

					for (int k = 0; k < 12; k++) {
						ResultObject ro = ros.get(getMonth(calmonth_start));
						ro.setRealValue(db.getKategorienAlleSummeWhere(
								formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
								where));
						ro.setDiff(abs(wert * prozent[getMonth(calmonth_start)] - ro.getRealValue()));
						if (ro.getRealValue() == 0.0 )
						{
							if (ro.getDiff() == 0.0 )
							{
							ro.setDiffProzent(0.0);
							}
							else
							{
								ro.setDiffProzent(100.0);	
							}
						}
						else
						{
						ro.setDiffProzent(abs(ro.getDiff()/ro.getRealValue()));
						}
						System.out.println("Diff ="+ ro.getDiffProzent());
						System.out.println("Diff in Euro="+ ro.getDiff());
						if (k == 11) 
						{
							System.out.println("geschätzter Wert = "+ wert * prozent[getMonth(calmonth_start)]);
							System.out.println("Realer Wert Wert = "+ ro.getRealValue());
							System.out.println("Abweichung  = " + ro.getDiffProzent());
							System.out.println("Abweichung in Euro  = " + ro.getDiff());
						}
						calmonth_start.add(Calendar.MONTH, 1);
						calmonth_end.add(Calendar.MONTH, 1);
					}
					
					
					
					while (calstart.before(cal_end))
					// TODO: Hier muss evtl geschaut werde, ob ein Enddatum vorhanden ist.
					{
						Hashtable trans = new Hashtable();

						// Einfuegen
						// zuerst schauen, ob der Eintrag schon da ist
						trans.put("datum", (String) formatter.format(calstart.getTime()));
						trans.put("user", "Wiederholung");
						trans.put("name", "Forecast " + kategorie.get("name"));
						trans.put("konto", konto.get("id"));
						// trans.put("wert", wertMonth.toString());
						trans.put("wert", wert * prozent[getMonth(calstart)]);
						trans.put("partner", "");
						trans.put("beschreibung", "");
						trans.put("kategorie", kategorie.get("id"));
						trans.put("kor_id", "0");
						trans.put("cycle", "0");
						trans.put("planed", "j");
						// System.out.println("Transwert "+trans.get("wert"));
						if (wert * prozent[getMonth(calstart)] > 0.001 || wert * prozent[getMonth(calstart)] < -0.001) {
							//db.insertTransaktionZycl(trans);
						}
						calstart.add(Calendar.MONTH, 1);

					}
					// --------------------------Eintag in kategorien
				}
			}

		}

	}

	private int getMonth(Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM");

		return new Integer(formatter.format(cal.getTime())) - 1;
	}
	
	
	
	private void printRos (Map <Integer,ResultObject> ros ) {
		System.out.println("/////////////////");
		for (int k = 0; k < 12; k++) {
			{   
				System.out.print(ros.get(k).getYearback1());
				System.out.print(" ");
				System.out.print(ros.get(k).getYearback2());
				System.out.print(" ");
				System.out.print(ros.get(k).getYearback3());
				System.out.println();
			}
		}
	}
	
	private double abs(double a)
	{
		if ( a >= 0.0) {
			return a;
		}
		else
		{
			return a * -1;
		}
	}
}