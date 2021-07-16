package cbudgetbatch.forecast;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cbudgetbatch.DBBatch;

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
		ResultObjectAll roa = new ResultObjectAll();
		Gewichtung gewichtung = new Gewichtung();
		gewichtung.setyear1back(3);
		gewichtung.setyear2back(2);
		gewichtung.setyear3back(1);
		
		forecast.getAllKategoriesWithForecast(db,roa,gewichtung,-1);
		//forecast.getAllKategoriesWithForecast(db,roa,gewichtung,-2);
		
		forecast.calculate(roa,gewichtung);

		db.closeConnection();
	}

	private void getAllKategoriesWithForecast(DBBatch db,ResultObjectAll roa,Gewichtung gewichtung,int yearBack) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calOneYearBack = Calendar.getInstance();
		Calendar calTowYearBack = Calendar.getInstance();
		Calendar calThreeYearBack = Calendar.getInstance();
		Calendar calnow = Calendar.getInstance();
		// Drei Jahre zurück rechnen
		calnow.add(Calendar.YEAR, yearBack);
		calThreeYearBack.add(Calendar.YEAR, -3+yearBack);
		calTowYearBack.add(Calendar.YEAR, -2+yearBack);
		calOneYearBack.add(Calendar.YEAR, -1+yearBack);
		// Erst mal alle Kategorien holen.
		Vector kategories = db.getAllKategorien();
		// Dann alle Konten
		Vector konten = db.getAllKonto();
		
		roa.setAbweichungGesamt(0.0);
		// Dann von allen Kategorien den Durchschnitt der letzten drei Jahre holen und
		// den Durchschnitt pro Monat ausrechnen.
		for (int i = 0; i < kategories.size(); i++) {
			System.out.println("Bearbeite kategorie " +i+ " von "+kategories.size());
			for (int j = 0; j < konten.size(); j++) {
				Hashtable kategorie = (Hashtable) kategories.elementAt(i);
				Hashtable konto = (Hashtable) konten.elementAt(j);
				
				/*
				if (!((String) kategorie.get("name")).equals("Lebensmittel")) {
					continue;
				}
				
				if (!((String) konto.get("name")).equals("Sparkassen Depot")) {
					continue;
				}*/
				ResultObjectKategorieKonto rsoy = new ResultObjectKategorieKonto();
				rsoy.setJahr(getYear(calnow));
				rsoy.setKategorie((String)kategorie.get("name"));
				rsoy.setKonto((String)konto.get("name"));
				rsoy.setAbweichungjahr(0.0);
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
				Map <Integer,ResultObjectMonth> ros  = new HashMap<Integer,ResultObjectMonth>();
               //Hier machen wir das Ganze 3 Mal hintereinander für jedes Jahr. Das sollte man eigentlich besser machen.
				//System.out.println("----Year3back");
				Double wertYear3 = db.getKategorienAlleSummeWhere(formatter.format(calThreeYearBack.getTime()),
						formatter.format(calTowYearBack.getTime()), where);
				
				Calendar calmonth_start = (Calendar) calThreeYearBack.clone();
				Calendar calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);
				calmonth_end.add(Calendar.DATE,-1);
				rsoy.setWertYear3(wertYear3);
				for (int k = 0; k < 12; k++) {
					ResultObjectMonth ro = new ResultObjectMonth(); 						
					 ro.setYearback1(db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where));
					 
					 ro.setMonth(getMonth(calmonth_start));
					ros.put(getMonth(calmonth_start),ro);
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				
				//System.out.println("----Year2back");
				Double wertYear2 = db.getKategorienAlleSummeWhere(formatter.format(calTowYearBack.getTime()),
						formatter.format(calOneYearBack.getTime()), where);
				calmonth_start = (Calendar) calTowYearBack.clone();
				calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);
				calmonth_end.add(Calendar.DATE,-1);
				rsoy.setWertYear2(wertYear2);
				for (int k = 0; k < 12; k++) {					
					ResultObjectMonth ro = ros.get(getMonth(calmonth_start));
					ro.setYearback2(db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where));
					
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				//System.out.println("----Year1back");
				Double wertYear1 = db.getKategorienAlleSummeWhere(formatter.format(calOneYearBack.getTime()),
						formatter.format(calnow.getTime()), where);

				calmonth_start = (Calendar) calOneYearBack.clone();
				calmonth_end = (Calendar) calmonth_start.clone();
				calmonth_end.add(Calendar.MONTH, 1);
				calmonth_end.add(Calendar.DATE,-1);
				rsoy.setWertYear1(wertYear1);
				for (int k = 0; k < 12; k++) {
					ResultObjectMonth ro = ros.get(getMonth(calmonth_start));
					ro.setYearback3(db.getKategorienAlleSummeWhere(
							formatter.format(calmonth_start.getTime()), formatter.format(calmonth_end.getTime()),
							where));
					calmonth_start.add(Calendar.MONTH, 1);
					calmonth_end.add(Calendar.MONTH, 1);
				}
				
				
				//Berechn gesamtWert für jedes Ro
				for (int k = 0; k < 12; k++) {
					//System.out.println("K = " +k);
					ResultObjectMonth ro = ros.get(k);
					
					ro.setGesamt(ro.getYearback1()+ro.getYearback2()+ro.getYearback3());
					
				}
				Double wertUngewichtet = wertYear1 + wertYear2 + wertYear3;
				Double wert = (gewichtung.getyear1back() * wertYear1 + gewichtung.getyear2back() * wertYear2 + gewichtung.getyear3back() * wertYear3) / (gewichtung.getyear1back() +gewichtung.getyear2back() +gewichtung.getyear3back());   ;
				//Double wert = ( wertYear1 +  wertYear2 + wertYear3) / 3;
				if (wert > 0.001 || wert < -0.001) {
					// Rechne Prozentwert aus
					double[] prozent = new double[12];
					double gesamtprozent = 0.0;
					for (int k = 0; k < 12; k++) {
						//double prozentwertwert = (montharry[k][0] + montharry[k][1] + montharry[k][2]);	
					
						ResultObjectMonth ro = ros.get(k);
						double prozentwertwert = (ro.getGesamt());
						prozent[k] = prozentwertwert / wertUngewichtet;
						ro.setProzentsatzFuerMonat(prozent[k]);
						gesamtprozent = gesamtprozent + prozent[k];
					}
					//System.out.println("Gesamtprozent= "+ gesamtprozent);
					Double wertMonth = wert / 12;
					wertMonth = Math.round(100.0 * wertMonth) / 100.0;
					
					Calendar cal_end = Calendar.getInstance();
					cal_end.add(Calendar.YEAR, 30);
					Calendar calstart = Calendar.getInstance();
					calstart.add(Calendar.MONTH, 1);
					calstart.add(Calendar.DATE, 6);
					
					//Ab hier beginnt die Auswertung
					//System.out.println("!!!!!!!!Auswertung!!!!!!!!!");
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.YEAR, yearBack);
					calmonth_start = (Calendar) cal.clone();
				    calmonth_end = (Calendar) cal.clone();
					calmonth_end.add(Calendar.MONTH, 1);
					//System.out.println("----Now");
					for (int k = 0; k < 12; k++) {
						ResultObjectMonth ro = ros.get(getMonth(calmonth_start));
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
						
						rsoy.setAbweichungjahr(rsoy.getAbweichungjahr()+ro.getDiff());
						rsoy.setRsom(ros);
						calmonth_start.add(Calendar.MONTH, 1);
						calmonth_end.add(Calendar.MONTH, 1);
					
					
					//System.out.println("Abweichung im Jahr = " + rsoy.getAbweichungjahr());
						
					
					if (roa.getAbweichungGesamt() < rsoy.getAbweichungjahr() )
					{
						roa.setAbweichungGesamt(rsoy.getAbweichungjahr());
						roa.setKategorieGroesteAbweichung(rsoy.getKategorie());
						roa.setKontoGroesteAbweichung(rsoy.getKonto());
					}
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
					Map mymap = roa.getRoy();
					mymap.put(roa.getCounter(),rsoy);
					roa.count();
					roa.setRoy  (mymap);
					// --------------------------Eintag in kategorien
				}
				
			}

		}
		System.out.print("Abweichung gesamt = " + roa.getAbweichungGesamt());
		System.out.print("Konto = " + roa.getKontoGroesteAbweichung());
		System.out.println("Kategorie = " + roa.getKategorieGroesteAbweichung()); 

	}
	
	private void calculate(ResultObjectAll roa, Gewichtung gewichtung) {

		System.out.println("ROA Anzahl " + roa.getRoy().size());
		for (Map.Entry<Integer, ResultObjectKategorieKonto> entryYear : roa.getRoy().entrySet()) {
			ResultObjectKategorieKonto roy = entryYear.getValue();
			Double wertUngewichtet = roy.getWertYear1() + roy.getWertYear2() + roy.getWertYear3();
			Double wert = (gewichtung.getyear1back() * roy.getWertYear1()
					+ gewichtung.getyear2back() * roy.getWertYear2() + gewichtung.getyear3back() * roy.getWertYear3())
					/ (gewichtung.getyear1back() + gewichtung.getyear2back() + gewichtung.getyear3back());
			;
			if (wert > 0.001 || wert < -0.001) {
				// Rechne Prozentwert aus
				//double prozent =0.0;
				double gesamtprozent = 0.0;

				for (Map.Entry<Integer, ResultObjectMonth> entryMonth : entryYear.getValue().getRsom().entrySet()) {
					//System.out.println(entryMonth.getKey());
					ResultObjectMonth rom = entryMonth.getValue();
					double prozentwertwert = rom.getGesamt();
					gesamtprozent = gesamtprozent + rom.getProzentsatzFuerMonat();
					Double wertMonth = wert / 12;
					wertMonth = Math.round(100.0 * wertMonth) / 100.0;
					rom.setDiff(abs(wert * rom.getProzentsatzFuerMonat() - rom.getRealValue()));
					if (rom.getRealValue() == 0.0 )
					{
						if (rom.getDiff() == 0.0 )
						{
						rom.setDiffProzent(0.0);
						}
						else
						{
							rom.setDiffProzent(100.0);	
						}
					}
					else
					{
					rom.setDiffProzent(abs(rom.getDiff()/rom.getRealValue()));
					}
					// Auf zwei Nachkommastellen formatieren
					rom.setYearback1(Math.round(100.0 * rom.getYearback1()) / 100.0);
					rom.setYearback2(Math.round(100.0 * rom.getYearback2()) / 100.0);
					rom.setYearback3(Math.round(100.0 * rom.getYearback3()) / 100.0);
					rom.setRealValue(Math.round(100.0 * rom.getRealValue()) / 100.0);
					String kategorie = roy.getKategorie() + ","+ roy.getKonto();
					if (rom.getRealValue() != 0.0)
					{
					writeData(rom.getYearback1()+","+rom.getYearback2()+","+rom.getYearback3()+","+rom.getRealValue()+"," + kategorie +","+ rom.getMonth()+"\n");
					}
				}

			}
		}

	}
	
	private void writeData(String content)
	{
		try {
		String filename="C:/temp/myfile";
		File file = new File(filename);
		 BufferedWriter output = null;
        output = new BufferedWriter(new FileWriter(file,true));
        output.write(content);
        output.close();
		}
		catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		}
	}
      
 /*     System.out.println("Successfully wrote to the file. " +filename);
	
 } catch (IOException e) {
   System.out.println("An error occurred.");
   e.printStackTrace();
		}
	}*/

	private int getMonth(Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM");

		return new Integer(formatter.format(cal.getTime())) - 1;
	}
	
	private int getYear(Calendar cal) {
		SimpleDateFormat formatter = new SimpleDateFormat("YYYY");

		return new Integer(formatter.format(cal.getTime()));
	}
	
	
	
	private void printRos (Map <Integer,ResultObjectMonth> ros ) {
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