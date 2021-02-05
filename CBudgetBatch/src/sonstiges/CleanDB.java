package sonstiges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import cbudgetbase.DB;

public class CleanDB extends DB {
	
	public int getAnzTransHist ()
	{
		int erg=0;
		try {

			PreparedStatement stmt;
			ResultSet res = null;
			String stm_str ="";
			
			stm_str="select count(*) as anz from transaktion_history"; 
			
			
	
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

	
	public Vector deleteTransID(int limit,int offset ) {
		Vector vec = new Vector();
		try {

			PreparedStatement stmt;
			ResultSet res = null;
		
			stmt = con
					.prepareStatement(" select id from transaktion_history where trans_id not in ( select  id from transaktionen) limit "+limit+" offset "+offset );
			
				System.out.println("select id from transaktion_history where trans_id not in ( select  id from transaktionen) limit "+limit+" offset "+offset );
			
			res = stmt.executeQuery();
			while (res.next()) {
				int found= new Integer(res.getInt("id"));
				System.out.println("loesche "+found );
				PreparedStatement stmt2 = con.prepareStatement("delete  from transaktion_history where trans_id = "+found);   			
				stmt2.executeUpdate();;
			}
		} catch (SQLException e) {
			System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
			return vec;
		}
		if (debug) System.out.println("Select-Anweisung ausgeführt");
		// return summe/(float)getAnz(tag,monat,year);
		return vec;
	}

}
