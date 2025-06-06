package cbudgetbase;


import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.SimpleDateFormat;


@SuppressWarnings("LossyEncoding")
public class DB {
	public Connection con = null;
    public boolean debug=false;
	/**
	 * Macht den INI-Hash in der Klasse "global" und stellt die Verbindung zum
	 * Datenbank-Server her.
	 */ 

	/*public boolean dataBaseConnect() {
		if (debug) if (debug) System.out.println("Verbinde mich zur Datenbank");
		try {
			try {
				Class.forName("org.gjt.mm.mysql.Driver").newInstance(); // DB-
																		// Treiber
																		// laden
			} catch (Exception E) {
				System.err
						.println("Konnte MySQL Datenbank-Treiber nicht laden!");
				return false;
			}

			String url = "jdbc:mysql://192.168.2.8/budget_test";
			
			

			con = DriverManager.getConnection(url, "budget", "budget"); // Verbindung
		      													// herstellen
			if (debug) System.out.println("Verbindung erstellt");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Treiber fuer mySQL nicht gefunden");
			return false;
		}
		return true;
	}*/


    public boolean dataBaseConnect(String username,String password, String connectString) {
   	 if (debug) System.out.println("Verbinde mich zur Datenbank");
   		try {
   			try {
   				//Class.forName("org.gjt.mm.mysql.Driver").newInstance(); // DB-
   																		// Treiber
   																// laden
   				Class.forName("org.postgresql.Driver").newInstance();
   																		
   			} catch (Exception E) {
   				System.err
   						.println("Konnte MySQL Datenbank-Treiber nicht laden!");
   				return false;
   			}
   			//String url = "jdbc:mysql://192.168.2.8/budget_test";
   			//con = DriverManager.getConnection(connectString, username, password); // Verbindung
   			if (debug) System.out.println("Try to connect with "+connectString);											// herstellen
   			
   				 //con = DriverManager.getConnection("jdbc:postgresql://192.168.2.28:5432/budget", "budget", "budget");
   				con = DriverManager.getConnection(connectString, username, password);
   			//DriverManager.getConnection("jdbc:postgresql://localhost:5432/budget?user=budget&password=");
   				
   			if (debug) System.out.println("Verbindung erstellt");
   		} catch (Exception e) {
   			e.printStackTrace();
   			System.err.println("Treiber fuer postgres nicht gefunden");
   			return false;
   		}
   		return true;
   	}
	/**
	 * Schlie�t die Verbindung zum Server. Das Objekt ist danach unbrauchbar.
	 */

	public boolean closeConnection() {
		if (con != null) {
			try {
				con.close();
				if (debug) System.out.println("Verbindung beendet");
			} catch (Exception e) {
				System.err.println("Konnte Verbindung nicht beenden!!");
				return false;
			}
		}
		return true;
	}
	
