package budget;

import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Vector;
import java.util.Hashtable;
import cbudgetbase.DB;

public class UpdateZyklischeTransaktion {
	
	public boolean update(DB db,PrintWriter out,Hashtable settings )
	{
		Vector vec=db.getAllCycleTransaktionen();
		Hashtable hash;
		Hashtable trans = new Hashtable();
		boolean meldung=false;
		//Überprüfe, ob zu überprüfen ist
		Calendar cal= Calendar.getInstance();
		Calendar cal_end= Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Vector planed =new Vector();
		if (db.getPlanedTransaktionen(formatter.format(cal.getTime()),planed))
		{
			for (int i=0;i<planed.size();i++)
			{
			out.println("<p>");
			out.println("Meldung: Geplante Transaktion "+((String)((Hashtable)planed.elementAt(i)).get("name")) +" liegt in der Vergangenheit und wird gelöscht");
			out.println("<p>");
			}
			meldung=true;
			db.deletePlanedTransaktionen(formatter.format(cal.getTime()));
			
		}
		
		try {
		cal_end.setTime(formatter.parse(((String)settings.get("checkdatum"))));
		} catch (Exception ex)
		{
			System.err.println("Falsches Datumsformat in der Datenbank");
			System.out.println((String)settings.get("checkdatum"));
		}
		cal_end.add(Calendar.MONTH, 1);
		if (cal_end.after(cal))
		{
			System.out.println("Keine prüfung notwendig");
			return meldung;
		}
		db.updatesetting("checkdatum",formatter.format(cal.getTime()));
		cal_end=Calendar.getInstance();
		cal_end.add(Calendar.YEAR,1);
		for (int i=0; i<vec.size();i++)
		{
		hash=(Hashtable)vec.elementAt(i);
		System.out.println(hash);
		if (((String)hash.get("noend")).equals("ja"))
		{
			
			String datum=formatter.format(cal.getTime());
			trans=db.getLastCycleTransaktion(formatter.format(cal.getTime()),(Integer)hash.get("korid"));
			System.out.println( "Last = "+trans);
			if(trans.get("id")== null)
			{
				continue;
			}
			
			
			cal.setTime((java.util.Date)trans.get("datum"));
			while (cal.before(cal_end))
			{
				//Einfuegen
				//zuerst schauen, ob der Eintrag schon da ist
				trans.put("datum", (String)formatter.format(cal.getTime()));
				trans.put("user", "Wiederholung");
				if (db.getCycleTransaktion(trans))
				{
					//System.out.println("Eintrag bereits vorhanden");
				}
				else
				{
					
					db.insertTransaktionZycl(trans);
					out.println("<p>");
					out.println("Meldung: Konto "+db.getKontoName((Integer)trans.get("konto"))+" wurde angepasst");
					out.println("<p>");
					out.println("Eintrag "+trans.get("name")+" wurde gemacht");
					meldung=true;
				}
					//Schaue,ob es einen Gegenbuchung gibt;
					if (((Integer)trans.get("cycle"))== 2)

					{
						Hashtable kor_konto=db.getKorKontoId(((Integer)trans.get("konto")).toString(),trans.get("kor_id").toString());
						kor_konto.put("datum", (String)formatter.format(cal.getTime()));
						kor_konto.put("user", "Wiederholung");
						if (db.getCycleTransaktion(kor_konto))
						{
							System.out.println("Eintrag bereits vorhanden");
						}
						else
						{
						db.insertTransaktionZycl(kor_konto);
						out.println("<p>");
						out.println("Meldung: Konto "+db.getKontoName((Integer)kor_konto.get("konto"))+" wurde angepasst");
						out.println("<p>");
						out.println("Eintrag "+kor_konto.get("name")+" wurde gemacht");
						meldung=true;
						}
					}
				
				int int_delta=(Integer)hash.get("delta");
				if (((String)hash.get("wiederholung")).equals("monatlich"))
				{
					cal.add(Calendar.MONTH,int_delta);
					continue;
				}
				if (((String)hash.get("wiederholung")).equals("taeglich"))
				{
					cal.add(Calendar.DATE,int_delta);
					continue;
				}
				
				if (((String)hash.get("wiederholung")).equals("jaehrlich"))
				{
					cal.add(Calendar.YEAR,int_delta);
					continue;
				}
				else
				{
					cal.add(Calendar.MONTH,int_delta);
				}
					
			}
		}
		}
		return meldung;
	}

}
