package cbudgetbatch;

import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Vector;
import java.util.Hashtable;
import cbudgetbase.DB;

public class UpdateZyklischeTransaktion {

	public boolean update(DB db) {
		Vector vec = db.getAllCycleTransaktionen();
		Hashtable hash;
		Hashtable trans = new Hashtable();
		boolean meldung = false;
		// Überprüfe, ob zu überprüfen ist
		Calendar cal = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		 long start = System.currentTimeMillis();
		Vector planed = new Vector();
		if (db.getPlanedTransaktionen(formatter.format(cal.getTime()), planed)) {
			
			meldung = true;
			db.deletePlanedTransaktionen(formatter.format(cal.getTime()));

		}

		try {
			Hashtable settings = db.getSettings();
			cal_end.setTime(formatter.parse(((String) settings.get("checkdatum"))));
		} catch (Exception ex) {
			System.err.println("Falsches Datumsformat in der Datenbank");
			// System.out.println((String)settings.get("checkdatum"));
		}
		cal_end.add(Calendar.MONTH, 1);
		if (cal_end.after(cal)) {
			//System.out.println("Keine prüfung notwendig");
			return meldung;
		}
		System.out.println("Berechne Zyklische Updates ...");
		db.updatesetting("checkdatum", formatter.format(cal.getTime()));
		
		Calendar cal_now = cal = Calendar.getInstance();
		for (int i = 0; i < vec.size(); i++) {
			hash = (Hashtable) vec.elementAt(i);
			cal_end = Calendar.getInstance();
			 //System.out.println(hash);
			if (((String) hash.get("noend")).equals("ja")) {
				cal_end.add(Calendar.YEAR, 30);
				//System.out.println("noend found "+ hash.get("name")+" "+ formatter.format(cal_end.getTime()));

			} else {

				cal_end.setTime((java.util.Date) hash.get("end_datum"));

			}
			cal = Calendar.getInstance();
			trans = db.getLastCycleTransaktion(formatter.format(cal.getTime()), (Integer) hash.get("korid"));
			// System.out.println( "Last = "+trans);
			if (trans.get("id") == null) {
				//System.out.println("trans nicht gefunden "+hash.get("korid")+" "+formatter.format(cal.getTime()));				
				continue;
			}
			 cal.setTime((java.util.Date)trans.get("datum"));
			 //System.out.println("cal = "+ hash.get("name")+" "+ formatter.format(cal.getTime()));
			while (cal.before(cal_end))
			// TODO: Hier muss evtl geschaut werde, ob ein Enddatum vorhanden ist.
			{
				// Einfuegen
				
				//Wenn Eintrag in der Vergangenheit liegt, dann ist nicht zu tun
				if (cal.before(cal_now))
				{
					//System.out.println("Eintrag in der Vergangenheit. Nichts zu tun");
				}
				else
				{
				// zuerst schauen, ob der Eintrag schon da ist
				trans.put("datum", (String) formatter.format(cal.getTime()));
				trans.put("user", "Wiederholung");
				if (db.getCycleTransaktion(trans)) {
					//System.out.println("Eintrag bereits vorhanden");
				} else {

					db.insertTransaktionZycl(trans);
					
					meldung = true;
				}
				// Schaue,ob es einen Gegenbuchung gibt;
				if (((Integer) trans.get("cycle")) == 2)

				{
					Hashtable kor_konto = db.getKorKontoId(((Integer) trans.get("konto")).toString(),
							trans.get("kor_id").toString());
					kor_konto.put("datum", (String) formatter.format(cal.getTime()));
					kor_konto.put("user", "Wiederholung");
					if (db.getCycleTransaktion(kor_konto)) {
						//System.out.println("Eintrag bereits vorhanden");
					} else {
						db.insertTransaktionZycl(kor_konto);
						
						meldung = true;
					}
				}
				}
				int int_delta = (Integer) hash.get("delta");
				if (((String) hash.get("wiederholung")).equals("monatlich")) {
					cal.add(Calendar.MONTH, int_delta);
					continue;
				}
				if (((String) hash.get("wiederholung")).equals("taeglich")) {
					cal.add(Calendar.DATE, int_delta);
					continue;
				}

				if (((String) hash.get("wiederholung")).equals("jaehrlich")) {
					cal.add(Calendar.YEAR, int_delta);
					continue;
				} else {
					cal.add(Calendar.MONTH, int_delta);
				}

			}
		}
		long duration = (System.currentTimeMillis() - start)/1000;
		System.out.println("Dauer des Updates:  " + duration+" Sekunden" );
		return meldung;
	}

}
