package budget;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Kontoverwaltung extends javax.servlet.http.HttpServlet {

		private static final long serialVersionUID = 1L;

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
				
			

				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				Vector vec= new Vector();
				vec=db.getAllKonto();
				session.setAttribute("konten", vec);
				double gesamtsumme=0.0;
				int offset=0;
				out.println("<html>");
				out.println("<body  bgcolor=\"#EEFFBB\">");
				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<h1>Kontoverwaltung</h1>");
				out.println("<table cellpadding=\"0\" cellspacing=\"4\" width=\"114\" border=\"0\">");
				out.println("<tr><td>");
				out.println("<form action=\"neuesKonto\">");
				out.println("<input type=\"submit\" name=\"Text 1\" value=\"Neues Konto anlegen\">");
				out.println("</form>");
				out.println("</td>");
				out.println("<td>");
				out.println("<form action=\"kontoAendern\">");
				out.println("<input type=\"submit\" name=\"Text 2\" value=\"Konto ändern/löschen\">");
				out.println("</td></tr>");
				out.println("</table>");
				//out.println("</p>");
				out.println("<th align=\"center\" valign=\"top\">");
				//--------------------- Tabelle Geldkonto-------------------------------------------------
				out.println("<table border=\"1\" rules=\"groups\">");
				out.println("<td>");
				String mode="Geldkonto";
				vec=db.getAllKonto(mode);
				double summe=0.0;
				double akt_stand;
				out.println("<h2>"+mode+"</h2>");
//Übersicht über die Konten
				out.println("<table border=\"1\" rules=\"groups\">");
				//out.println("<table border=\"1\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th></th>");
				out.println("<th>Kontoname</th>");
				out.println("<th>Kontostand</th>");
				out.println("<th>versteckt</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				out.println("<tr>");
				
				//Allle Konten durchgehen und eintragen
			
				
				for (int i=0; i<vec.size();i++)
				{
				//	Double summe=db.getAktuellerKontostand((String)((Hashtable)vec.elementAt(i)).get("name"), formatter.format(cal.getTime()),"");
				//out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
			     out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Hashtable)vec.elementAt(i)).get("id")+"\"></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("name")+"</td>");
				akt_stand=db.getAktuellerKontostand((String)((Hashtable)vec.elementAt(i)).get("name"), formatter.format(cal.getTime()),"");
				out.println("<td><font color=\"green\">"+formater(akt_stand,3)+"</font></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("versteckt")+"</td>");
				out.println("</tr>");
				summe=summe+akt_stand;
				}
				offset+=vec.size();
				out.println("<tr>");
				out.println("<td></td><td><font size=\"5\"> Summe: </font></td><td>"+formater(summe,5)+"</td><td></td>");
				out.println("</tr>");
				out.println("</tbody>");
				out.println("</table>");
				out.println("</th>");
				gesamtsumme=gesamtsumme+summe;
				//out.println("<tr>");
				//----------------------------------------------------------------------
				//--------------------- Tabelle Geldanlage-------------------------------------------------
				out.println("</td><td>");
				out.println("<th align=\"center\" valign=\"top\">");
				mode="Geldanlage";
				vec=db.getAllKonto(mode);
				summe=0.0;
				akt_stand=0.0;
				out.println("<h2>"+mode+"</h2>");
//Übersicht über die Konten
				out.println("<table border=\"1\" rules=\"groups\">");
				//out.println("<table border=\"1\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th></th>");
				out.println("<th>Kontoname</th>");
				out.println("<th>Kontostand</th>");
				out.println("<th>versteckt</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				out.println("<tr>");
				
				//Allle Konten durchgehen und eintragen
			
				
				for (int i=0; i<vec.size();i++)
				{
				//	Double summe=db.getAktuellerKontostand((String)((Hashtable)vec.elementAt(i)).get("name"), formatter.format(cal.getTime()),"");
				//out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
			     out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Hashtable)vec.elementAt(i)).get("id")+"\"></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("name")+"</td>");
				akt_stand=db.getAktuellerKontostand((String)((Hashtable)vec.elementAt(i)).get("name"), formatter.format(cal.getTime()),"");
				out.println("<td><font color=\"green\">"+formater(akt_stand,3)+"</font></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("versteckt")+"</td>");
				out.println("</tr>");
				summe=summe+akt_stand;
				}
				offset+=vec.size();
				out.println("<tr>");
				out.println("<td></td><td><font size=\"5\"> Summe: </font></td><td>"+formater(summe,5)+"</td><td></td>");
				out.println("</tr>");
				out.println("</tbody>");
				out.println("</table>");
				out.println("</th>");
				gesamtsumme=gesamtsumme+summe;
				//--------------------- Tabelle Sachanlage-------------------------------------------------
				out.println("</td><td>");
				out.println("<th align=\"center\" valign=\"top\">");
				mode="Sachanlage";
				vec=db.getAllKonto(mode);
				summe=0.0;
				akt_stand=0.0;
				out.println("<h2>"+mode+"</h2>");
//Übersicht über die Konten
				out.println("<table border=\"1\" rules=\"groups\">");
				//out.println("<table border=\"1\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th></th>");
				out.println("<th>Kontoname</th>");
				out.println("<th>Kontostand</th>");
				out.println("<th>versteckt</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				out.println("<tr>");
				
				//Allle Konten durchgehen und eintragen
			
				
				for (int i=0; i<vec.size();i++)
				{
				//	Double summe=db.getAktuellerKontostand((String)((Hashtable)vec.elementAt(i)).get("name"), formatter.format(cal.getTime()),"");
				//out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
			     out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Hashtable)vec.elementAt(i)).get("id")+"\"></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("name")+"</td>");
				akt_stand=db.getAktuellerKontostand((String)((Hashtable)vec.elementAt(i)).get("name"), formatter.format(cal.getTime()),"");
				out.println("<td><font color=\"green\">"+formater(akt_stand,3)+"</font></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("versteckt")+"</td>");
				out.println("</tr>");
				summe=summe+akt_stand;
				}
				offset+=vec.size();
					out.println("<tr>");
					out.println("<td></td><td><font size=\"5\"> Summe: </font></td><td>"+formater(summe,5)+"</td><td></td>");
					out.println("</tr>");
					out.println("</tbody>");
					out.println("</table>");
					out.println("</th>");
					gesamtsumme=gesamtsumme+summe;
					
				out.println("</td>");
				out.println("</table>");
				out.println("</form>");
				out.println("<td></td><td><font size=\"5\"> Summe gesamt: </font></td><td>"+formater(gesamtsumme,5)+"</td><td></td>");
				
				out.println("</body>");
				out.println("</html>");
				out.close();
			}

			catch (Throwable theException) {
				theException.printStackTrace();
			}
		}
		private String formater(Double d, int size) {
			String str = "";
			DecimalFormat f = new DecimalFormat("#0.00");
			if (d.doubleValue() < 0) {
				str = "<font color=\"red\" size=\""+size+"\">";
			} else {
				str = "<font color=\"green\" size=\""+size+"\">";
			}

			str = str + f.format(d);
			str = str + "</font>";
			return str;
		}

}