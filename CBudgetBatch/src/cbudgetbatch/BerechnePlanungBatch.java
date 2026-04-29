package cbudgetbatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import cbudgetbase.DB;
import sonstiges.MyLogger;

//import budget.HeaderFooter;


public class BerechnePlanungBatch {
	static String user;
	static String pass;
	static String datenbank;
	boolean debug = false;


	private static final MyLogger logger = new MyLogger();

	/**
	 * Schmeisst die alten Cache Jobs raus, die Alt sind und nicht mehr gebraucht werden.
	 */
	private void cleanOldCacheEntries(DBBatch dbbatch) {
		long intervall = 3; //Anzahl der Tage nachdem gelÃ¶scht wird.
		Vector allPlan = dbbatch.getAllCachePlanAktuell();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		Calendar cal_akt = Calendar.getInstance();

		dbbatch.deleteOldForecast(formatter.format(cal.getTime()));

		for (int i = 0; i < allPlan.size(); i++) {

			Hashtable hash_plan = (Hashtable) allPlan.get(i);
			if ((Integer) hash_plan.get("inwork") == 0) {
				continue;
			}
			cal.setTime((Date) hash_plan.get("datum"));
			Date startDate = cal.getTime();
			Date endDate = cal_akt.getTime();

			long startTime = startDate.getTime();
			long endTime = endDate.getTime();
			long diffTime = endTime - startTime;
			long diffDays = diffTime / (1000 * 60 * 60 * 24);
			if (diffDays > intervall) {
				System.out.println("diffDays =" + diffDays);
				System.out.println("Delete inwork " + (Integer) hash_plan.get("plan_id") + " and kategorie =" + (Integer) hash_plan.get("kategorie"));
				//Delete inwork
				dbbatch.updateInwork((Integer) hash_plan.get("plan_id"), (Integer) hash_plan.get("kategorie"));

			}

		}
		logger.log("Start cleanunusedCaches ..");
		dbbatch.cleanunusedCaches();
		logger.log("CleanunusedCaches done!");
	}

