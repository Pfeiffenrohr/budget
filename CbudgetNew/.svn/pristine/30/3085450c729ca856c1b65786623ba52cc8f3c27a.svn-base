package budget_batch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;

import budget.DB;
import budget.HeaderFooter;

public class BerechnePlanungBatch {
	static String user;
	static String pass;
	static String datenbank;
	boolean debug=true;
	
	
	
	private void berechnePlan(DBBatch db,Hashtable hash_plan,String kategorie_id)
	{
		if (debug)
		{
			System.out.println("berechnePlan");
		}
		Calendar cal_akt= Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String akt_datum=formatter.format(cal_akt.getTime());
		try {
    	
			String plan_id = ((Integer)hash_plan.get("plan_id")).toString();
			db.deletePlanCacheKategorie(plan_id, kategorie_id);
			Calendar cal_begin= Calendar.getInstance();
			Calendar cal_end= Calendar.getInstance();
			cal_begin.setTime((Date)hash_plan.get("startdatum"));
			cal_end.setTime((Date)hash_plan.get("enddatum"));
			//liegt ds jetzige Datum zwischenStart und Endzeit?
			if (cal_akt.before(cal_begin))
			{
				System.out.println("Datum ausserhalb des Zeitraums");
				return;
			}
			
			if (cal_akt.after(cal_end))
			{
				System.out.println("Ende des Planungszeitraums erreicht!");
				//cal_akt=(Calendar)cal_end.clone();
			}
			String rule_id=((Integer)hash_plan.get("rule_id")).toString();
			String rule;
			if (rule_id.equals("-1"))
			{
				//dummy
				
			rule="";
			}
			else
			{
			rule=" AND "+db.getRuleCommand(new Integer(rule_id));
			}
			cal_akt=(Calendar)cal_begin.clone();
			//Vector daten=new Vector();
			Long begin_zeit=cal_begin.getTimeInMillis();
			String kategorie=db.getKategorieName(new Integer(kategorie_id));
			Double wert_relativ=0.0;
			Double summe=0.0;
			Double prozent=0.0;
			String where="";
			if (kategorie_id.equals("-1"))
			{
			where=buildWhere( db, "ausgabe", plan_id,rule);
			//System.out.println("where ="+where);
			}
			if (kategorie_id.equals("-2"))
			{
			where=buildWhere(db, "einnahme", plan_id,rule);
			}
			//System.out.println("where danach ="+where);
			while (! cal_akt.after(cal_end))
					{
				Long akt_zeit= cal_akt.getTimeInMillis();
				
				long vergangene_tage=(akt_zeit - begin_zeit)/(3600000*24);
				Long end_zeit=cal_end.getTimeInMillis();
				long gesamte_tage=(end_zeit -begin_zeit)/(3600000*24);
			//double faktor=((double)vergangene_tage*100)/(double)gesamte_tage;
				double faktor=((double)vergangene_tage)/(double)gesamte_tage;
				if (faktor<0.05)
				{
					cal_akt.add(Calendar.DATE, 1);
					continue;
				}
			//out.println("<p>gesamt: "+gesamte_tage+" Vergangen: "+vergangene_tage+" Faktor: "+faktor+"<p>");	
		//System.out.println("Kategorie_id="+kategorie_id);
				if (kategorie_id.equals("-1")) {
			// Alle Ausgaben
			wert_relativ = db.getPlanungAllWhere(new Integer(plan_id),
					faktor, buildWhere(db, "ausgabe", plan_id,""));
			//System.out.println("Plan: "+wert_relativ);
			summe = db.getKategorienAlleSummeWhere(
					formatter.format(hash_plan.get("startdatum")),
					formatter.format(cal_akt.getTime()), where);
			//System.out.println("Summe: "+summe);
			//System.out.println("where: "+where);
		} else {
			if (kategorie_id.equals("-2")) {
				// Alle Einnahmen
				wert_relativ = db.getPlanungAllWhere(new Integer(
						plan_id), faktor,
						buildWhere(db, "einnahme", plan_id,""));
				summe = db.getKategorienAlleSummeWhere(
						formatter.format(hash_plan.get("startdatum")),
						formatter.format(cal_akt.getTime()),
						where);
			} else {
				// Kategorie
				wert_relativ = db.getKategorienAlleRecursivPlanung(
						kategorie, new Integer(plan_id), faktor);
				summe = db.getKategorienAlleRecursivSumme(kategorie,
						formatter.format(hash_plan.get("startdatum")),
						formatter.format(cal_akt.getTime()),rule);
			}
		}
				prozent=0.0;
				if ( wert_relativ ==0.0 )
				{
					prozent=0.0;
				}
				else
				{
				 prozent=(summe*100/wert_relativ);
				}
				Hashtable hash=new Hashtable();
				//hash.put("datum",formatter.format(cal_akt.getTime()));
				hash.put("datum",cal_akt.getTime());
				hash.put("datum",formatter.format(cal_akt.getTime()));
				hash.put("wert",prozent);
				hash.put("plan_id", plan_id);
				hash.put("kategorie_id", kategorie_id);
				//daten.add(hash);
				db.insertPlanCache(hash);
				cal_akt.add(Calendar.DATE, 1);
				
					}
			//session.setAttribute("chart_vec", daten);
			//System.out.println(daten);
			
		}catch (Throwable theException) {
				theException.printStackTrace();
			}
			
			
	}
	private void berechneTriggerPlan()
	{
		if (debug)
		{
			System.out.println("berechneTriggerPlan");
		}
		DBBatch db = new DBBatch();
		System.out.println("Open Connection");
    	db.dataBaseConnect(user, pass, datenbank);
        Vector allplan = db.getAllPlanungen();
        Vector tmp = db.getAllTmpUpdate();
        //Alle Kategorien ermitteln,für die Planungen berechnet werden müssen
        Hashtable plan_todo = new Hashtable();
        Calendar cal= Calendar.getInstance();
        Calendar cal_start= Calendar.getInstance();
        Calendar cal_end= Calendar.getInstance();
        System.out.println("Close Connection");
        db.closeConnection();
        for (int i=0;i<tmp.size();i++)
        {
        	cal.setTime((Date)((Hashtable)tmp.elementAt(i)).get("datum"));
        	System.out.println("Open Connection");
        	db.dataBaseConnect(user, pass, datenbank);
        	for (int j=0;j<allplan.size();j++)
        	{
        		
        		cal_start.setTime((Date)((Hashtable)allplan.elementAt(j)).get("startdatum"));
        		cal_end.setTime((Date)((Hashtable)allplan.elementAt(j)).get("enddatum"));
        		if (cal.before(cal_end) && cal.after(cal_start))
        		{
        			//Planung ist im Zeitraum und muß berechnet werden
        			Vector vec=null;
        			if (plan_todo.containsKey(((Integer)((Hashtable)allplan.elementAt(j)).get("plan_id")).toString()))
        			{
        				vec=(Vector)plan_todo.get(((Integer)((Hashtable)allplan.elementAt(j)).get("plan_id")).toString());
        			}
        			else
        			{
        			vec=new Vector();
        			}
        			if (! vec.contains((Integer)((Hashtable)tmp.elementAt(i)).get("kategorie")))
        			{
        			
        			//System.out.println("Füge hinzu"+(Integer)((Hashtable)tmp.elementAt(i)).get("kategorie"));	
        			vec.addElement((Integer)((Hashtable)tmp.elementAt(i)).get("kategorie"));
        			}
        			Vector kat = db.getAllKategorien();
        			
        			//Hier noch die Elternkategorien berechnen
        			parents(kat,db,vec,(Integer)((Hashtable)tmp.elementAt(i)).get("kategorie"));
        			plan_todo.put(((Integer)((Hashtable)allplan.elementAt(j)).get("plan_id")).toString(),vec);
        		}
        	}
        db.deleteTmpUpdate((Integer)((Hashtable)tmp.elementAt(i)).get("id"));
        System.out.println("Close Connection");
        db.closeConnection();
        }
        System.out.println(plan_todo);
        berechneAllePlan(plan_todo);
	}
	
