package budget_batch;
import common.FileHandling;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Date;
public class InsertFileInBatch {
	
	private static Vector readFile(String file )
	{
		FileHandling fh = new FileHandling();
		Vector file_vec = new Vector();
		file_vec=fh.readFileAsVector(file);
		return file_vec;
		
	}
	
	public void parse(DBBatch db,Vector lines)
	{
		System.out.println("Starte den Parser "); 
		Hashtable hash=new Hashtable();
		for (int i=0;i<lines.size();i++)
		{
			 SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		     String timestamp=sdf.format(new Date());
			
			String str []={"","","","",""};
			
			str =((String)lines.elementAt(i)).split(";");
			if (str.length < 5)
			{
				System.out.println(timestamp+"Fehler! Unerwartete Zeile "+ (String)lines.elementAt(i));
				continue;
			}
			hash.put("kategorie",str[0]);
			hash.put("konto",str[1]);
			hash.put("datum",str[2]);
			if (str[3].isEmpty())
			{
				continue;
			}
			hash.put("name",str[3]);
			if (str[4].isEmpty())
			{
				continue;
			}
			hash.put("wert",str[4]);
			hash.put("beschreibung","Eintrag von Batch");
			hash.put("partner","");
			hash.put("cycle","0");
			hash.put("planed","n");
			hash.put("user", "Android");
			//System.out.println("Feld: "+str); 
			if (! db.checkKonto((String)hash.get("konto")))
			{
				System.err.println((String)lines.elementAt(i));
				System.err.println(timestamp+"Fehler: Konto "+(String)hash.get("konto")+ " nicht gefunden");
				   continue;
			}
			if (! db.checkKategorie((String)hash.get("kategorie")))
			{
			System.err.println((String)lines.elementAt(i));
		   System.err.println(timestamp+"Fehler: Kategorie "+(String)hash.get("kategorie")+ " nicht gefunden");
		   continue;
			}
			if (! konvertDatum(hash))
					{
				System.err.println((String)lines.elementAt(i));
				   System.err.println(timestamp+"Fehler: falsches Datumformat ");
				   continue;
					}
			if (! konvertWert(hash))
			{
				System.err.println((String)lines.elementAt(i));
		   System.out.println(timestamp+"Fehler: falsches Format für Wert");
		   continue;
			}
			
			
			if (db.isAlreadyInsert(hash))
			{
				System.out.println(timestamp+"Eintrag ist schon vorhanden");
			}
			else
			{
				db.insertTransaktion(hash,0);
			}
				
		}
	}
	
	private boolean konvertDatum( Hashtable hash)
	{
		try  {
		String datum=(String)hash.get("datum");
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Calendar cal= Calendar.getInstance();
		cal.setTime(formatter.parse(datum));
		formatter.applyPattern("yyyy-MM-dd");
		hash.put("datum",formatter.format(cal.getTime()));
		}
		catch (Exception ex) {
			System.err.println("Falsches Datumformat "+(String)hash.get("datum"));
			return false;
		}
		return true;
   }

	
	private boolean konvertWert( Hashtable hash)  
	{
		try  {
		String wert=(String)hash.get("wert");
		wert.replaceAll(",",".");
		System.out.println("Wert "+wert);
		Double d = new Double(wert);
		d=d*-1;
		wert=d.toString();
		hash.put("wert", wert);
		}
		catch (Exception ex) {
			System.err.println("Falsches Wertformat "+(String)hash.get("wert"));
			return false;
		}
		return true;  
   }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String user = args[0];
		String pass = args[1];
		String datenbank = args [2];
		String datei=args[3];
		
		DBBatch db = new DBBatch();
		db.dataBaseConnect(user,pass,datenbank);
		InsertFileInBatch ifib = new InsertFileInBatch();
		Vector lines=readFile(datei);
		ifib.parse(db,lines);
		System.out.println("Fertig");
		db.closeConnection();
		

	}

}
