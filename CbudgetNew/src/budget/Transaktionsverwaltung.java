package budget;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Date;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Transaktionsverwaltung extends javax.servlet.http.HttpServlet {

		private static final long serialVersionUID = 1L;
		private Double aktWert=0.0;

		public void doGet(javax.servlet.http.HttpServletRequest request,
				javax.servlet.http.HttpServletResponse response)
				throws javax.servlet.ServletException, java.io.IOException {

			performTask(request, response);

		}

		public void doPost(javax.servlet.http.HttpServletRequest request,
				javax.servlet.http.HttpServletResponse response)
				throws javax.servlet.ServletException, java.io.IOException {
			performTask(request, response);

		}

		public void performTask(javax.servlet.http.HttpServletRequest request,
				javax.servlet.http.HttpServletResponse response) {
			try {
				//FileHandling fh = new FileHandling();
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				HttpSession session = request.getSession(true);
				HeaderFooter hf = new HeaderFooter();
				int limit_max=1;
				int limit_min=-1;

				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				String konto = request.getParameter("konto");				
				Vector vecTrans= new Vector();
				
				Vector allKategorien= new Vector();
				allKategorien=db.getAllActiveKategorien();
				Vector allKonten= new Vector();
				allKonten=db.getAllKonto();
				session.setAttribute("konten", allKonten);
				session.setAttribute("kategorien",allKategorien);
				session.setAttribute("akt_konto",konto);
				
				
				
				String transzeile="";
//Allle Transaktionen durchgehen und eintragen
				
				Calendar cal_aktuell= Calendar.getInstance();
				Calendar cal_next= Calendar.getInstance();
				Calendar cal_begin= Calendar.getInstance();
				Calendar cal_end= Calendar.getInstance();
				cal_begin.add(Calendar.MONTH, limit_min);
				cal_end.add(Calendar.MONTH, limit_max);
				SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
				vecTrans=db.getAllTransaktionenWithWhere("where konto_id = (select id from konten where kontoname='"+konto+"') AND datum > to_date('"
						+formater.format(cal_begin.getTime())+"','YYYY-MM-DD') AND datum < to_date('"
								+formater.format(cal_end.getTime())+"','YYYY-MM-DD') order by datum");
				session.setAttribute("transaktionen",vecTrans);
				//SimpleDateFormat forma tter = new SimpleDateFormat("yyyy-MM-dd");
				//cal_begin.setTime((Date)hash_trans.get("datum"));
				String bgcolor="";
				
				//aktWert=0.0;
				aktWert=db.getAktuellerKontostand(konto, formater.format(cal_begin.getTime()), "");
				String strAktStand;
				Vector chartvec = new Vector();
				System.out.println(vecTrans.size() +" Transaktionen gefunden");
				for (int i=0; i<vecTrans.size();i++)
				{
					
				Hashtable hash_chart = new Hashtable();
				hash_chart.put("datum",(Date)((Hashtable)vecTrans.elementAt(i)).get("datum"));
				Double wert=(Double)((Hashtable)vecTrans.elementAt(i)).get("wert");
				cal_next.setTime((Date)((Hashtable)vecTrans.elementAt(i)).get("datum"));
				cal_begin.setTime((Date)((Hashtable)vecTrans.elementAt(i)).get("datum"));
				cal_end.setTime((Date)((Hashtable)vecTrans.elementAt(i)).get("datum"));
				cal_begin.add(Calendar.MONTH, limit_min);
				cal_end.add(Calendar.MONTH, limit_max);
			     strAktStand=berechneKontostand(wert);
				if (cal_begin.compareTo(cal_aktuell)>0 ||cal_end.compareTo(cal_aktuell)<0)
				{
					continue;
				}
				String defcolor=">";
				if (((String)((Hashtable)vecTrans.elementAt(i)).get("planed")).equals("j"))
						{
					defcolor=" bgcolor=\"#CCCCCC\">";
						}
				if (cal_next.compareTo(cal_aktuell)<0)
				{
					bgcolor=" bgcolor=\"#00FFFF\">";
					//Setze nur wenn das nächste Feld nicht gesetzt würed
					if (i<vecTrans.size()-1)
					{
						cal_next.setTime((Date)((Hashtable)vecTrans.elementAt(i+1)).get("datum"));
						if (cal_next.compareTo(cal_aktuell)<0)
						{
							bgcolor=defcolor;
						}
					}
				}
				else
				{
					bgcolor=defcolor;
				}
				//out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
				transzeile=transzeile+"<tr>";
				transzeile=transzeile+"<td"+bgcolor+"<input type=\"checkbox\" name=\"loeschen\" value=\""+new Integer(i).toString()+"\"></td>";
				transzeile=transzeile+"\n<td"+bgcolor+""+((Hashtable)vecTrans.elementAt(i)).get("name")+"</td>";
				String color;
				if (ispositiv(wert))
					{
					color="green";
					}
				else
				{
					color="red";
				}
				transzeile=transzeile+"\n<td"+bgcolor+"<font color=\""+color+"\">"+wert.toString()+"</font></td>";
				
				if (ispositiv(aktWert))
				{
				color="green";
				}
			else
			{
				color="red";
			}
				hash_chart.put("wert",aktWert);
				chartvec.addElement(hash_chart);
				
				transzeile=transzeile+"\n<td"+bgcolor+"<font color=\""+color+"\">"+strAktStand+"</font> </td>";
				
				transzeile=transzeile+"\n<td"+bgcolor+""+calcKategorie((Integer)((Hashtable)vecTrans.elementAt(i)).get("kategorie"),allKategorien)+"</td>";
				transzeile=transzeile+"\n<td><a href=neueZyklischeTransaktion?vec_id="+i+"&trans="+(Integer)((Hashtable)vecTrans.elementAt(i)).get("id")+"&kor_id="+(Integer)((Hashtable)vecTrans.elementAt(i)).get("kor_id")+">"+chooseIcon((Integer)((Hashtable)vecTrans.elementAt(i)).get("kor_id"),(Integer)((Hashtable)vecTrans.elementAt(i)).get("cycle"))+"</a></td>";
				transzeile=transzeile+"\n<td"+bgcolor+""+formatDatum((Date)((Hashtable)vecTrans.elementAt(i)).get("datum"))+"</td>";
				transzeile=transzeile+"<tr>";			
		}
		session.setAttribute("chart_vec",chartvec);
		
				out.println("<html>");
				out.println("<body bgcolor=\"#EEFFBB\">");
				//out.println("<body>");
				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				
				out.println("<table border=\"0\">");
				out.println("<tr><td>");
				out.println("<h1>Transaktionsverwaltung von "+konto+"</h1>");
				out.println("</p>");
				out.println("<font size=\"-2\">Bitte beachten Sie, dass nur die Transaktionen von vor "+limit_min+" Monaten bis +"+limit_max+" Monaten angezeigt werden</font>");
				out.println("<td/><td>");
				out.println("<img src=chart?mode=verlauf width'400' height='200'>");
				out.println("</td></tr>");
				out.println("</table>");
				out.println("</p>");
				
				
				out.println("<table cellpadding=\"0\" cellspacing=\"4\" width=\"114\" border=\"0\">");
				out.println("<tr><td>");
				out.println("<form action=\"neueTransaktion\">");
				out.println("<input type=\"submit\" name=\"Text1\" value=\"Neue Transaktion tätigen\">");
				out.println("</form>");
				out.println("</td>");
				out.println("<td>");
				out.println("<form action=\"transaktionAendern\">");
				out.println("<input type=\"submit\" name=\"Text2\" value=\"Transaktion ändern/löschen\">");
				out.println("</td></tr>");
				out.println("</table>");
				
				out.println("</p>");
				
//Übersicht über die Konten
				out.println("<table border=\"1\"  bgcolor=\"#CCEECC\">");
				//out.println("<table border=\"1\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th></th>");
				out.println("<th>Name der ausgeführeten Transaktion</th>");
				out.println("<th>Buchungsbetrag</th>");
				out.println("<th>aktueller Kontostand</th>");
				out.println("<th>Kategorie</th>");
				out.println("<th>Typ</th>");
				out.println("<th>Datum</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				
				out.println(transzeile);
				out.println("</tr>");	
				out.println("</tbody>");
				out.println("</table>");
				
				out.println("</form>");
				out.println("</body>");
				out.println("</html>");
				out.close();
			}

			catch (Throwable theException) {
				theException.printStackTrace();
			}
		}
	public String berechneKontostand(Double wert )
	{
		DecimalFormat f = new DecimalFormat("#0.00");
		aktWert=aktWert.doubleValue()+wert.doubleValue();
		
		return ""+ f.format(aktWert);
	}
	private boolean ispositiv(Double wert)
	{
		if (wert>0)
		{
			return true;
		}
		else
			return false;
	}
	private String calcKategorie(Integer id ,Vector allKat)
	{
	  for (int i=0;i<allKat.size();i++)
	  {
		  if (id.intValue()==((Integer)((Hashtable)allKat.elementAt(i)).get("id")).intValue())
		  {
			  return (String)((Hashtable)allKat.elementAt(i)).get("name");
		  }
	  }
	  return "keine Kategorie";
	}
	private String chooseIcon(Integer id,Integer cycle)
	{
		if (id.intValue()==0)
		{
			return "<img src=\"icons/finished_transaction.png\" width'20' height='20'>";
		}
		else
		{
			if (cycle.intValue()==0)
			{
			return "<img src=\"icons/finished_transferal_target_transaction.png\" width'20' height='20'>";
			}
			else
			{
				if (cycle.intValue()==1)
				{
					return "<img src=\"icons/planned_transferal_source_transaction.png\" width'20' height='20'>";
				}
				else
				{
					return "<img src=\"icons/planned_transferal_target_transaction.png\" width'20' height='20'>";
				}
			}
			}
	}
	private String formatDatum(Date datum)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("E dd.MM.yyyy");
		Calendar cal= Calendar.getInstance();
		cal.setTime(datum);
		//System.out.println(" Cal = " + formatter.format(cal.getTime()));
		return formatter.format(cal.getTime());
	}
}