	private void berechneAllePlan(Hashtable hash)
	{
		if (debug)
		{
			System.out.println("berechneAllePlan");
		}
		DBBatch db = new DBBatch();
		System.out.println("Open Connection");
    	db.dataBaseConnect(user, pass, datenbank);
        Vector vec = db.getAllPlanungen();
        Calendar cal_akt= Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
		String akt_datum=formatter.format(cal_akt.getTime());
		db.closeConnection();
		System.out.println("Close Connection");
        for (int i=0; i<vec.size();i++)
        {
        	Hashtable hash_plan = (Hashtable)vec.elementAt(i);
        	System.out.println("Open Connection");
        	db.dataBaseConnect(user, pass, datenbank);
        	if (! hash.containsKey((String)((Integer)hash_plan.get("plan_id")).toString()))
        	{
        		System.out.println("Plan braucht nicht berechnet werden");
        		System.out.println("Close Connection");
            	db.closeConnection();
        		continue;
        	}
        	Vector zuBerechnen=(Vector)hash.get((String)((Integer)hash_plan.get("plan_id")).toString());
        	if (((String)hash_plan.get("batch")).equals("ja"))
        			{
        		
        		Vector kat_aus=db.getAllKategorien("ausgabe");
        		for (int j=0;j<kat_aus.size();j++)
        		{
        			
        			Integer kategorie_id=(Integer)((Hashtable)kat_aus.elementAt(j)).get("id");
        			//System.out.println(kat_aus);
        			if (! zuBerechnen.contains(kategorie_id))
        			{
        				System.out.println("Überspringe "+kategorie_id);
        				continue;
        			}
        			berechnePlan(db,hash_plan,kategorie_id.toString());
        			cal_akt= Calendar.getInstance();
        			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id))
        			{
        				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        			else
        			{
        			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        		}
        		
    			Vector kat_ein=db.getAllKategorien("einnahme");
    			for (int j=0;j<kat_ein.size();j++)
        		{
        			Integer kategorie_id=(Integer)((Hashtable)kat_ein.elementAt(j)).get("id");
        			if (! zuBerechnen.contains(kategorie_id))
        			{
        				System.out.println("Überspringe "+kategorie_id);
        				continue;
        			}
        			berechnePlan(db,hash_plan,kategorie_id.toString());
        			cal_akt= Calendar.getInstance();
        			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id))
        			{
        				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        			else
        			{
        			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        		}
    			if ( zuBerechnen.contains(-1))
    			{
    				System.out.println("Berechne alle Ausgaben "+ "-1");
    				berechnePlan(db,hash_plan,"-1");
    				
    			
    			
    			cal_akt= Calendar.getInstance();
    			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),-1))
    			{
    				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-1);
    			}
    			else
    			{
    			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-1);
    			}
    			}
    			//berechnePlan(db,hash_plan,"-2");
    			if ( zuBerechnen.contains(-2))
    			{
    				System.out.println("Berechne alle Einnahmen "+ "-2");
    				berechnePlan(db,hash_plan,"-2");
    				
    			
    			cal_akt= Calendar.getInstance();
    			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),-2))
    			{
    				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-2);
    			}
    			else
    			{
    			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-2);
    			}
    			}
				
				
				//*****************************************************
				
    			
    			
				
        			}
        	System.out.println("Close Connection");
        	db.closeConnection();
        }
	}
	private void berechneAllePlan()
	{
		if (debug)
		{
			System.out.println("berechneAllePlan()");
		}
		DBBatch db = new DBBatch();
       
        Vector vec = db.getAllPlanungen();
        Calendar cal_akt= Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
		String akt_datum=formatter.format(cal_akt.getTime());
        for (int i=0; i<vec.size();i++)
        {
        	Hashtable hash_plan = (Hashtable)vec.elementAt(i);
        	System.out.println("Open Connection");
        	 db.dataBaseConnect(user, pass, datenbank);
        	if (((String)hash_plan.get("batch")).equals("ja"))
        			{
        		
        		Vector kat_aus=db.getAllKategorien("ausgabe");
        		for (int j=0;j<kat_aus.size();j++)
        		{
        			Integer kategorie_id=(Integer)((Hashtable)kat_aus.elementAt(j)).get("id");
        			berechnePlan(db,hash_plan,kategorie_id.toString());
        			cal_akt= Calendar.getInstance();
        			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id))
        			{
        				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        			else
        			{
        			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        		}
        		
    			Vector kat_ein=db.getAllKategorien("einnahme");
    			for (int j=0;j<kat_ein.size();j++)
        		{
        			Integer kategorie_id=(Integer)((Hashtable)kat_ein.elementAt(j)).get("id");
        			berechnePlan(db,hash_plan,kategorie_id.toString());
        			cal_akt= Calendar.getInstance();
        			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id))
        			{
        				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        			else
        			{
        			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        		}
    			berechnePlan(db,hash_plan,"-1");
    			cal_akt= Calendar.getInstance();
    			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),-1))
    			{
    				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-1);
    			}
    			else
    			{
    			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-1);
    			}
    			berechnePlan(db,hash_plan,"-2");
    			cal_akt= Calendar.getInstance();
    			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),-2))
    			{
    				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-2);
    			}
    			else
    			{
    			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-2);
    			}
        
				
				
				//*****************************************************
				
    			
    			
				
        			}
        	db.closeConnection();
        	System.out.println("Open Connection");
        }
	}
	private void berechneNeuPlan()
	{
		if (debug)
		{
			System.out.println("berechneNeuPlan");
		}
		DBBatch db = new DBBatch();
		System.out.println("Open Connection");
   	    db.dataBaseConnect(user, pass, datenbank);   	    
        Vector vec = db.getAllPlanungen();
        System.out.println("Close Connection");
    	db.closeConnection();
        Calendar cal_akt= Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
		String akt_datum=formatter.format(cal_akt.getTime());
        for (int i=0; i<vec.size();i++)
        {
        	Hashtable hash_plan = (Hashtable)vec.elementAt(i);
        	System.out.println("Open Connection");
        	 db.dataBaseConnect(user, pass, datenbank);
        	if (((String)hash_plan.get("batch")).equals("ja"))
        			{
        		
        		Vector kat_aus=db.getAllKategorien("ausgabe");
        		for (int j=0;j<kat_aus.size();j++)
        		{
        			Integer kategorie_id=(Integer)((Hashtable)kat_aus.elementAt(j)).get("id");
        			Hashtable hash=db.getAllPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id.toString());
        			if (hash.get("datum")==null)
        			{
        			System.out.println("Berechne Plan "+((String)hash_plan.get("name")));	
        			berechnePlan(db,hash_plan,kategorie_id.toString());
        			}
        			else
        			{
        				continue;
        			}
        			cal_akt= Calendar.getInstance();
        			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id))
        			{
        				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        			else
        			{
        			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        		}
        		
    			Vector kat_ein=db.getAllKategorien("einnahme");
    			for (int j=0;j<kat_ein.size();j++)
        		{
        			Integer kategorie_id=(Integer)((Hashtable)kat_ein.elementAt(j)).get("id");
        			Hashtable hash=db.getAllPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id.toString());
        			if (hash.get("datum")==null)
        			{
        				System.out.println("Berechne Plan "+((String)hash_plan.get("name")));	
        				berechnePlan(db,hash_plan,kategorie_id.toString());
        			}
        			else
        			{
        				continue;
        			}
        			
        			cal_akt= Calendar.getInstance();
        			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),kategorie_id))
        			{
        				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        			else
        			{
        			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),kategorie_id);
        			}
        		}
    			Hashtable hash=db.getAllPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),"-1");
    			if (hash.get("datum")==null)
    			{
    				System.out.println("Berechne Plan "+((String)hash_plan.get("name")));	
    				berechnePlan(db,hash_plan,"-1");
    			}
    			else
    			{
    				System.out.println("Close Connection");
    	        	db.closeConnection();
    				continue;
    			}
    			
    			cal_akt= Calendar.getInstance();
    			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),-1))
    			{
    				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-1);
    			}
    			else
    			{
    			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-1);
    			}
    			
    			hash=db.getAllPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),"-2");
    			if (hash.get("datum")==null)
    			{
    				System.out.println("Berechne Plan "+((String)hash_plan.get("name")));	
    				berechnePlan(db,hash_plan,"-2");
    			}
    			else
    			{
    				System.out.println("Close Connection");
    	        	db.closeConnection();
    				continue;
    			}
    			
    			
    			cal_akt= Calendar.getInstance();
    			if (db.getPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(),-2))
    			{
    				db.updatePlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-2);
    			}
    			else
    			{
    			db.insertPlanAktuell(((Integer)hash_plan.get("plan_id")).toString(), formatter.format(cal_akt.getTime()), formatTime.format(cal_akt.getTime()),-2);
    			}
        
				
				
				//*****************************************************
				
    			
    			
				
        			}
        	System.out.println("Close Connection");
        	db.closeConnection();
        }
	}
	
	private void parents (Vector kat,DB db,Vector vec,Integer kat_id)
	{
		if (debug)
		{
			System.out.println("parents");
		}
		//Suche kategorie
		for (int i=0;i<kat.size();i++)
		{
			//System.out.println("first= "+(Integer)((Hashtable)kat.elementAt(i)).get("id"));
			//System.out.println("second= "+kat_id);
			if (((Integer)((Hashtable)kat.elementAt(i)).get("id")).intValue()==kat_id.intValue())
			{
				//System.out.println("Id gefunden");
				if (! vec.contains(kat_id))
				{
					//System.out.println("Füge hinzu"+kat_id);	
				vec.addElement(kat_id);
				}
				if(((String)((Hashtable)kat.elementAt(i)).get("mode")).equals("ausgabe"))
				{
					if (! vec.contains(-1))
					{
						vec.addElement(-1);
					}
				}
				else
				{
					if (! vec.contains(-2))
					{
						vec.addElement(-2);
					}
				}
					
					
				//System.out.println("Parent ist "+(String)((Hashtable)kat.elementAt(i)).get("parent"));
				if ((String)((Hashtable)kat.elementAt(i)).get("parent")!=null)
				{
					//System.out.println("Parent ist "+(String)((Hashtable)kat.elementAt(i)).get("parent"));
					parents (kat,db,vec,db.getKategorieId((String)((Hashtable)kat.elementAt(i)).get("parent")));	
				}
			}
		}
		
	}
	
	public void searchSub (Vector all, String kat,Vector allkat,int tiefe)
	{
		for (int i=0; i<allkat.size();i++)
		{
			if (((String)((Hashtable)allkat.elementAt(i)).get("parent")) .equals(kat))
			{
				searchSub(all,(String)((Hashtable)allkat.elementAt(i)).get("name"),allkat,tiefe+1);
			}
		}
		if (tiefe>0)
		{
			all.addElement(kat);
		}
	}
	public void searchSub (Vector all, String kat,Integer id,Vector allkat,int tiefe)
	{
		for (int i=0; i<allkat.size();i++)
		{
			if (((String)((Hashtable)allkat.elementAt(i)).get("parent")) .equals(kat))
			{
				searchSub(all,(String)((Hashtable)allkat.elementAt(i)).get("name"),((Integer)((Hashtable)allkat.elementAt(i)).get("id")),allkat,tiefe+1);
			}
		}
		if (tiefe>0)
		{
			all.addElement(id);
		}
	}