	public Vector getAllKonto() {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,kontoname,hidden,upperlimit,lowerlimit,description,mode,rule_id from konten order by kontoname");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("kontoname"));
				hash.put("versteckt", (String) res.getString("hidden"));
				hash.put("upperlimit", new Float(res.getFloat("upperlimit")));
				hash.put("lowaerlimit", new Float(res.getFloat("lowerlimit")));
				hash.put("description", (String) res.getString("description"));
				hash.put("mode", (String) res.getString("mode"));
				hash.put("rule_id", (Integer) res.getInt("rule_id"));
				
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	public Vector getAllKonto(String mode) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,kontoname,hidden,upperlimit,lowerlimit,description,mode,rule_id from konten where mode='"+mode+"' order by kontoname");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("kontoname"));
				hash.put("versteckt", (String) res.getString("hidden"));
				hash.put("upperlimit", new Float(res.getFloat("upperlimit")));
				hash.put("lowerlimit", new Float(res.getFloat("lowerlimit")));
				hash.put("description", (String) res.getString("description"));
				hash.put("mode", (String) res.getString("mode"));
				hash.put("rule_id", (Integer) res.getInt("rule_id"));
				
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	public boolean insertKonto(String name,String beschreibung,String versteckt,String mode,Integer rule_id) {
		try {

			PreparedStatement stmt;
			// ResultSet res = null;
			//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
			String stm= "insert into konten values(default,'" 
				+ name + "','"
				+ versteckt + "',0,1000,'" + beschreibung +"','"+ mode + "',"+rule_id+")";
			stmt = con.prepareStatement(stm);
			
			 if (debug) System.out.println(stm);
			// " where datum="+jahr+monat+tag+
			// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		// if (debug) System.out.println("update-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		 return true;

	}
		
	public boolean updateKonto(Hashtable hash) {
		try {
			String id= ((Integer)hash.get("id")).toString();
			String name= (String)hash.get("name");
			String beschreibung= (String)hash.get("description");
			String versteckt= (String)hash.get("versteckt");
			String mode= (String)hash.get("mode");
			Integer rule_id= (Integer)hash.get("rule_id");
			String str= "update konten set kontoname = '"
				+ name + "',description = '"
				+beschreibung+"',hidden = '"  
				+ versteckt + "', mode = '"
				+mode +"', rule_id = "
				+rule_id +" where id='" + id+ "'";
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	
	public boolean updateWildcard(String str) {
		try {
		
		
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	
	public boolean deleteKonto(Hashtable hash) {
		try {

			PreparedStatement stmt;
			String id = ((Integer)hash.get("id")).toString();
			/*
			 * Falls das konto Eintr�ge hat, darf es nicht gel�scht werden 
			 */
			String where = " where konto_id ='" + id +"'";
			Vector vec = getAllTransaktionenWithWhere(where);
			if (vec.size() > 0 )
			{
			    System.out.println("!!Es gibt noch Transaktionen mit diesen Konto. Kann Konto nicht l�schen!!!");
			    return false;
			}
			
			
			if (debug) System.out.println("delete from konten where id=" + id);
			stmt = con.prepareStatement("delete from konten where id="+id );
			// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
			// " where datum="+jahr+monat+tag+
			// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
		return true;
	}
	
	public Hashtable getKontoID(String id) {
		Hashtable hash = new Hashtable();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,kontoname,hidden,upperlimit,lowerlimit,description,mode,rule_id from konten where id='"+id+"' order by kontoname");
			res = stmt.executeQuery();
			while (res.next()) {
				
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("kontoname"));
				hash.put("versteckt", (String) res.getString("hidden"));
				hash.put("upperlimit", new Float(res.getFloat("upperlimit")));
				hash.put("lowaerlimit", new Float(res.getFloat("lowerlimit")));
				hash.put("description", (String) res.getString("description"));
				hash.put("mode", (String) res.getString("mode"));
				hash.put("rule_id", (Integer) res.getInt("rule_id"));
				
				
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
	
	public String getKontoName (Integer id) {
		String erg = "";
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select kontoname from konten where id =" + id.toString());
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				
				
				
				erg=(String) res.getString("kontoname");
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return "";
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return erg;
	}

	public Vector getAllKategorien() {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,name,parent,description,limit_month,limit_year,mode,active,forecast,inflation from kategorien order by name");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("parent", (String) res.getString("parent"));
				hash.put("description", (String) res.getString("description"));
				hash.put("monthlimit", new Float(res.getFloat("limit_month")));
				hash.put("yearlimit", new Float(res.getFloat("limit_year")));
				hash.put("description", (String) res.getString("description"));
				hash.put("mode", (String) res.getString("mode"));
				hash.put("active", new Integer(res.getInt("active")));
				hash.put("forecast", new Integer(res.getInt("forecast")));
				hash.put("inflation", new Integer(res.getInt("inflation")));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

	public Vector getAllKategorien(String mode) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,name,parent,description,limit_month,limit_year,mode,active,forecast,inflation from kategorien where mode='"+mode+"' order by name");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("parent", (String) res.getString("parent"));
				hash.put("description", (String) res.getString("description"));
				hash.put("monthlimit", new Float(res.getFloat("limit_month")));
				hash.put("yearlimit", new Float(res.getFloat("limit_year")));
				hash.put("description", (String) res.getString("description"));
				hash.put("mode", (String) res.getString("mode"));
				hash.put("active", new Integer(res.getInt("active")));
				hash.put("forecast", new Integer(res.getInt("forecast")));
				hash.put("inflation", new Integer(res.getInt("inflation")));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

	public Vector getAllActiveKategorien() {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,name,parent,description,limit_month,limit_year,mode,active,forecast,inflation from kategorien where active=1 order by name");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("parent", (String) res.getString("parent"));
				hash.put("description", (String) res.getString("description"));
				hash.put("monthlimit", new Float(res.getFloat("limit_month")));
				hash.put("yearlimit", new Float(res.getFloat("limit_year")));
				hash.put("description", (String) res.getString("description"));
				hash.put("mode", (String) res.getString("mode"));
				hash.put("active", new Integer(res.getInt("active")));
				hash.put("forecast", new Integer(res.getInt("forecast")));
				hash.put("inflation", new Integer(res.getInt("inflation")));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

	
	public boolean insertKategorie(Hashtable hash) {
		try {

			PreparedStatement stmt;
			String stm= "insert into kategorien values(default,'" 
				+ hash.get("name") + "','"
				+ hash.get("parent") + "','"
				+ hash.get("description") + "','"
				+ hash.get("monthlimit") + "','"
				+ hash.get("yearlimit") + "','"
				+ hash.get("mode") + "','"
				+ hash.get("active") + "','"
				+ hash.get("forecast") + "','"
			    + hash.get("inflation") + "')";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	
	
	
	   private boolean updateAllParents(String parent,String id) {
	        try {         
	            String old_name = getKategorieName(new Integer(id));
	            String str= "update kategorien set " +
	                    "parent = '"+ parent + "' where parent = '"+old_name + "'";
	            if (debug) System.out.println(str);
	            PreparedStatement stmt;
	            stmt = con.prepareStatement(str);
	            stmt.executeUpdate();
	        } catch (SQLException e) {
	            System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
	            return false;
	        }
	        return true;
	    }
	
	
	public boolean updateKategorie(Hashtable hash) {
		try {
			String id= ((Integer)hash.get("id")).toString();
			String name= (String)hash.get("name");
			String beschreibung= (String)hash.get("description");
			String parent= (String)hash.get("parent");
			String monthlimit =(String)hash.get("monthlimit");
			String yearlimit =(String)hash.get("yearlimit");
			String mode =(String)hash.get("mode");
			String active =(String)hash.get("active");
			String forecast =(String)hash.get("forecast");
			String inflation =(String)hash.get("inflation");
			String str= "update kategorien set " +
					"name = '"+ name + "'," +
					"description = '"+beschreibung+"',"+
					"parent = '"+parent+"',"+
					"limit_month = '"+monthlimit+"',"+
					"mode = '"+mode+"',"+
					"active = '"+active+"',"+
					"forecast = '"+forecast+"',"+
					"inflation = '"+inflation+"',"+
					"limit_year = '"+yearlimit+"' where id = '"+id+"'";
					
			updateAllParents(name,id);
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	public boolean deleteKategorie(Hashtable hash) {
		try {

			PreparedStatement stmt;
			String id = ((Integer)hash.get("id")).toString();
			if (debug) System.out.println("delete from kategorien where id=" + id +" or parent='"+hash.get("name")+"'");
			stmt = con.prepareStatement("delete from kategorien where id="+id +" or parent='"+hash.get("name")+"'");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
		return true;
	}

	public boolean deleteKategorieRecursive(String name) {
		try {
	
			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select name from kategorien where parent='"+name+"' order by name");
			res = stmt.executeQuery();
			Vector vec = new Vector();
			while (res.next())
			{
				vec.add((String) res.getString("name"));
				if (debug) System.out.println("child gefunden");
			}
			boolean fehler=false;
			for (int i=0;i<vec.size();i++)
			{
				if( !deleteKategorieRecursive((String)vec.elementAt(i)))
				{
					fehler=true;
				}
					
			}
			if (!fehler)
			{
				//Pr�fe, ob noch Eintr�ge existieren
				String str_stm="select name from transaktionen where name='"+name+"'";
				stmt = con.prepareStatement(str_stm);
				res=stmt.executeQuery();
				if (res.next())
				{
					System.err.println("Es existieren noch Transaktionen unter der kategorie "+name);
					return false;
				}
				else
				{
			stmt = con.prepareStatement("delete from kategorien where name ='" +name+"'" );
			stmt.executeUpdate();
				}
			}
			else
			{
				System.err.println("Es existieren noch Transaktionen unter dieser kategorie");
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
	return true;		
	}
	
	public String getKategorieName (Integer id) {
		String erg = "";
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select name from kategorien where id =" + id.toString());
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				
				
				
				erg=(String) res.getString("name");
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return "";
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return erg;
	}

	public Vector getKategorienAlleAusgaben (String mode ,boolean sub) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm;
			if (sub)
			{
				//Alle kategorien
				str_stm=("select id, name from kategorien where mode = '"+mode+"'");
			}
			else
			{
				//Nur Hauptkategorien
				str_stm=("select id, name from kategorien where parent='' and mode = '"+mode+"'");
			}
			
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	
	public Vector getKategorienByName(String kategorie) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm;
				str_stm=("select id, name from kategorien where name = '"+kategorie+"'");
		
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id"))); 
				hash.put("name", (String) res.getString("name"));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

	
	public boolean getKategorienAlleRecursiv(String kategorie, Vector vec) {
			try {
				Vector allKats =new Vector();
				PreparedStatement stmt;
				ResultSet res = null;
				String str_stm="select name from kategorien where parent = '"+kategorie+"' order by name";
				if (debug) System.out.println(str_stm);
				stmt = con
						.prepareStatement(str_stm);
				res = stmt.executeQuery();
				while (res.next()) {
					
					allKats.addElement((String) res.getString("name"));
					
					
				}
				for (int i=0;i<allKats.size();i++)
				{
					getKategorienAlleRecursiv(((String)allKats.elementAt(i)),vec);
				}
				str_stm="select id,name,parent,description,limit_month,limit_year,mode,active,forecast,inflation from kategorien where name = '"+kategorie+"'";
				if (debug) System.out.println(str_stm);
				stmt = con
				.prepareStatement(str_stm);
		res = stmt.executeQuery();
		while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("parent", (String) res.getString("parent"));
				hash.put("description", (String) res.getString("description"));
				hash.put("monthlimit", new Float(res.getFloat("limit_month")));
				hash.put("yearlimit", new Float(res.getFloat("limit_year")));
				hash.put("description", (String) res.getString("description"));
				hash.put("mode", (String) res.getString("mode"));
				hash.put("active", new Integer(res.getInt("active")));
				hash.put("forecast", new Integer(res.getInt("forecast")));
				hash.put("inflation", new Integer(res.getInt("inflation")));
				vec.addElement(hash);
		}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return false;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return true;
		}
		
	public double getKategorienAlleRecursivSumme(String kategorie, String startdatum, String enddatum,String rule) {
			double sum=0.0;
			try {
				Vector allKats =new Vector();
				PreparedStatement stmt;
				ResultSet res = null;
				String str_stm="select name from kategorien where parent = '"+kategorie+"' order by name";
				if (debug) System.out.println(str_stm);
				stmt = con
						.prepareStatement(str_stm);
				res = stmt.executeQuery();
				while (res.next()) {
					
					allKats.addElement((String) res.getString("name"));
					
					
				}
				
				sum=0.0;
				for (int i=0;i<allKats.size();i++)
				{
					sum=sum+getKategorienAlleRecursivSumme(((String)allKats.elementAt(i)),startdatum,enddatum,rule);
				}
				str_stm="select sum(wert) as summe from transaktionen where kategorie = (select id from kategorien where name ='"+kategorie+"') and datum >=   '"+startdatum+"' and datum <=  '"+enddatum+"' "+rule;
				
				if (debug) System.out.println(str_stm);
				stmt = con
				.prepareStatement(str_stm);
		res = stmt.executeQuery();
		while (res.next()) {
				sum=sum +(res.getDouble("summe"));
		}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return sum;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return sum;
		}
	
	public double getKategorienAlleRecursivSumme(String kategorie, String startdatum, String enddatum,String rule, int plan_id) {
		double sum=0.0;
		try {
			Vector allKats =new Vector();
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select name from kategorien where parent = '"+kategorie+"' order by name";
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			
			while (res.next()) {
				
				allKats.addElement((String) res.getString("name"));
				
				
			}
			if (planwertGleichNull(plan_id, kategorie ))
			{
				return 0.0;
			}
			sum=0.0;
			for (int i=0;i<allKats.size();i++)
			{
				sum=sum+getKategorienAlleRecursivSumme(((String)allKats.elementAt(i)),startdatum,enddatum,rule,plan_id);
			}
			str_stm="select sum(wert) as summe from transaktionen where kategorie = (select id from kategorien where name ='"+kategorie+"') and datum >=   '"+startdatum+"' and datum <=  '"+enddatum+"' "+rule;
			
			if (debug) System.out.println(str_stm);
			stmt = con
			.prepareStatement(str_stm);
	res = stmt.executeQuery();
	while (res.next()) {
			sum=sum +(res.getDouble("summe"));
	}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return sum;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return sum;
	}
			
	public double getKategorienAlleSummeWhere(String startdatum, String enddatum,String where) {
		double sum=0.0;
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select sum(wert) as summe from transaktionen where datum >=  '"+startdatum+"'"+" and datum <=  '"+enddatum+"'"+" and "+where;
			if (debug)System.out.println(str_stm);
			stmt = con
			.prepareStatement(str_stm);
	       res = stmt.executeQuery();
	   	while (res.next()) {
			sum=(res.getDouble("summe"));
	   	}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return sum;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return sum;
	}

	public double getallErtragTransaktionen(Integer konto,  String startdatum, String enddatum) {
		double sum=0.0;
		try {debug=true;
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select sum(wert) as summe from transaktionen where konto_id = " + konto +" and datum >=  '"+startdatum+"'"+" and datum <=  '"+enddatum +"' and name = 'Ertrag' ";
			if (debug)System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				sum=(res.getDouble("summe"));
			}
			debug=false;
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return sum;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return sum;
	}
	public double getallWithoutErtragTransaktionen(Integer konto,  String startdatum, String enddatum) {
		double sum=0.0;
		try {
			debug=true;
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select sum(wert) as summe from transaktionen where konto_id = " + konto +" and datum >=  '"+startdatum+"'"+" and datum <=  '"+enddatum +"' and name != 'Ertrag' ";
			if (debug)System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				sum=(res.getDouble("summe"));
			}
			debug = false;
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return sum;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return sum;
	}



	public Map <Integer,Double> getKategorienAlleSummeWhereAsMapPerDay(String startdatum, String enddatum,String where) {
		double sum=0.0;
		Map <Integer,Double >map = new HashMap<Integer, Double>();
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			//String str_stm="select sum(wert) as summe from transaktionen where datum >=  '"+startdatum+"'"+" and datum <=  '"+enddatum+"'"+" and "+where;
			String str_stm = "select sum (wert) as summe ,date_trunc('day', transaktionen.datum) as d from transaktionen where datum >=  '"+startdatum+"' and datum <=  '"+enddatum+"'"+" and "+ where + " group by date_trunc('day', transaktionen.datum)";
			if (debug)System.out.println(str_stm);
			stmt = con
			.prepareStatement(str_stm);
	       res = stmt.executeQuery();
	   	while (res.next()) {
			sum=(res.getDouble("summe"));
			Date day = res.getDate("d");
			Calendar cal = Calendar.getInstance();
			cal.setTime(day);
			int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);  
			//System.out.println(day);
			//System.out.println("DayOfYear= "+dayOfYear);
			map.put(dayOfYear, sum);
	   	}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return map;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return map;
	}
	
	   public Vector getKategorienAlleSummeWhereAsVectorPerDay(String startdatum, String enddatum,String where) {
	        double sum=0.0;
	     Vector vec = new Vector();
	        try {
	            PreparedStatement stmt;
	            ResultSet res = null;
	         
	            //String str_stm="select sum(wert) as summe from transaktionen where datum >=  '"+startdatum+"'"+" and datum <=  '"+enddatum+"'"+" and "+where;
	            String str_stm = "select sum (wert) as summe ,date_trunc('day', transaktionen.datum) as d from transaktionen where datum >=  '"+startdatum+"' and datum <=  '"+enddatum+"'"+" and "+ where + " group by date_trunc('day', transaktionen.datum)";
	            if (debug)System.out.println(str_stm);
	            stmt = con
	            .prepareStatement(str_stm);
	          
	           res = stmt.executeQuery();
	        while (res.next()) {
	            sum=(res.getDouble("summe"));
	             vec.addElement(sum);
	        }
	        } catch (SQLException e) {
	            System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
	            return vec;
	        }
	        if (debug) System.out.println("Select-Anweisung ausgeführt");
	        // return summe/(float)getAnz(tag,monat,year);
	        return vec;
	    }
	
	public double getKategorienSummeKontoart(String mode ,String startdatum, String rule) {
		double sum=0.0;
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select sum(trans.wert) as summe from transaktionen trans  "
					+ "join konten kont on kont.id = trans.konto_id and "
					+ "kont.mode = '"+mode+"' where trans.datum <= '"+convDatum(startdatum)+"' " + rule ;
			if (debug)System.out.println(str_stm);
			stmt = con
			.prepareStatement(str_stm);
	       res = stmt.executeQuery();
	   	while (res.next()) {
			sum=(res.getDouble("summe"));
	   	}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return sum;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return sum;
	}
	
	public Vector getAllTransaktionen1 (String konto) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			if (debug) System.out.println("select id, name, konto_id, wert, datum, partner, beschreibung, kategorie, kor_id, cycle,planed from transaktionen where konto_id=(select id from konten where kontoname ='"+konto+"') order by datum");
			stmt = con
					.prepareStatement("select id, name, konto_id, wert, datum, beschreibung, partner, kategorie, kor_id, cycle,planed  from transaktionen where konto_id=(select id from konten where kontoname ='"+konto+"') order by datum");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("konto", new Integer (res.getInt("konto_id")));
				hash.put("wert", new Double(res.getDouble("wert")));
				hash.put("datum", (Date)( res.getDate("datum")));				
				hash.put("partner", (String) res.getString("partner"));	
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("kategorie", new Integer (res.getInt("kategorie")));
				hash.put("kor_id", new Integer (res.getInt("kor_id")));
				hash.put("cycle", new Integer (res.getInt("cycle")));
				hash.put("planed", (String) res.getString("planed"));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	
	public Vector getAllTransaktionenWithWhere (String where) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select id, name, konto_id, wert, datum,partner,beschreibung, kategorie, kor_id, cycle,planed from transaktionen " +where);
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("konto", new Integer (res.getInt("konto_id")));
				hash.put("wert", new Double(res.getDouble("wert")));
				hash.put("datum", (Date)( res.getDate("datum")));
				hash.put("partner", (String) res.getString("partner"));	
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("kategorie", new Integer (res.getInt("kategorie")));
				hash.put("kor_id", new Integer (res.getInt("kor_id")));
				hash.put("cycle", new Integer (res.getInt("cycle")));
				hash.put("planed", (String) res.getString("planed"));
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	
	
	public Vector getAllTransaktionenWithKontoArt (String startdate, String enddate, String rule ) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			
			String str_stm=("select trans.datum as datum, trans.wert as wert, (select mode from konten where id=trans.konto_id) AS mode from transaktionen trans where datum > '"+convDatum(startdate)+"' and datum <= '"+convDatum(enddate)+"'"+ rule +"  order by datum ");
			debug=true;
			if (debug) System.out.println(str_stm);
			debug=false;
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();

				Double wert = res.getDouble("wert");
				Date datum = res.getDate("datum");
				String mode = res.getString("mode");
				if (wert == null || datum == null || mode == null )
				{
					System.out.println("!!!Datum = " + datum.toString()+ " wert = "+wert+" mode = "+mode);
				}
				hash.put("datum", (Date)( res.getDate("datum")));
				hash.put("wert", new Double(res.getDouble("wert")));
				hash.put("mode", (String) res.getString("mode"));	
				vec.add(hash);
			}
			
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	
	public boolean insertTransaktion(Hashtable hash,int kor_id) {
		try {
			String konto_id=getKontoId((String)hash.get("konto")).toString();
			//String kor_id=getKontoId((String)hash.get("kor_konto")).toString();
			String kat_id=getKategorieId((String)hash.get("kategorie")).toString();
			PreparedStatement stmt;
			String stm= "insert into transaktionen values(default,'" 
				+ hash.get("name") + "',"
				+ konto_id + ","
				+ hash.get("wert")  + ",'"
				+ hash.get("datum") + "','"				
				+ hash.get("partner") + "','"
				+ hash.get("beschreibung") + "',"
				+ kat_id + ","
				+ kor_id + ","
			    + hash.get("cycle") +",'" 
			    + hash.get("planed")+"')";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
			insert_history((String)hash.get("user"),"0","where name='"+hash.get("name")+"' and" +
					 " konto_id=" + konto_id + " and" +
					 " wert=" + hash.get("wert") + " and" +
					 " datum='" + ((String)hash.get("datum")) + "' and" +
					 " beschreibung='" + hash.get("beschreibung") + "' and" +
					 " kategorie = "+ kat_id + " and" +
					 " partner='" + hash.get("partner") + "'");
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	public boolean insertTransaktionZycl(Hashtable hash) {
		try {
			
			PreparedStatement stmt;
			String stm= "insert into transaktionen values(default,'" 
				+ hash.get("name") + "',"
				+ hash.get("konto") + ","
				+ hash.get("wert")  + ",'"
				+ hash.get("datum") + "','"				
				+ hash.get("partner") + "','"
				+ hash.get("beschreibung") + "',"
				+ hash.get("kategorie") + ","
				+ hash.get("kor_id") + ","
				+ hash.get("cycle") +",'" 
				+ hash.get("planed")+"')";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
			insert_history((String)hash.get("user"),"0","where name='"+hash.get("name")+"' and" +
										 " konto_id=" + hash.get("konto") + " and" +
										 " wert=" + hash.get("wert") + " and" +
										 " datum='" + ((String)hash.get("datum")) + "' and" +
										 " beschreibung='" + hash.get("beschreibung") + "' and" +
										 " kategorie='" + hash.get("kategorie") + "' and" +
										 " partner='" + hash.get("partner") + "'");
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	public boolean insertTransaktionCycle(Hashtable hash,int kor_id) {
		try {
			
			PreparedStatement stmt;
			String stm= "insert into transaktionen values(default,'" 
				+ hash.get("name") + "',"
				+ hash.get("konto") + ","
				+ hash.get("wert")  + ",'"
				+ hash.get("datum") + "','"			
				+ hash.get("partner") + "','"
				+ hash.get("beschreibung") + "',"
				+ hash.get("kategorie")+ ","
				+ kor_id + ","
				+ hash.get("cycle") +",'" 
				+ hash.get("planed")+"')";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
			insert_history((String)hash.get("user"),"0","where name='"+hash.get("name")+"' and" +
					 " konto_id=" + hash.get("konto") + " and" +
					 " wert=" + hash.get("wert") + " and" +
					 " datum='" + ((String)hash.get("datum")) + "' and" +
					 " beschreibung='" + hash.get("beschreibung") + "' and" +
					 " kategorie='" + hash.get("kategorie") + "' and" +
					 " partner='" + hash.get("partner") + "'");
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	
	public boolean insertCycleTransaktion(Hashtable hash,String update) {
		try {
			//Todo		
			PreparedStatement stmt;
			String stm="";
			if (update.equals("0"))
			{
			stm= "insert into reccuring values(default," 
				+ hash.get("korid") + ",'"
				+ hash.get("end_datum") + "','"
				+ hash.get("wiederholung") + "','"
				+ hash.get("name") + "','"
				+ hash.get("noend") +  "',"
				+ hash.get("delta")  +")";	
			}
			else
			{
				stm= "update reccuring set " +
						"enddatum= '" + (String)hash.get("end_datum") +"'"+
						",type = '"+ hash.get("wiederholung") + 
						"',name = '"+ hash.get("name") +
						"',noend = '"+ hash.get("noend") +
						"',delta = "+ hash.get("delta") +
						" where kor_id = "+ hash.get("korid");
			}
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	
	public Hashtable getCycleTransaktionen (Integer kor_id) {
		Hashtable hash;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String stm_str="select id,kor_id,enddatum,type,name, noend,delta  from reccuring where kor_id = "+kor_id ;
			if (debug) System.out.println(stm_str);
			stmt = con
					.prepareStatement(stm_str);
			res = stmt.executeQuery();
			while (res.next()) {
				hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("korid", new Integer (res.getInt("kor_id")));
				hash.put("end_datum", (Date)( res.getDate("enddatum")));
				hash.put("wiederholung", (String) res.getString("type"));
				hash.put("name", (String) res.getString("name"));
				hash.put("noend", (String) res.getString("noend"));
				hash.put("delta", new Integer (res.getInt("delta")));
				return hash;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return null;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return null;
	}
	
	public Hashtable getLastCycleTransaktion (String datum, Integer kor_id) {
		Hashtable hash = new Hashtable();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select id, name, konto_id, wert, datum, partner, beschreibung, kategorie, kor_id, cycle,planed from transaktionen  where kor_id = "+kor_id+" and datum <= '"+convDatum(datum)+"' order by datum desc limit 1");
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("konto", new Integer (res.getInt("konto_id")));
				hash.put("wert", new Double(res.getDouble("wert")));
				hash.put("datum", (Date)( res.getDate("datum")));				
				hash.put("partner", (String) res.getString("partner"));	
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("kategorie", new Integer (res.getInt("kategorie")));
				hash.put("kor_id", new Integer (res.getInt("kor_id")));
				hash.put("cycle", new Integer (res.getInt("cycle")));
				hash.put("planed", (String) res.getString("planed"));
				
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
	
	public Hashtable getFirstCycleTransaktion (Integer kor_id) {
		Hashtable hash = new Hashtable();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select id, name, konto_id, wert, datum, partner,beschreibung, kategorie, kor_id, cycle,planed from transaktionen  where kor_id = "+kor_id+" order by datum limit 1");
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("konto", new Integer (res.getInt("konto_id")));
				hash.put("wert", new Double(res.getDouble("wert")));
				hash.put("datum", (Date)( res.getDate("datum")));				
				hash.put("partner", (String) res.getString("partner"));	
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("kategorie", new Integer (res.getInt("kategorie")));
				hash.put("kor_id", new Integer (res.getInt("kor_id")));
				hash.put("cycle", new Integer (res.getInt("cycle")));
				hash.put("planed", (String) res.getString("planed"));
				
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
	
	public int getAnzCycleEintrag (String kor_id,String datum,boolean schonEingetragen)
	{
		int erg=0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String stm_str ="";
			if (schonEingetragen)
			{
			stm_str="select count(*) as anz from transaktionen  where kor_id = "+kor_id+" and datum <= '"+convDatum(datum)+"'";
			}
			else
			{
				stm_str="select count(*) as anz from transaktionen  where kor_id = "+kor_id+" and datum > '"+convDatum(datum)+"'";
				}	
			if (debug) System.out.println(stm_str);
			stmt = con
					.prepareStatement(stm_str);
			res = stmt.executeQuery();
			while (res.next()) {
				erg= new Integer (res.getInt("anz")).intValue();
				
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return erg;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return erg;
	}
	
	public Vector getAllCycleTransaktionen () {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String stm_str="select id,kor_id,enddatum,type,name, noend,delta  from reccuring";
			if (debug) System.out.println(stm_str);
			stmt = con
					.prepareStatement(stm_str);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("korid", new Integer (res.getInt("kor_id")));
				hash.put("end_datum", (Date)( res.getDate("enddatum")));
				hash.put("wiederholung", (String) res.getString("type"));
				hash.put("name", (String) res.getString("name"));
				hash.put("noend", (String) res.getString("noend"));
				hash.put("delta", new Integer (res.getInt("delta")));
				vec.addElement(hash); 
				
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	
	
	
	public boolean setNewEnddate (Integer kor_id, String datum)
	{
		try { String str= "update reccuring  set " +
			 "enddatum = '"+datum +"' where kor_id = "+kor_id;
		if (debug) System.out.println(str);
		PreparedStatement stmt;
		stmt = con.prepareStatement(str);
		stmt.executeUpdate();
	} catch (SQLException e) {
		System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
	    return false;
	}
	return true;
}
	
	public boolean setNoendNo (Integer kor_id)
	{
		try { String str= "update reccuring  set " +
			 "noend = 'nein' where kor_id = "+kor_id;
		if (debug) System.out.println(str);
		PreparedStatement stmt;
		stmt = con.prepareStatement(str);
		stmt.executeUpdate();
	} catch (SQLException e) {
		System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
	    return false;
	}
	return true;
}
	
	public boolean updateTransaktion(Hashtable hash) {
		try {
			Integer konto_id =getKontoId((String)hash.get("konto"));
			Integer kategorie =getKategorieId((String)hash.get("kategorie"));
			
			String str= "update transaktionen set " +
					"name = '"+ hash.get("name") + "'," +
					"konto_id = "+konto_id +"," +
					"wert = "+ hash.get("wert")+"," +
					"datum = '"+ hash.get("datum")+"'," +
					"beschreibung = '"+ hash.get("beschreibung")+"'," +
					"partner = '"+ hash.get("partner")+"'," +
					"kategorie = "+kategorie +"," +
					"kor_id = "+ hash.get("kor_id")+"," +
					"cycle = "+ hash.get("cycle")+"," +
					"planed = '"+ hash.get("planed")+
					"' where id = " + hash.get("id");
				
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
			insert_history((String)hash.get("user"),"1","where id = " + hash.get("id"));
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	
	public boolean updateTransaktionSetKorId0(Hashtable hash) {
		try {
			Integer konto_id =getKontoId((String)hash.get("konto"));
			Integer kategorie =getKategorieId((String)hash.get("kategorie"));
			
			String str= "update transaktionen set " +
					"kor_id=0 "+
					" where id = " + hash.get("id");
				
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
			insert_history((String)hash.get("user"),"1","where id = " + hash.get("id"));
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	
		public boolean getCycleTransaktion (Hashtable trans) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select id from transaktionen where " +
					" name = '"+ trans.get("name")+"' and " +
					" konto_id = "+ trans.get("konto") + " and "+
					" datum = '" + convDatum(((String)(trans.get("datum")))) + "' and "+
					" kategorie = "+ trans.get("kategorie")+ " and "+
					" kor_id =  "+ trans.get("kor_id");
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			if (res.next()) {
				return true;
			}
			else
			{
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return false;
		}
		// return summe/(float)getAnz(tag,monat,year);
	}
	
	
	public boolean updateTransaktionWithKorAndKontoid (Hashtable hash) {
		try {
			Integer konto_id =getKontoId((String)hash.get("konto"));
			Integer neue_konto_id =getKontoId((String)hash.get("neues_konto"));
			Integer kategorie =getKategorieId((String)hash.get("kategorie"));
			String str="";
			if (((Integer)hash.get("cycle")).intValue()==0)
			{
			        str= "update transaktionen set " +
					"name = '"+ hash.get("name") + "'," +
					"konto_id = "+ neue_konto_id+ "," +
					"wert = "+ hash.get("wert")+"," +
					"datum = '"+ hash.get("datum")+"'," +
					"beschreibung = '"+ hash.get("beschreibung")+"'," +
					"partner = '"+ hash.get("partner")+"'," +
					"kategorie = "+kategorie +"," +
					"cycle = "+ hash.get("cycle")+"," +
					"planed = '"+ hash.get("planed")+
					"' where kor_id = " + hash.get("kor_id")+ " and konto_id="+ konto_id ;
			}
			else
			{
				  str= "update transaktionen set " +
					"name = '"+ hash.get("name") + "'," +
					"konto_id = "+ neue_konto_id+ "," +
					"wert = "+ hash.get("wert")+"," +
					"beschreibung = '"+ hash.get("beschreibung")+"'," +
					"partner = '"+ hash.get("partner")+"'," +
					"kategorie = "+kategorie +"," +
					"cycle = "+ hash.get("cycle")+"," +
					"planed = '"+ hash.get("planed")+
					"' where kor_id = " + hash.get("kor_id")+ " and konto_id="+ konto_id ;
			}
				
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
			insert_history((String)hash.get("user"),"1","where kor_id = " + hash.get("kor_id")+ " and konto_id="+ konto_id);
			
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	
	public boolean updateTransaktionWithKorAndKontoidDatum (Hashtable hash, String datum,int groesser) {
		try {
			Integer konto_id =getKontoId((String)hash.get("konto"));
			Integer neue_konto_id =getKontoId((String)hash.get("neues_konto"));
			Integer kategorie =getKategorieId((String)hash.get("kategorie"));
			String str="";
			String whereHist="";
			if (groesser==1)//folgend vorher true
			{
			str= "update transaktionen set " +
					"name = '"+ hash.get("name") + "'," +
					"konto_id = "+ neue_konto_id+ "," +
					"wert = "+ hash.get("wert")+"," +
					"beschreibung = '"+ hash.get("beschreibung")+"'," +
					"partner = '"+ hash.get("partner")+"'," +
					"kategorie = "+kategorie +"," +
					"kor_id = "+ hash.get("neue_kor_id") +"," +
					"cycle = "+ hash.get("cycle")+"," +
					"planed = '"+ hash.get("planed")+ 
					"' where kor_id = " + hash.get("kor_id")+ " and konto_id="+ konto_id +" and datum >= '"+convDatum(datum)+"'";
			whereHist="where kor_id = " + hash.get("neue_kor_id")+ " and konto_id="+ konto_id +" and datum >='"+convDatum(datum) +"'";
			}
			if (groesser==0)//einzeln
			{
				str= "update transaktionen set " +
				"name = '"+ hash.get("name") + "'," +
				"konto_id = "+ neue_konto_id+ "," +
				"wert = "+ hash.get("wert")+"," +
				"beschreibung = '"+ hash.get("beschreibung")+"'," +
				"partner = '"+ hash.get("partner")+"'," +
				"kategorie = "+kategorie +"," +
				"kor_id = "+ hash.get("neue_kor_id") +"," +
				"cycle = "+ hash.get("cycle")+"," +
				"planed = '"+ hash.get("planed")+
				"' where kor_id = " + hash.get("kor_id")+ " and konto_id="+ konto_id +" and datum = '"+convDatum(datum)+"'";
				whereHist="where kor_id = " + hash.get("neue_kor_id")+ " and konto_id="+ konto_id +" and datum ='"+convDatum(datum)+"'";
			}
			if (groesser==2)//zuvor vorher false
			{
				str= "update transaktionen set " +
				"name = '"+ hash.get("name") + "'," +
				"konto_id = "+ neue_konto_id+ "," +
				"wert = "+ hash.get("wert")+"," +
				"beschreibung = '"+ hash.get("beschreibung")+"'," +
				"partner = '"+ hash.get("partner")+"'," +
				"kategorie = "+kategorie +"," +
				"kor_id = "+ hash.get("neue_kor_id") +"," +
				"cycle = "+ hash.get("cycle")+"," +
				"planed = '"+ hash.get("planed")+
				"' where kor_id = " + hash.get("kor_id")+ " and konto_id="+ konto_id +" and datum <= '"+convDatum(datum)+"'";
				whereHist="where kor_id = " + hash.get("neue_kor_id")+ " and konto_id="+ konto_id +" and datum <= '"+convDatum(datum)+"'";
			}	
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
			insert_history((String)hash.get("user"),"1",whereHist);
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	
	public boolean deleteTransaktion(Integer id,String anwenden,String datum) {
		try {

			PreparedStatement stmt;
			// ResultSet res = null;
			//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
			String stm_str="";
			if (anwenden.equals("alle"))
			{
				stm_str="delete from transaktionen where id=" + id;
			}
			if (anwenden.equals("folgend"))
			{
				stm_str="delete from transaktionen where id=" + id +" and datum >= '"+datum+"'";
			}
			if (anwenden.equals("zuvor"))
			{
				stm_str="delete from transaktionen where id=" + id +" and datum <='"+datum+"'";
			}
			if (debug) System.out.println(stm_str);
			stmt = con.prepareStatement(stm_str);
			// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
			// " where datum="+jahr+monat+tag+
			// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
		return true;
	}
	
	public boolean deleteTransaktion(Integer id) {
		try {

			PreparedStatement stmt;
			// ResultSet res = null;
			//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
			String stm_str="";
			stm_str="delete from transaktionen where id=" + id;
			if (debug) System.out.println(stm_str);
			stmt = con.prepareStatement(stm_str);
			// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
			// " where datum="+jahr+monat+tag+
			// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
		return true;
	}
	
	
	 
	 public boolean deleteTransaktionWithWhere(String where) {
			try {

				PreparedStatement stmt;
				// ResultSet res = null;
				//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
				String stm_str="";
				stm_str="delete from transaktionen where" + where ;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
				// " where datum="+jahr+monat+tag+
				// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
				 return false;
			}
			return true;
		}
	
	public boolean deleteTransaktionExcept(Integer kor_id,String datum) {
		try {

			PreparedStatement stmt;
			// ResultSet res = null;
			//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
			String stm_str="";
			stm_str="delete from transaktionen where kor_id = " + kor_id +" AND datum != '"+datum+"'";
			if (debug) System.out.println(stm_str);
			stmt = con.prepareStatement(stm_str);
			// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
			// " where datum="+jahr+monat+tag+
			// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
		return true;
	}
	
	public boolean deleteTransaktion_kor_id(Integer kor_id , String anwenden, String datum) {
		try {

			PreparedStatement stmt;
			// ResultSet res = null;
			//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
			String stm_str="";
			if (anwenden.equals("alle"))
			{
				stm_str="delete from transaktionen where kor_id=" + kor_id;
			}
			if (anwenden.equals("folgend"))
			{
				stm_str="delete from transaktionen where kor_id=" + kor_id +" and datum >= '"+datum+"'";
			}
			if (anwenden.equals("zuvor"))
			{
				stm_str="delete from transaktionen where kor_id=" + kor_id +" and datum <= '"+datum+"'";
			}
			if (anwenden.equals("einzeln"))
			{
				stm_str="delete from transaktionen where kor_id=" + kor_id +" and datum = '"+datum+"'";
			}
			if (debug) System.out.println(stm_str);
			stmt = con.prepareStatement(stm_str);
			// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
			// " where datum="+jahr+monat+tag+
			// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
			stmt.executeUpdate();
			if (anwenden.equals("alle"))
			{
				stm_str="delete from reccuring where kor_id=" + kor_id;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				stmt.executeUpdate();
			}
			if (anwenden.equals("folgend"))
			{
				setNoendNo(kor_id);
				setNewEnddate(kor_id,datum);
			}
			
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
		return true;
	}
	
	public Double getAktuellerKontostand (String konto,String datum,String where) {
		Double summe=0.0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="";
			String single_konto="";
			if (debug) System.out.println(konto);
			if (! konto.equals("alleKonten"))
			{
				single_konto="konto_id=(select id from konten where kontoname='"+konto+"')and ";
			}
			
			str_stm=("select sum(wert) as summe from transaktionen where "+single_konto+" datum <= '"+convDatum(datum)+"' "+where);
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				summe= new Double(res.getDouble("summe"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return 0.0;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return summe;
	}
	
	
	
	public Integer getKontoId (String konto) {
		Integer id=0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			
			if (debug) System.out.println("select id from konten where kontoname='"+konto+"'");
			stmt = con
					.prepareStatement("select id from konten where kontoname='"+konto+"'");
			res = stmt.executeQuery();
			while (res.next()) {
				id= new Integer(res.getInt("id"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return 0;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return id;
	}
	public Integer getKorId (String transid) {
		Integer id=0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select kor_id from transaktionen where id="+transid);
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				id= new Integer(res.getInt("kor_id"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return 0;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return id;
	}
	
	public String getKorKonto (String konto,Integer kor_id) {
		   //liefert das korespondierende Konto mit gleicher korid
		String neueskonto;
		try {
			Integer id=0;
			Integer kontoId=getKontoId(konto);
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select konto_id from transaktionen where kor_id="+kor_id+" and konto_id != "+kontoId);
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				id= new Integer(res.getInt("konto_id"));
			}
			neueskonto=getKontoName(id);
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return "";
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return neueskonto;
	}
	
	public Hashtable getKorKontoId (String kontoid ,String kor_id) {
		Hashtable hash = new Hashtable();
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select id, name, konto_id, wert, datum, beschreibung, partner, kategorie, kor_id, cycle,planed from transaktionen where konto_id !=" +kontoid+ " and kor_id = "+kor_id);
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				
				
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("konto", new Integer (res.getInt("konto_id")));
				hash.put("wert", new Double(res.getDouble("wert")));
				hash.put("datum", (Date)( res.getDate("datum")));
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("partner", (String) res.getString("partner"));	
				hash.put("kategorie", new Integer (res.getInt("kategorie")));
				hash.put("kor_id", new Integer (res.getInt("kor_id")));
				hash.put("cycle", new Integer (res.getInt("cycle")));
				hash.put("planed", (String) res.getString("planed"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return null;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
	
	public Integer getHighestId (String tablename,String columname) {
		Integer id=0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select max("+columname+")as maxvalue from "+ tablename;
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				id= new Integer(res.getInt("maxvalue"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return 0;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		if (debug) System.out.println("DB Highest ID="+id );
		// return summe/(float)getAnz(tag,monat,year);
		return id;
	}
	public boolean setKorId(String kor_Id ,String id) {
		try {
			
			String str= "update transaktionen set kor_id = '"
				+ kor_Id + "'where id=" + id;
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	public boolean setCycle(String id ,String cycle) {
		try {
			
			String str= "update transaktionen set cycle = "
				+ cycle + " where id=" + id;
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	public Integer getKategorieId (String kategorie) {
		Integer id=0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			
			if (debug) System.out.println("select id from kategorien where name='"+kategorie+"'");
			stmt = con
					.prepareStatement("select id from kategorien where name='"+kategorie+"'");
			res = stmt.executeQuery();
			while (res.next()) {
				id= new Integer(res.getInt("id"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return 0;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return id;
	}

	public Vector getKategorieGruppen(String startdatum, String enddatum, String wherestring) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select kategorie,sum(wert) as summe from transaktionen where datum >= '"+startdatum+"' and datum <= '"+enddatum+"' "+wherestring+"  group by kategorie";
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("kategorie", new Integer(res.getInt("kategorie")));
				hash.put("wert", new Double(res.getDouble("summe")));
				
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

	public Vector getAllPlanungen() {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,name,beschreibung,startdate,enddate,plan_id,batch,rule_id,prio from planung order by name DESC");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("startdatum", (Date)( res.getDate("startdate")));
				hash.put("enddatum", (Date)( res.getDate("enddate")));
				hash.put("plan_id", new Integer(res.getInt("plan_id")));
				hash.put("batch", (String) res.getString("batch"));
				hash.put("rule_id", new Integer(res.getInt("rule_id")));
				hash.put("prio", new Integer(res.getInt("prio")));
				
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}
	
	public Hashtable getPlanungen(String plan_id) {
		Hashtable hash = new Hashtable();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id,name,beschreibung,startdate,enddate,plan_id,batch,rule_id,prio from planung where plan_id="+plan_id+ "order by name DESC");
			res = stmt.executeQuery();
			while (res.next()) {
				
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("startdatum", (Date)( res.getDate("startdate")));
				hash.put("enddatum", (Date)( res.getDate("enddate")));
				hash.put("plan_id", new Integer(res.getInt("plan_id")));
				hash.put("batch", (String) res.getString("batch"));
				hash.put("rule_id", new Integer(res.getInt("rule_id")));
				hash.put("prio", new Integer(res.getInt("prio")));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}

	public int getPlanungPrioWithplanId (String planid) {
		int prio=0;
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm=("select prio from planung where plan_id =" +planid);
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				prio=  new Integer (res.getInt("prio"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return 0;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return prio;
	}

	public boolean insertPlanung(Hashtable hash) {
		try {

			PreparedStatement stmt;
			String stm= "insert into planung values(default,'" 
				+ hash.get("name") + "','"
				+ hash.get("beschreibung") + "','"
				+ hash.get("startdatum") + "','"
				+ hash.get("enddatum") + "',"
			    + hash.get("plan_id") +",'"
			    + hash.get("batch") +"',"
					+ hash.get("rule_id") +","
			    + hash.get("prio") + ")";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	
	public boolean deletePlanung(Hashtable hash) {
		try {

			PreparedStatement stmt;
			String id = ((Integer)hash.get("id")).toString();
			String plan_id=((Integer)hash.get("plan_id")).toString();
			String str_stm="delete from planung where id=" + id;
			if (debug) System.out.println(str_stm);
			stmt = con.prepareStatement(str_stm);
			stmt.executeUpdate();
			str_stm="delete from planung_daten where plan_id=" + plan_id;
			stmt = con.prepareStatement(str_stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
			 return false;
		}
		return true;
	}
	public boolean updatePlanung(Hashtable hash) {
		try {
			String id= ((Integer)hash.get("id")).toString();
			String name= (String)hash.get("name");
			String beschreibung= (String)hash.get("beschreibung");
			String startdate= (String)hash.get("startdatum");
			String enddate =(String)hash.get("enddatum");
			Integer plan_id =(Integer)hash.get("plan_id");
			String batch= (String)hash.get("batch");
			Integer rule_id =(Integer)hash.get("rule_id");
			Integer prio =(Integer)hash.get("prio");
			
			String str= "update planung set " +
					"name = '"+ name + "'," +
					"beschreibung = '"+beschreibung+"',"+
					"startdate = '"+startdate+"',"+
					"enddate = '"+enddate+"',"+
					"plan_id = "+plan_id+ "," +
					"rule_id = "+rule_id+ "," +
					"prio = "+prio+ "," +
					"batch = '"+batch+"'   where id = '"+id+"'";
					
				
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	public boolean checkPlanningJob(String plan_id,Integer kategorie)
	{
	try {

		PreparedStatement stmt;
		ResultSet res = null;
		String str_stm="select id from tmpplanningjobs where plan_id='"+plan_id+"' and kategorie = "+kategorie;
		//System.out.println(str_stm);
		stmt = con.prepareStatement(str_stm);
		res = stmt.executeQuery();
		if (res.next()) 
		{
			return true;
		}
	} catch (SQLException e) {
		System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
		return false;
	}
	//System.out.println("Select-Anweisung ausgeführt");
	// return summe/(float)getAnz(tag,monat,year);
	return false;
}
	public boolean insertJobs(String plan_id, Integer kategorie, Integer prio) {
		try {

			PreparedStatement stmt;
			if (checkPlanningJob(plan_id,kategorie))
			{
				//System.out.println("Job ist schon eingetragen");
				return true;
			}
			String stm= "insert into tmpplanningjobs values(default,'" 
				+ plan_id + "',"
					+kategorie+","
			 + prio + ")";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	
	public boolean insertPlanung_daten(Integer plan_id,Integer kategorie,String wert,String absolute) {
		try {

			PreparedStatement stmt;
			String stm= "insert into planung_daten values(default," 
				+ plan_id+ ","
				+ kategorie+ ","
				+ wert+ ",'"
		       + absolute + "')";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
			//Der Plan muss dann auch neu berechnet werden
			int prio = getPlanungPrioWithplanId(plan_id.toString());
			insertJobs(plan_id.toString(),kategorie,prio);
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	
	public boolean updatePlanung_daten(Integer plan_id,Integer kategorie,String wert,String absolut) {
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String stm= "select wert from planung_daten  where plan_id="+plan_id +" and kategorie="+kategorie; 
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			res=stmt.executeQuery();
			
			if (!res.next())
			{
				insertPlanung_daten(plan_id,kategorie,wert,absolut);
			}
			else
			{
			stm= "update planung_daten set wert="+wert+ ",absolut='"+absolut+"' where plan_id="+plan_id +" and kategorie="+kategorie; 
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
			}
			int prio = getPlanungPrioWithplanId(plan_id.toString());
			insertJobs(plan_id.toString(),kategorie,prio);
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}
	public Vector getAllPlanungsDaten(Integer plan_id) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select id,kategorie,wert,absolut from planung_daten where plan_id="+plan_id;
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("kategorie", new Integer(res.getInt("kategorie")));
				hash.put("wert", new Float(res.getFloat("wert")));
				hash.put("absolut", (String) res.getString("absolut"));				
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

	public Double getPlanung_daten_wert(Integer plan_id,Integer kategorie) {
		Double d=0.0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select wert from planung_daten where plan_id="+plan_id+ " and kategorie ="+kategorie; 
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				d= res.getDouble("wert");
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return d;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return d;
	}
	
	public String getPlanung_daten_absolut(Integer plan_id,Integer kategorie) {
		String str ="";
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select absolut from planung_daten where plan_id="+plan_id+ " and kategorie ="+kategorie; 
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				str= res.getString("absolut");
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return str;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return str;
	}
	
	public Vector getAllPlanungsKategorien(String plan_id)  {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select kategorie from planung_daten where plan_id="+plan_id);
			res = stmt.executeQuery();
			while (res.next()) {
				vec.addElement(new Integer(res.getInt("kategorie")));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return  /(float)getAnz(tag,monat,year);
		return vec;
	}
	
	public double getKategorienAlleRecursivPlanung(String kategorie, Integer plan_id,Double faktor) {
		double sum=0.0;
		try {
			Vector allKats =new Vector();
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select name from kategorien where parent = '"+kategorie+"' order by name";
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				
				allKats.addElement((String) res.getString("name"));
				
				
			}
			if (planwertGleichNull(plan_id, kategorie ))
			{
				return 0.0;
			}
			sum=0.0;
			for (int i=0;i<allKats.size();i++)
			{
				sum=sum+getKategorienAlleRecursivPlanung(((String)allKats.elementAt(i)),plan_id,faktor);
			}
			// Hier erst mal schauen, ob die Kategorie zu den Parent dazugez�klt werden soll.
			// Das ist nur der Fall, wenn der Planwert != 0 ist.
			if (planwertGleichNull(plan_id, kategorie ))
			{
				return 0.0;
			}
			
			str_stm="select absolut,wert from planung_daten where plan_id="+plan_id+" and kategorie = (select id from kategorien where name ='"+kategorie+"')";
			if (debug) System.out.println(str_stm);
			stmt = con
			.prepareStatement(str_stm);
	res = stmt.executeQuery();
	while (res.next()) {
			String absolute=res.getString("absolut");
			Double wert=res.getDouble("wert");
			if (absolute.equals("relativ"))
			{
				
				wert=wert*faktor;
				//if (debug) System.out.println("Faktor multipliziert "+wert+" Faktor " +faktor);
			}
			
			sum=sum +wert;
	}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return sum;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return sum;
	}

	private boolean planwertGleichNull(int plan_id, String kategorie) {
		PreparedStatement stmt;
		ResultSet res = null;
		String str_stm = "";
		try {
			str_stm = "select wert from planung_daten where plan_id=" + plan_id
					+ " and  kategorie =  (select id from kategorien where name ='" + kategorie + "')";
			if (debug)
				System.out.println(str_stm);
			stmt = con.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Double wert = res.getDouble("wert");
				if (wert < - 0.001 || wert > 0.001) {

					return false;
					// if (debug) System.out.println("Faktor multipliziert "+wert+" Faktor "
					// +faktor);
				}

			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return true;
		}
		if (debug)
			System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return true;
	}
	
	public double getPlanungAllWhere(Integer plan_id,Double faktor,String where) {
		double sum=0.0;
		PreparedStatement stmt;
		ResultSet res = null;
		String str_stm="";
		try {						
			str_stm="select absolut,wert from planung_daten where plan_id="+plan_id+" and "+where;
			if (debug) System.out.println(str_stm);
			stmt = con
			.prepareStatement(str_stm);
	res = stmt.executeQuery();
	while (res.next()) {
			String absolute=res.getString("absolut");
			Double wert=res.getDouble("wert");
			if (absolute.equals("relativ"))
			{
				
				wert=wert*faktor;
				//if (debug) System.out.println("Faktor multipliziert "+wert+" Faktor " +faktor);
			}
			
			sum=sum +wert;
	}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return sum;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return sum;
	}
	
	public boolean getPlanWertNull(String plan_id,Integer kategorie)  {
		Double wert=0.0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select wert from planung_daten where plan_id="+plan_id+" and kategorie= "+kategorie;
			if (debug)
				System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				wert=res.getDouble("wert");
			}
			if (wert==0.0)
			{
				
				return true;
			}
			
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return false;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return  /(float)getAnz(tag,monat,year);
		return false;
	}
	public Vector getAllPlanCache(String plan_id, String kategorie_id) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			//System.out.println("select id,plan_id,kategorie_id,datum,wert,initial,initial_datum from plan_cache where plan_id="+plan_id+" and kategorie_id="+kategorie_id+" order by datum");
			stmt = con
					.prepareStatement("select id,plan_id,kategorie_id,datum,wert,initial,initial_datum from plan_cache where plan_id="+plan_id+" and kategorie_id="+kategorie_id+" order by datum");
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("plan_id", new Integer(res.getInt("plan_id")));
				hash.put("kategorie", new Integer(res.getInt("kategorie_id")));
				hash.put("datum", (Date) res.getDate("datum"));
				hash.put("wert", (Double) res.getDouble("wert"));
				hash.put("initial", (Double) res.getDouble("initial"));
				Date myDate = (Date) res.getDate("initial_datum");
				if (myDate != null)
				{
				  hash.put("initialDatum", myDate);
				}
				else
				{
					 hash.put("initialDatum", "--not calculated--");
				}
				vec.addElement(hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

	public Hashtable getAllPlanAktuell(String plan_id,String kategorie) {
		Hashtable hash = new Hashtable();
		try {
			
			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select datum,zeit,duration from plan_aktuell where plan_id="+plan_id+" and kategorie="+kategorie);
			res = stmt.executeQuery();
			while (res.next()) {
				
				hash.put("datum", (Date) res.getDate("datum"));
				hash.put("zeit", (Date) res.getTime("zeit"));
				hash.put("duration", (Integer) res.getInt("duration"));
				
				
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
	
	public Hashtable getSettings() {
		Hashtable hash = new Hashtable();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select parameter,wert from settings");
			res = stmt.executeQuery();
			while (res.next()) {
				
				hash.put( res.getString("parameter"), (String) res.getString("wert"));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
	
	
	public boolean authenticate(String pass) {
		
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			//String str_stm="select * from settings where parameter='passwort' and wert=password('"+pass+"')";
			String str_stm="select * from settings where parameter='passwort' and wert=('"+pass+"')";
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			if (res.next()) {
				return true;
			}
			else
			{
				return false;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return false;
		} 
	}
	
	public boolean updatesetting(String parameter,String wert) {
		try {
			ResultSet res = null;
			PreparedStatement stmt;
			String str= "select parameter from settings where parameter='"+parameter+"'";
			if (debug) System.out.println(str);
			stmt = con.prepareStatement(str);
			res = stmt.executeQuery();
			if (res.next()) {
				str= "update settings set wert = '"+ wert + "' where parameter ='" + parameter+ "'";
				if (debug) System.out.println(str);
					stmt = con.prepareStatement(str);
					stmt.executeUpdate();
			}
			else
			{
				str="insert into settings values(default,'"+parameter+"','"+wert+"')";
				if (debug) System.out.println(str);
				stmt = con.prepareStatement(str);
				stmt.executeUpdate();
			} 
		}catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}

	public boolean getPlanedTransaktionen(String datum,Vector vec) {
		try {
			boolean found=false;
			PreparedStatement stmt;
			ResultSet res = null;
			String str_stm="select id, name, konto_id, wert, datum, beschreibung, partner, kategorie, kor_id, cycle,planed from transaktionen where datum < '"+convDatum(datum)+"' and planed='j' order by datum";
			if (debug) System.out.println(str_stm);
			stmt = con
					.prepareStatement(str_stm);
			res = stmt.executeQuery();
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("name", (String) res.getString("name"));
				hash.put("konto", new Integer (res.getInt("konto_id")));
				hash.put("wert", new Double(res.getDouble("wert")));
				hash.put("datum", (Date)( res.getDate("datum")));
				hash.put("beschreibung", (String) res.getString("beschreibung"));
				hash.put("partner", (String) res.getString("partner"));	
				hash.put("kategorie", new Integer (res.getInt("kategorie")));
				hash.put("kor_id", new Integer (res.getInt("kor_id")));
				hash.put("cycle", new Integer (res.getInt("cycle")));
				hash.put("planed", (String) res.getString("planed"));
				vec.addElement(hash);
				found=true;
			}
			return found;
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return false;
		}
	}
		public boolean deletePlanedTransaktionen(String datum)
				{
			try {

				PreparedStatement stmt;
				// ResultSet res = null;
				//if (debug) System.out.println("insert into genre values(null,'"+genre+"') ");
				String stm_str="";			
					stm_str="delete from transaktionen where datum < ' "+convDatum(datum)+"' and planed='j' ";
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				// if (debug) System.out.println("update data_"+jahr+" set temp_out="+temp+
				// " where datum="+jahr+monat+tag+
				// " and zeit > \"18:15:00\" and zeit < \"18:45:00\" ");
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
				 return false;
			}
			return true;
		}
		
		public boolean insertRule(Hashtable hash) {
			    Calendar cal_akt= Calendar.getInstance();
				SimpleDateFormat formaterDate = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat formaterTime = new SimpleDateFormat("HHmmss");
			try {

				PreparedStatement stmt;
				String stm= "insert into rules values( default,'"
					+ formaterDate.format(cal_akt.getTime())+"','"
					+ formaterTime.format(cal_akt.getTime())+"','"
					+ formaterDate.format(cal_akt.getTime())+"','"
					+ formaterTime.format(cal_akt.getTime())+"','"
					+ hash.get("command") + "',"
					+ hash.get("rule_id") + ",'"
					+ hash.get("name") + "','"
					+ hash.get("beschreibung") + "','"
				    + hash.get("mode") + "')";
				if (debug) System.out.println(stm); 
				stmt = con.prepareStatement(stm);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
				 return false;
			}
			 return true;

		}
		
		public boolean updateRule(Hashtable hash) {
			try {
				Integer rule_id =(Integer)hash.get("rule_id");
				Calendar cal_akt= Calendar.getInstance();
				SimpleDateFormat formaterDate = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat formaterTime = new SimpleDateFormat("HHmmss");
				String str= "update rules set " +
						"name = '"+ hash.get("name") + "'," +
						"beschreibung = '"+ hash.get("beschreibung") +"',"+
						"mode = '"+ hash.get("mode")+"'," +
						"modificationdate = '" + formaterDate.format(cal_akt.getTime())+"'," +
						"modificationtime = '" + formaterTime.format(cal_akt.getTime())+"' "+					
						" where rule_id = " + hash.get("rule_id");
					
				if (debug) System.out.println(str);
				PreparedStatement stmt;
				stmt = con.prepareStatement(str);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
			    return false;
			}
			return true;
		}
		
		public boolean updateRuleCommand(Integer rule_id,String command) {
			try {
				
				String str= "update rules set command = '"
					+ command +"'where rule_id='" + rule_id+ "'";
				if (debug) System.out.println(str);
				PreparedStatement stmt;
				stmt = con.prepareStatement(str);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
			    return false;
			}
			return true;
		}
		
		public Vector getAllRules() {
			Vector vec = new Vector();
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				stmt = con
						.prepareStatement("select id,creationdate,creationtime,modificationdate,modificationtime,command,rule_id,name,beschreibung,mode from rules order by name");
				res = stmt.executeQuery();
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("id", new Integer(res.getInt("id")));
					hash.put("creationdate", (Date) res.getDate("creationdate"));
					hash.put("creationtime", (Date) res.getTime("creationtime"));
					hash.put("modificationdate", (Date) res.getDate("modificationdate"));
					hash.put("modificationtime", (Date) res.getTime("modificationtime"));
					hash.put("command", (String) res.getString("command"));
					hash.put("rule_id", new Integer(res.getInt("rule_id")));
					hash.put("name", (String) res.getString("name"));
					hash.put("beschreibung", (String) res.getString("beschreibung"));
					hash.put("mode", (String) res.getString("mode"));
					
					vec.addElement(hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return vec;
		}
		
		public Vector onlyValidRules (Vector rules)
		{
			Vector newRules= new Vector();
			for (int i=0; i<rules.size();i++)
			{
				if (! ((String)((Hashtable) rules.get(i)).get("name")).startsWith("_")) 
				{
					//System.out.println("Remove "+((String)((Hashtable) rules.get(i)).get("name")));
				 //rules.remove(i);
					newRules.addElement(rules.get(i));
				}
			}
			return newRules;
		}
		
		public Vector renditeRules (Vector rules)
        {
            Vector newRules= new Vector();
            for (int i=0; i<rules.size();i++)
            {
                if ( ((String)((Hashtable) rules.get(i)).get("name")).startsWith("_Rendite_")) 
                {
                    //System.out.println("Rendite Rules only!!");
                    //System.out.println("Remove "+((String)((Hashtable) rules.get(i)).get("name")));
                 //rules.remove(i);
                    newRules.addElement(rules.get(i));
                }
            }
            return newRules;
        }
		
		/**
		 * 
		 * @param rule_id
		 * @return
		 */
		public String getRuleCommand(Integer rule_id) {
			Vector vec = new Vector();
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				stmt = con
						.prepareStatement("select command from rules where rule_id = "+rule_id);
				if (debug) System.out.println("select command from rules where rule_id = "+rule_id);
				res = stmt.executeQuery();
				while (res.next()) {					
					return( (String) res.getString("command"));
					
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return "";
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return "";
		}
		
		public Hashtable getRule(Integer rule_id) {
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				stmt = con
						.prepareStatement("select id,creationdate,creationtime,modificationdate,modificationtime,command,rule_id,name,beschreibung,mode from rules where rule_id="+rule_id+" order by name");
				res = stmt.executeQuery();
				if (debug) System.out.println("select id,creationdate,creationtime,modificationdate,modificationtime,command,rule_id,name,beschreibung,mode from rules where rule_id="+rule_id+" order by name");
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("id", new Integer(res.getInt("id")));
					hash.put("creationdate", (Date) res.getDate("creationdate"));
					hash.put("creationtime", (Date) res.getTime("creationtime"));
					hash.put("modificationdate", (Date) res.getDate("modificationdate"));
					hash.put("modificationtime", (Date) res.getTime("modificationtime"));
					hash.put("command", (String) res.getString("command"));
					hash.put("rule_id", new Integer(res.getInt("rule_id")));
					hash.put("name", (String) res.getString("name"));
					hash.put("beschreibung", (String) res.getString("beschreibung"));
					hash.put("mode", (String) res.getString("mode"));
					
					return (hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return null;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return null;
		}
	
		public boolean deleteRule(Integer rule_id) {
			try {

				PreparedStatement stmt;
				
				if (debug) System.out.println("delete from rules where rule_id="+rule_id);
				stmt = con.prepareStatement("delete from rules where rule_id="+rule_id );
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
				 return false;
			}
			return true;
		}
		
		public boolean insertRuleItem(Hashtable hash) {
		   
		try {

			PreparedStatement stmt;
			String stm= "insert into rules_item values(default,"
				+ hash.get("rule_id") + ",'"
				+ hash.get("art") + "','"
				+ hash.get("operator") + "','"
			    + hash.get("value") + "')";
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			 return false;
		}
		 return true;

	}	
		
	
		
		public boolean deleteRuleItem(Integer rule_id) {
			try {

				PreparedStatement stmt;
				
				if (debug) System.out.println("delete from rules_item where rule_id="+rule_id);
				stmt = con.prepareStatement("delete from rules_item where rule_id="+rule_id );
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
				 return false;
			}
			return true;
		}

		public Vector getRulesItems(Integer rule_id) {
			Vector vec = new Vector();
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				stmt = con
						.prepareStatement("select id,rule_id,art,operator,value from rules_item where rule_id="+rule_id);
				res = stmt.executeQuery();
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("id", new Integer(res.getInt("id")));
					hash.put("rule_id", new Integer(res.getInt("rule_id")));
					hash.put("art", (String) res.getString("art"));
					hash.put("operator", (String) res.getString("operator"));
					hash.put("value", (String) res.getString("value"));				
					vec.addElement(hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return vec;
		}	
		
		
		public void insert_history(String user, String mode, String where)
		{
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String str="select id from transaktionen "+ where;
			if (debug) System.out.println(str); 
			stmt = con
					.prepareStatement(str);
			res = stmt.executeQuery();
			while (res.next()) {
				vec.addElement( new Integer(res.getInt("id")));				
			}
			Calendar cal_akt= Calendar.getInstance();
			SimpleDateFormat formaterDate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formaterTime = new SimpleDateFormat("HHmmss");
			for (int i=0;i<vec.size();i++)
			{
				
			
			//PreparedStatement stmt;
			String stm= "insert into transaktion_history values(default,"
				+ vec.elementAt(i)+",'"
				+ mode +"','"
				+ formaterDate.format(cal_akt.getTime())+"','"
				+ formaterTime.format(cal_akt.getTime())+"','"
				+ user+"')";
				;			
			if (debug) System.out.println(stm);
			stmt = con.prepareStatement(stm);
			stmt.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
	}
		
		
		public Vector getTransaktionHistorie(String trans_id) {
			Vector vec = new Vector();
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				String str="select id,trans_id,type,datum,zeit,curruser from transaktion_history where trans_id="+trans_id+" order by datum,zeit";
				if (debug) System.out.println(str);
				stmt = con
						.prepareStatement(str);
				res = stmt.executeQuery();
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("id", new Integer(res.getInt("id")));
					hash.put("trans_id", new Integer(res.getInt("trans_id")));
					hash.put("type", new Integer(res.getInt("type")));
					hash.put("datum", (Date)( res.getDate("datum")));
					hash.put("zeit", (Date) res.getTime("zeit"));
					hash.put("user", (String) res.getString("curruser"));										
					vec.addElement(hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return vec;
		}
		
		
		public int getUnknownBons() {
			int  erg =0;
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				
				String str="select count(*) as anz from bon where internalname='unknown'";
				if (debug) System.out.println(str);
				stmt = con
						.prepareStatement(str);
				res = stmt.executeQuery();
				while (res.next()) {
					erg= new Integer (res.getInt("anz")).intValue();
					
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return 0;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return erg;
		}
		
		public Hashtable getBon() {
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				stmt = con
						.prepareStatement("select id,company, internalname, rawname, rawname_mutant from bon where internalname='unknown' limit 1");
				res = stmt.executeQuery();
				if (debug) System.out.println("select id,company, internalname, rawname, rawname_mutant from bon where internalname='unknown' limit 1");
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("id", new Integer(res.getInt("id")));
					hash.put("company", (String) res.getString("company"));
					hash.put("internalname", (String) res.getString("internalname"));
					hash.put("rawname", (String) res.getString("rawname"));
					hash.put("rawname_mutant", (String) res.getString("rawname_mutant"));
					return (hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return null;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return null;
		}
		
		public boolean updateBon(Hashtable hash) {
			try {
				String id= ((Integer)hash.get("id")).toString();
				String internalname= (String)hash.get("internalname");
				String rawname= (String)hash.get("rawname");
				String rawname_mutant =(String)hash.get("rawname_mutant");
				String str= "update bon set " +
						"internalname = '"+internalname+"',"+
						"rawname = '"+rawname+"',"+
						"rawname_mutant = '"+rawname_mutant+"' where id = '"+id+"'";
						
					
				if (debug) System.out.println(str);
				PreparedStatement stmt;
				stmt = con.prepareStatement(str);
				stmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
			    return false;
			}
			return true;
		}
		
		public Vector getAllAnlagen() {
	        Vector vec = new Vector();
	        try {

	            PreparedStatement stmt;
	            ResultSet res = null;
	            stmt = con
	                    .prepareStatement("select id,name,rendite,rule_id from anlagen order by name");
	            res = stmt.executeQuery();
	            while (res.next()) {
	                Hashtable hash = new Hashtable();
	                hash.put("id", new Integer(res.getInt("id")));
	                hash.put("name", (String) res.getString("name"));
	                hash.put("rendite", (String) res.getString("rendite"));
	                hash.put("rule_id", (Integer) res.getInt("rule_id"));
	                vec.addElement(hash);
	            }
	        } catch (SQLException e) {
	            System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
	            return vec;
	        }
	        if (debug) System.out.println("Select-Anweisung ausgeführt");
	        // return summe/(float)getAnz(tag,monat,year);
	        return vec;
	    }
		
		  public String getAnlagenamebyID(String id) {
		      String result="";
	            try {	               
	                PreparedStatement stmt;
	                ResultSet res = null;
	                stmt = con
	                        .prepareStatement("select name from anlagen where id = "+id);
	                res = stmt.executeQuery();
	                while (res.next()) {
	                    result = res.getString("name");              
	                }
	            } catch (SQLException e) {
	                System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
	                return result;
	            }
	            if (debug) System.out.println("Select-Anweisung ausgeführt");
	            // return summe/(float)getAnz(tag,monat,year);
	            return result;
	        }
		
		public boolean insertAnlage(Hashtable hash) {
	        try {

	            PreparedStatement stmt;
	            String stm= "insert into anlagen values(default,'" 
	                    + hash.get("name") + "','"
	                    + hash.get("beschreibung") + "','"
	                    + hash.get("rendite") + "',"
	                    + hash.get("rule_id") + ")";
	                
	            if (debug) System.out.println(stm);
	            stmt = con.prepareStatement(stm);
	            stmt.executeUpdate();
	        } catch (SQLException e) {
	            System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
	             return false;
	        }
	         return true;

	    }
		
		public boolean deleteAnlage(String name) {
	        try {
	    
	            PreparedStatement stmt;
	            ResultSet res = null;
	            /*
	             * Pr�fe erstmal, ob es noch Konten mit dieser Anlage gibt
	             */
	            Vector vec = getAllKonto(name);
	            if (vec.size() > 0)
	            {
	                System.out.println("!!Es gibt noch Konten in dieser Anlage. Ich kann die Anlage nicht L�schen!!!");
	                return false;
	            }
	            stmt = con.prepareStatement("delete from anlagen where name ='" +name+"'" );
	            stmt.executeUpdate();	           
	        } catch (SQLException e) {
	            System.err.println("Konnte Delete-Anweisung nicht ausführen" + e);
	             return false;
	        }
	    return true;        
	    }
		
		public boolean updateAnlage(Hashtable hash) {
	        try {
	            String id= ((Integer)hash.get("id")).toString();
	            String name= (String)hash.get("name");
	            String beschreibung= (String)hash.get("description");
	            String rendite= (String)hash.get("rendite");
	            Integer rule_id= (Integer)hash.get("rule_id");
	            
	            String str= "update anlagen set " +
	                    "name = '"+ name + "'," +
	                    "beschreibung = '"+beschreibung+"',"+
	                    "rendite = '"+rendite+"',"+
	                    "rule_id = "+rule_id+
	                    " where id = '"+id+"'";
	                    
	           // updateAllParents(name,id);
	            if (debug) System.out.println(str);
	            PreparedStatement stmt;
	            stmt = con.prepareStatement(str);
	            stmt.executeUpdate();
	            
	        } catch (SQLException e) {
	            System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
	            return false;
	        }
	        return true;
	    }
		
		 public Integer getAnstehendePlanungsJobs() {
            Integer result=0;
              try {                  
                  PreparedStatement stmt;
                  ResultSet res = null;
                  stmt = con
                          .prepareStatement("select count (id) as anz from tmpplanningjobs t");
                  res = stmt.executeQuery();
                  while (res.next()) {
                      result = res.getInt("anz");              
                  }
              } catch (SQLException e) {
                  System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
                  return result;
              }
              if (debug) System.out.println("Select-Anweisung ausgeführt");
              // return summe/(float)getAnz(tag,monat,year);
              return result;
          }
		
		 public Integer getAvgCumputaionTimeForPlanningJobs(String planID) {
	            Integer result=0;
	              try {                  
	                  PreparedStatement stmt;
	                  ResultSet res = null;
	                  stmt = con
	                          .prepareStatement("select avg(duration) as dur from plan_aktuell pa where plan_id = "+planID);
	                  res = stmt.executeQuery();
	                  while (res.next()) {
	                      result = res.getInt("dur");              
	                  }
	              } catch (SQLException e) {
	                  System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
	                  return result;
	              }
	              if (debug) System.out.println("Select-Anweisung ausgeführt");
	              // return summe/(float)getAnz(tag,monat,year);
	              return result;
	          }
		  public Vector getRenditeByKonto(int konto, String startdate,String enddate, String command) {
               Vector vec = new Vector();
		       String result="";
		       
		       /*  Dadurch, dass bei den Regeln konto_id verwendet wird, hier aber konto, muessen wir eine Substitution machen.
		        * */		     
		        command=command.replace("konto_id", "konto"); 
	
                try {                  
                    PreparedStatement stmt;
                    ResultSet res = null;
                    String query = "select value,amount,datum from rendite  where konto = "+konto +" and datum > '"+convDatum(startdate)+"' and datum <= '"+convDatum(enddate)+"' "+command+" order by datum";
                    if ( debug )
                    {
                        System.out.println(query);
                    }
                    stmt = con
                            .prepareStatement(query);
                    res = stmt.executeQuery();
                        while (res.next()) {
                            Hashtable hash = new Hashtable();
                            hash.put("value", new Double(res.getDouble("value")));
                            hash.put("amount", new Double(res.getDouble("amount")));
                            hash.put("datum", (Date)( res.getDate("datum")));
                            vec.add(hash);
                    }
                } catch (SQLException e) {
                    System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
                    return vec;
                }
                if (debug) System.out.println("Select-Anweisung ausgeführt");
                // return summe/(float)getAnz(tag,monat,year);
                return vec;
            }
		private boolean  checkOrderRendite() {
		boolean result= false;
		try {
			PreparedStatement stmt;
			ResultSet res = null;
			stmt = con
					.prepareStatement("select id from orderrendite");
			res = stmt.executeQuery();
			while (res.next()) {
				result = true;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return result;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return result;
	}

	public boolean insertOrderRendite(String startdatum, String enddatum, String ruleId) {
		try {
			if (checkOrderRendite())
			{
				String str= "update orderrendite set " +
						" datum = current_timestamp, "+
						"startdate = '"+  startdatum + "'," +
						"enddate = '"+enddatum+"',"+
						"ruleid = "+ruleId+ ","+
						"finished = 0";


				// updateAllParents(name,id);
				debug =true;
				if (debug) System.out.println(str);
				debug = false;
				PreparedStatement stmt;
				stmt = con.prepareStatement(str);
				stmt.executeUpdate();
			}
			else {
				PreparedStatement stmt;
				String stm = "insert into orderrendite values(default,current_timestamp,"
						+ " '" + startdatum + "','"
						+ enddatum + "',"
						+ ruleId + ","
						+ " 0)";
				debug = true;
				if (debug) System.out.println(stm);
				debug = false;
				stmt = con.prepareStatement(stm);
				stmt.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Konnte Insert-Anweisung nicht ausführen" + e);
			return false;
		}
		return true;

	}
	public Hashtable getOrderRendite() {
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String statement= "select id,datum, startdate, enddate, ruleid,finished from orderrendite limit 1";
			stmt = con
					.prepareStatement(statement);
			res = stmt.executeQuery();
			if (debug) System.out.println(statement);
			while (res.next()) {
				Hashtable hash = new Hashtable();
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("datum", (Date) res.getTimestamp("datum"));
				hash.put("startdate", (Date) res.getDate("startdate"));
				hash.put("enddate", (Date) res.getDate("enddate"));
				hash.put("ruleid", (Integer) res.getInt("ruleid"));
				hash.put("finished", (Integer) res.getInt("finished"));
				return (hash);
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return null;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return null;
	}

	public Hashtable getRenditeBatch(int konto) {

		String result="";
		Hashtable hash = new Hashtable();
		/*  Dadurch, dass bei den Regeln konto_id verwendet wird, hier aber konto, muessen wir eine Substitution machen.
		 * */

		try {
			PreparedStatement stmt;
			ResultSet res = null;
			String query = "select konto,ertrag,ertrag_prozent,id,wert_pro_tag from renditebatch  where konto = "+konto +" limit 1";
			debug=true;
			if ( debug )
			{
				System.out.println(query);
			}
			debug=false;
			stmt = con
					.prepareStatement(query);
			res = stmt.executeQuery();
			while (res.next()) {

				hash.put("konto", new Integer(res.getInt("konto")));
				hash.put("id", new Integer(res.getInt("id")));
				hash.put("ertrag", new Double(res.getDouble("ertrag")));
				hash.put("ertragProzent", new Double(res.getDouble("ertrag_prozent")));
				hash.put("wertProTag", new Double(res.getDouble("wert_pro_tag")));
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return hash;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return hash;
	}
		private String convDatum(String dat)
		{
			
			if (dat.contains("-"))
			{
				return dat;
			}
			else
			{
			String year= dat.substring(0,4);
			//System.out.println("year = "+year);
			String month= dat.substring(4,6);
			//System.out.println("month = "+month);
			String day= dat.substring(6,8);
			//System.out.println("day = "+day);
			return year+"-"+month+"-"+day;
			}
		}
		
}