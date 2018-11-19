package budget;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

public class HeaderFooter {
	
	/*public void writeHeader(PrintWriter out)
	{

		out.println(" <img src=\"icons/budget.gif\" width='1200' height='40'><p>");
		out.println("<h1><font color=\"black\" size=1> Datenbank: Testversion</font></h1><p>");
		out.println("<a href='kontouebersicht'>Kontouebersicht</a>");
		out.println("<a href='regelverwaltung'>Regeln</a>");
		out.println("<a href='kontoverwaltung'>Kontoverwaltung</a>");
		out.println("<a href='transaktionen'>Transaktionen</a>");
		out.println("<a href='kategorieverwaltung'>Kategorieverwaltung</a>");
		out.println("<a href='planungsverwaltung'>Planung</a>");
		out.println("<a href='auswertung?mode=none'>Statistische Auswertung</a>");
		out.println("<a href='abmelden'>Abmelden</a>");
	}
*/
	public void writeHeader(PrintWriter out,String database)
	{

		out.println(" <img src=\"icons/budget.gif\" width='1200' height='40'><p>");
		out.println("<h1><font color=\"black\" size=1> Datenbank: "+ database+"</font></h1><p>");
		out.println("<a href='kontouebersicht'>Kontouebersicht</a>");
		out.println("<a href='regelverwaltung'>Regeln</a>");
		out.println("<a href='kontoverwaltung'>Kontoverwaltung</a>");
		out.println("<a href='transaktionen'>Transaktionen</a>");
		out.println("<a href='kategorieverwaltung'>Kategorieverwaltung</a>");
		out.println("<a href='planungsverwaltung'>Planung</a>");
		out.println("<a href='auswertung?mode=none'>Statistische Auswertung</a>");
		out.println("<a href='einstellungen'>Einstellungen</a>");
		out.println("<a href='abmelden'>Abmelden</a>");
	}
	public void writeStatisticHeader(PrintWriter out)
	{
		out.println("<p>");
		out.println("<a href='auswertung?mode=none'>Kategorien</a>");
		out.println("<a href='auswertungverlauf'>Kontoverlauf</a>");
		out.println("<a href='auswertungmonat'>Monatsvergleich</a>");
		out.println("<a href='auswertungart'>Anlageart</a>");
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
	
}
