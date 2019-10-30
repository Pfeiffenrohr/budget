package cbudgetbatch;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class VergleicheDatenbanken {

	public static void main(String[] args) {
		
		DBBatch dborig = new DBBatch();
        dborig.dataBaseConnect("budget", "budget", "jdbc:postgresql://192.168.2.28/");
        
		DBBatch dbclone = new DBBatch();
        dbclone.dataBaseConnect("budget", "budget", "jdbc:postgresql://192.168.2.29/");
		// TODO Auto-generated method stub

        Vector vec = dbclone.getAllTransaktionenWithWhere("where datum < '2019-10-28'");
        
        for (int i=0; i < vec.size(); i++)
        
        {
        	Hashtable hash= (Hashtable) vec.elementAt(i);
        	Vector found=dborig.getAllTransaktionenWithWhere( "where id = '"+ hash.get("id") +"'"+
        													" and name = '"+ hash.get("name") +	"'"+	
        													" and konto_id = "+ hash.get("konto") +	
        													" and datum = '"+ hash.get("datum") +"'"+	
        													" and kor_id = "+ hash.get("kor_id") +	
        													" and cycle = "+ hash.get("cycle") +	
        													" and planed = '"+ hash.get("planed")+"'");	
        	if (found.size() == 0)
        	{
        		System.out.println(hash);
        		dbclone.updateWildcard("update reccuring set enddatum='"+hash.get("datum")+"' where kor_id = "+ hash.get("kor_id"));
        		dbclone.deleteTransaktion((int)hash.get("id"));
        	}
        			
        			
        }
        
        
	}

}