String buildWhere(DB db,String mode,String plan_id,String rule)
{
	Vector all = db.getAllKategorien(mode);
	//System.err.println("Alle Kategorien:"+all);
	Vector allIds = new Vector();
	for (int i=0;i<all.size();i++)
	{
		if(! db.getPlanWertNull(plan_id,(Integer)((Hashtable)all.elementAt(i)).get("id")))
		{
		allIds.add((Integer)((Hashtable)all.elementAt(i)).get("id"));
		searchSub(allIds,(String)((Hashtable)all.elementAt(i)).get("name") , (Integer)((Hashtable)all.elementAt(i)).get("id"), all,0);
		}
		}
	//System.err.println("Alle Ids:"+allIds);
	String where="(";
	boolean first=true;
	for (int i=0;i<all.size();i++)
	{
		if(allIds.contains((Integer)((Hashtable)all.elementAt(i)).get("id")))
		{
		if (!first)
		{
			where=where+" or ";
		}
		else
		{
			first=false;
		}
		where=where+"kategorie= "+((Hashtable)all.elementAt(i)).get("id");
		}
	}
	if (where.equals("("))
			{
		//Setze Dummy ein, damit kein Fehler kommt
		where=where+" kategorie=-1 ";
			}
	where=where+")"+rule;
	return where;
}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		if (args.length != 4)
    	{
    		System.out.println("usage: BerechnePlanungBatch <user> <password> <datenbank> all|trigger");
    		System.exit(1);
    	}
		user = args[0];
		pass = args[1];
		datenbank = args [2];
		String mode =args [3];
       BerechnePlanungBatch batch = new BerechnePlanungBatch();
       if (mode.equals("all"))
       {
       batch.berechneAllePlan();
       }
       if (mode.equals("trigger"))
       {
        batch.berechneTriggerPlan();
       }
       if (mode.equals("neu"))
       {
        batch.berechneNeuPlan();
       }
    }
}
