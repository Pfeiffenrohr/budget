package cbudgetbatch;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;


public class DBBatch extends DB {
	public boolean dataBaseConnect(String user, String passwort, String connectstring) {
		if (debug) if (debug) System.out.println("Verbinde mich zur Datenbank");
		try {
			try {
				Class.forName("org.postgresql.Driver").newInstance(); // DB-
																		// Treiber
																		// laden
			} catch (Exception E) {
				System.err
						.println("Konnte MySQL Datenbank-Treiber nicht laden!");
				return false;
			}

			//String url = "jdbc:postgresql://192.168.2.28/"+datenbank;
			String url = connectstring;
			con = DriverManager.getConnection(url, user, passwort); // Verbindung
																		// herstellen
			if (debug) System.out.println("Verbindung erstellt");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Treiber fuer PSQL nicht gefunden"); 
			return false;
		}
		return true;
	}
	
	public boolean isAlreadyInsert( Hashtable hash) {
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			Integer konto = getKontoId((String)hash.get("konto"));
			Integer kategorie =this.getKategorieId((String)hash.get("kategorie"));
			String str_stm="select id from transaktionen where konto_id="+konto+" and kategorie ="+kategorie+" and name='"+hash.get("name")+"' and wert="+hash.get("wert")+" and datum='"+((String)hash.get("datum"))+"'";
			System.out.println(str_stm);
			stmt = con.prepareStatement(str_stm);
			res = stmt.executeQuery();
			if (res.next()) 
			{
				return true;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
			return false;
		}
		System.out.println("Select-Anweisung ausgefÃ¼hrt");
		// return summe/(float)getAnz(tag,monat,year);
		return false;
	}

	public boolean checkKonto(String konto)
	{
	try {

		PreparedStatement stmt;
		ResultSet res = null;
		String str_stm="select id from konten where kontoname='"+konto+"'";
		System.out.println(str_stm);
		stmt = con.prepareStatement(str_stm);
		res = stmt.executeQuery();
		if (res.next()) 
		{
			return true;
		}
	} catch (SQLException e) {
		System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
		return false;
	}
	System.out.println("Select-Anweisung ausgefÃ¼hrt");
	// return summe/(float)getAnz(tag,monat,year);
	return false;
}

	public boolean checkKategorie(String kategorie)
	{
	try {

		PreparedStatement stmt;
		ResultSet res = null;
		String str_stm="select id from kategorien where name='"+kategorie+"'";
		System.out.println(str_stm);
		stmt = con.prepareStatement(str_stm);
		res = stmt.executeQuery();
		if (res.next()) 
		{
			return true;
		}
	} catch (SQLException e) {
		System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
		return false;
	}
	System.out.println("Select-Anweisung ausgefÃ¼hrt");
	// return summe/(float)getAnz(tag,monat,year);
	return false;
}
	
