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
		// Dann von allen Kategorien den Durchschnitt der letzten drei Jahre holen und
		// den Durchschnitt pro Monat ausrechnen.
		for (int i = 0; i < kategories.size(); i++) {
			for (int j = 0; j < konten.size(); j++) {
				Hashtable kategorie = (Hashtable) kategories.elementAt(i);
				Hashtable konto = (Hashtable) konten.elementAt(j);
				
				/*if (!((String) kategorie.get("name")).equals("Lebensmittel")) {
					continue;
				}

				if (!((String) konto.get("name")).equals("Sparkasse Giro")) {
					continue;
				}*/
					
				String where = " kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id")
						+ " and planed = 'j' and name like 'Forecast%' ";
				db.deleteTransaktionWithWhere(where);
				if (kategorie.get("forecast").equals(0)) {
					// System.out.println("Kategorie "+ kategorie.get("name") + " muss nicht
					// berechnet werden");
					continue;
				}
				where = "kategorie = " + kategorie.get("id") + " and konto_id = " + konto.get("id") + "and cycle = 0";

				double[][] montharry = new double[12][3];
               //Hier machen wir das Ganze 3 Mal hintereinander für jedes Jahr. Das sollte man eigentlich besser machen.
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
				Double wertYear2 = db.getKategorienAlleSummeWhere(formatter.format(calTowYearBack.getTime()),
						formatter.format(calOneYearBack.getTime()), where);
				calmonth_start = (Calendar) calTowYearBack.clone();
				calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);
				calmonth_end.add(Calendar.DATE,-1);
				for (int k = 0; k < 12; k++) {
					montharry[getMonth(calmonth_start)][1] = db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where);
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}

				Double wertYear1 = db.getKategorienAlleSummeWhere(formatter.format(calOneYearBack.getTime()),
						formatter.format(calnow.getTime()), where);

				calmonth_start = (Calendar) calOneYearBack.clone();
				calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);
				calmonth_end.add(Calendar.DATE,-1);
				for (int k = 0; k < 12; k++) {
					montharry[getMonth(calmonth_start)][2] = db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where);
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				Double wertUngewichtet = wertYear1 + wertYear2 + wertYear3;
				Double wert = (3 * wertYear1 + 2 * wertYear2 + wertYear3) / 6;
				if (wert > 0.001 || wert < -0.001) {
					// Rechne Prozentwert aus
					double[] prozent = new double[12];
					double gesmantprozent = 0.0;
					for (int k = 0; k < 12; k++) {
						double prozentwertwert = (montharry[k][0] + montharry[k][1] + montharry[k][2]);					
						prozent[k] = prozentwertwert / wertUngewichtet;
						gesmantprozent = gesmantprozent + prozent[k];
					}
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
							db.insertTransaktionZycl(trans);
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
}