	private void berechneTriggerPlan() {


		//System.out.println("berechneTriggerPlan ..");

		DBBatch db = new DBBatch();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		//System.out.println("Open Connection");
		db.dataBaseConnect(user, pass, datenbank);
		logger.log("Start cleaning old entries ..");
		cleanOldCacheEntries(db);
		logger.log("Cleaning old entries done!");
		UpdateZyklischeTransaktion uzt = new UpdateZyklischeTransaktion();
		logger.log("Start update zyklische Transaktionen ..");
		uzt.update(db);
		logger.log("Update zyklische Transaktionen done!");
		logger.log("Start CalculatePlanInOutPlan ..");
		CalculatePlanInOutHistory calculatePlanInOutHistory = new CalculatePlanInOutHistory();
		calculatePlanInOutHistory.planInOutHistory(db);
		logger.log("End CalculatePlanInOutPlan ..");
		Vector allplan = db.getAllPlanungen();
		Vector tmp = db.getAllTmpUpdate();
		//Alle Kategorien ermitteln,fÃ¼r die Planungen berechnet werden mÃ¼ssen
		Hashtable plan_todo = new Hashtable();
		Calendar cal = Calendar.getInstance();
		Calendar cal_start = Calendar.getInstance();
		Calendar cal_end = Calendar.getInstance();
		if (tmp.size() > 0) {
			logger.log("Gefunden " + tmp.size() + " Einträge");
		} else {
			logger.log("Keine Einträge für Update gefunden");
		}
		for (int i = 0; i < tmp.size(); i++) {
			cal.setTime((Date) ((Hashtable) tmp.elementAt(i)).get("datum"));
			//System.out.println("Open Connection");
			for (int j = 0; j < allplan.size(); j++) {

				cal_start.setTime((Date) ((Hashtable) allplan.elementAt(j)).get("startdatum"));
				cal_end.setTime((Date) ((Hashtable) allplan.elementAt(j)).get("enddatum"));
				if (cal.before(cal_end) && cal.after(cal_start)) {
					//Planung ist im Zeitraum und muÃŸ berechnet werden
					Vector vec = null;
					if (plan_todo.containsKey(((Integer) ((Hashtable) allplan.elementAt(j)).get("plan_id")).toString())) {
						vec = (Vector) plan_todo.get(((Integer) ((Hashtable) allplan.elementAt(j)).get("plan_id")).toString());
					} else {
						vec = new Vector();
					}
					if (!vec.contains((Integer) ((Hashtable) tmp.elementAt(i)).get("kategorie"))) {

						//System.out.println("FÃ¼ge hinzu"+(Integer)((Hashtable)tmp.elementAt(i)).get("kategorie"));
						vec.addElement((Integer) ((Hashtable) tmp.elementAt(i)).get("kategorie"));
					}
					Vector kat = db.getAllKategorien();

					//Hier noch die Elternkategorien berechnen
					parents(kat, db, vec, (Integer) ((Hashtable) tmp.elementAt(i)).get("kategorie"));
					plan_todo.put(((Integer) ((Hashtable) allplan.elementAt(j)).get("plan_id")).toString(), vec);
				}

			}
			Calendar cal_akt = Calendar.getInstance();
			db.setRenditeDirty((Integer) ((Hashtable) tmp.elementAt(i)).get("konto"), formatter.format((Date) ((Hashtable) tmp.elementAt(i)).get("datum")));
			db.deleteTmpUpdate((Integer) ((Hashtable) tmp.elementAt(i)).get("id"));
		}

		Enumeration<String> keys = plan_todo.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			Vector vec = (Vector) plan_todo.get(key);
			for (int j = 0; j < vec.size(); j++) {
				int prio = db.getPlanungPrioWithplanId(key);
				db.insertJobs(key, (Integer) vec.elementAt(j), prio);
				// System.out.println("Value of "+key+" is: "+hm.get(key));
			}
			//System.out.println("Open Connection");

		}
		db.closeConnection();
		//System.out.println(plan_todo);
		//berechneAllePlan(plan_todo);
	}

	private void parents(Vector kat, DB db, Vector vec, Integer kat_id) {
		if (debug) {
			System.out.println("parents");
		}
		//Suche kategorie
		for (int i = 0; i < kat.size(); i++) {
			//System.out.println("first= "+(Integer)((Hashtable)kat.elementAt(i)).get("id"));
			//System.out.println("second= "+kat_id);
			if (((Integer) ((Hashtable) kat.elementAt(i)).get("id")).intValue() == kat_id.intValue()) {
				//System.out.println("Id gefunden");
				if (!vec.contains(kat_id)) {
					//System.out.println("FÃ¼ge hinzu"+kat_id);	
					vec.addElement(kat_id);
				}
				if (((String) ((Hashtable) kat.elementAt(i)).get("mode")).equals("ausgabe")) {
					if (!vec.contains(-1)) {
						vec.addElement(-1);
					}
				} else {
					if (!vec.contains(-2)) {
						vec.addElement(-2);
					}
				}


				//System.out.println("Parent ist "+(String)((Hashtable)kat.elementAt(i)).get("parent"));
				if ((String) ((Hashtable) kat.elementAt(i)).get("parent") != null) {
					//System.out.println("Parent ist "+(String)((Hashtable)kat.elementAt(i)).get("parent"));
					parents(kat, db, vec, db.getKategorieId((String) ((Hashtable) kat.elementAt(i)).get("parent")));
				}
			}
		}

	}

	public void searchSub(Vector all, String kat, Vector allkat, int tiefe) {
		for (int i = 0; i < allkat.size(); i++) {
			if (((String) ((Hashtable) allkat.elementAt(i)).get("parent")).equals(kat)) {
				searchSub(all, (String) ((Hashtable) allkat.elementAt(i)).get("name"), allkat, tiefe + 1);
			}
		}
		if (tiefe > 0) {
			all.addElement(kat);
		}
	}

	public void searchSub(Vector all, String kat, Integer id, Vector allkat, int tiefe) {
		for (int i = 0; i < allkat.size(); i++) {
			if (((String) ((Hashtable) allkat.elementAt(i)).get("parent")).equals(kat)) {
				searchSub(all, (String) ((Hashtable) allkat.elementAt(i)).get("name"), ((Integer) ((Hashtable) allkat.elementAt(i)).get("id")), allkat, tiefe + 1);
			}
		}
		if (tiefe > 0) {
			all.addElement(id);
		}
	}


	String buildWhere(DB db, String mode, String plan_id, String rule) {
		Vector all = db.getAllKategorien(mode);
		//System.err.println("Alle Kategorien:"+all);
		Vector allIds = new Vector();
		for (int i = 0; i < all.size(); i++) {
			if (!db.getPlanWertNull(plan_id, (Integer) ((Hashtable) all.elementAt(i)).get("id"))) {
				allIds.add((Integer) ((Hashtable) all.elementAt(i)).get("id"));
				searchSub(allIds, (String) ((Hashtable) all.elementAt(i)).get("name"), (Integer) ((Hashtable) all.elementAt(i)).get("id"), all, 0);
			}
		}
		//System.err.println("Alle Ids:"+allIds);
		String where = "(";
		boolean first = true;
		for (int i = 0; i < all.size(); i++) {
			if (allIds.contains((Integer) ((Hashtable) all.elementAt(i)).get("id"))) {
				if (!first) {
					where = where + " or ";
				} else {
					first = false;
				}
				where = where + "kategorie= " + ((Hashtable) all.elementAt(i)).get("id");
			}
		}
		if (where.equals("(")) {
			//Setze Dummy ein, damit kein Fehler kommt
			where = where + " kategorie=-1 ";
		}
		where = where + ")" + rule;
		return where;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	
		if (args.length != 4)
    	{
    		System.out.println("usage: BerechnePlanungBatch <user> <password> <connectstring,z.B: jdbc:postgresql://192.168.2.28/> all|trigger  ");
    		System.exit(1);
    	}
		user = args[0];
		pass = args[1];
		datenbank = args [2];
		String mode =args [3];
       BerechnePlanungBatch batch = new BerechnePlanungBatch();
       //BasicConfigurator.configure();
       if (mode.equals("trigger"))
       {
        batch.berechneTriggerPlan();
       }
       
 
      
    }
}