	public boolean insertPlanCache(Hashtable hash)
	{
		try {

			PreparedStatement stmt;
			String stm= "insert into plan_cache values(default," 
				+ hash.get("plan_id") + ","
				+ hash.get("kategorie_id") + ",'"
				+ ((String)hash.get("datum")) + "',"
			    + hash.get("wert") + ","
			    + hash.get("initial") + ",'"
			    +  hash.get("initialDatum") +"')";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausfÃ¼hren" + e);
			 return false;
		}
		 return true;
	}
	public boolean deletePlanCacheKategorie(String plan_id,String kategorie_id)
	{
		try {

			PreparedStatement stmt;
			String str_stm="delete from plan_cache where plan_id="+plan_id+" and kategorie_id="+kategorie_id;
			if (debug) System.out.println(str_stm);
			stmt = con.prepareStatement(str_stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausfÃ¼hren" + e);
			 return false;
		}
		return true;
	}
	
	public boolean insertPlanCacheInitial(Hashtable hash)
	{
		try {

			PreparedStatement stmt;
			String stm= "insert into plan_cache_initial values(default," 
				+ hash.get("plan_id") + ","
				+ hash.get("kategorie_id") + ",'"
				+ ((String)hash.get("datum")) + "',"
			    + hash.get("wert") + ",'"
			    + hash.get("initialDatum") +"')";
			
			if (debug) System.out.println(stm);
		
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausfÃ¼hren" + e);
			 return false;
		}
		 return true;
	}
	
	public Hashtable getPlanCacheInitial(String planID, String kategorieID,String datum) {
		Hashtable hash = new Hashtable(); 
		double wert =9999999999.9;
		Date initialDatum = new Date();
		try {
           
			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select wert,initial_datum from plan_cache_initial where plan_id = "+planID+ " and kategorie_id = "+kategorieID +" and datum = '"+datum + "'");
			//.prepareStatement("select wert,datum from plan_cache_initial where plan_id = "+planID+ " and kategorie_id = "+kategorieID+" and datum = '2021-01-18' limit 1"); 
			res = stmt.executeQuery();
		
			while (res.next()) {
				wert=res.getDouble("wert");
				initialDatum=  res.getDate("initial_datum");
				
			}
			hash.put("wert",wert);
			hash.put("initialDatum",initialDatum);
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgefÃ¼hrt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
	public boolean getPlanAktuellIsInWork(String plan_id ,Integer kategorie) {
		//Hashtable hash = new Hashtable();
		try {
			
			PreparedStatement stmt;
			ResultSet res = null;
			Integer isInwork=0;
			stmt = con
					.prepareStatement("select inwork from plan_aktuell where plan_id="+plan_id+" and kategorie="+kategorie);
			res = stmt.executeQuery();
			while (res.next()) {
			 isInwork =new Integer(res.getInt("inwork"));
			}
		
		
		if (isInwork==1)
		{
			return true;
		}
		else
		{
			return false;
		}
			
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
			return false;
		}
	}
	
	public boolean getPlanAktuell(String plan_id ,Integer kategorie) {
		Hashtable hash = new Hashtable();
		try {
			
			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select datum,zeit from plan_aktuell where plan_id="+plan_id+" and kategorie="+kategorie);
			res = stmt.executeQuery();
			if (res.next())
			{
				return true;
			}
			else 
			{
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
			return false;
		}
	}
	
	
	public boolean updatePlanAktuell(String plan_id, String datum, String zeit,Integer kategorie,Integer inwork,Integer duration)
	{
		try {

			PreparedStatement stmt;
			String stm= "update plan_aktuell set " +
			"plan_id = "+ plan_id + "," +
			"datum='"+datum+"',"+
			"inwork="+ inwork+","+
			"duration="+duration +","+
			"zeit = '"+zeit+"' where plan_id = "+plan_id+" and kategorie="+kategorie;
			//if (debug) 
			System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausfÃ¼hren" + e);
			return false;
		}
		 return true;
	}
	
	public Hashtable getPlanAktuellDateTime(String plan_id ,Integer kategorie) {
		Hashtable hash = new Hashtable();
		//hash.put("datum", (Date) res.getDate("datum"));
		
		try {
			
			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select datum,zeit from plan_aktuell where plan_id="+plan_id+" and kategorie="+kategorie);
			res = stmt.executeQuery();
			
			while (res.next()) {
				hash.put("datum", (Date) res.getDate("datum"));	
				hash.put("zeit", (String) res.getString("zeit"));	
				}
			
		return hash;	
			
			
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
			return hash;
		}
	}
	
	public boolean insertPlanAktuell(String plan_id, String datum, String zeit,Integer kategorie,Integer inwork, Integer duration)
	{
		try {

			PreparedStatement stmt;
			String stm= "insert into plan_aktuell values(default," 
				+ plan_id + ",'"
				+ datum + "','"
			    + zeit + "',"
			    + kategorie +","
			    + inwork +","
			    + duration + ")";
			//if (debug) 
				System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausfÃ¼hren" + e);
			 return false;
		}
		 return true;
	}
	
	public Vector getAllTmpUpdate() {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,datum,kategorie,konto from tmp_update order by kategorie");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("datum", (Date) res.getDate("datum"));		
				hash.put("kategorie", (Integer) res.getInt("kategorie"));
				hash.put("konto", (Integer) res.getInt("konto"));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausfÃ¼hren" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgefÃ¼hrt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	
	public boolean deleteTmpUpdate(Integer id) {
		try {

			PreparedStatement stmt;
			// ResultSet res = null;
			//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
			String stm_str="";
		
				stm_str="delete from tmp_update where id=" + id;
			if (debug) System.out.println(stm_str);
			stmt = con.prepareStatement(stm_str);
			// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
			// " where datum="+jahr+monat+tag+
			// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausfÃ¼hren" + e);
			 return false;
		}
		return true;
	}

	public Hashtable getJob() {
		Hashtable hash = new Hashtable();
		try {
			int random = (int)(Math.random() * 10 + 1);
			PreparedStatement stmt;
			ResultSet res = null;
			ResultSet delres = null;
			if ( random % 10 == 0 ) {
				stmt = con
						.prepareStatement("select id,plan_id,kategorie from tmpplanningjobs  order by id limit 1");
			}
				else {
				stmt = con
						.prepareStatement("select id,plan_id,kategorie from tmpplanningjobs  order by prio DESC, id limit 1");
			}
			res = stmt.executeQuery();
			while (res.next()) {
				Vector vec = new Vector();
				Integer id = (Integer) res.getInt("id");
				Integer kategorie = (Integer) res.getInt("kategorie");
				String plan_id=  (String) res.getString("plan_id");
				vec.addElement(kategorie);				
				hash.put(plan_id,vec);	
				PreparedStatement stmtdel= con.prepareStatement("delete from tmpplanningjobs where plan_id ='"
						     + plan_id + "' and kategorie = "+kategorie );
				
				System.out.println("delete from tmpplanningjobs where plan_id ='"
					     + plan_id + "' and kategorie = "+kategorie);
				stmtdel.execute();
			}
			
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		
		return hash;
	}
}
