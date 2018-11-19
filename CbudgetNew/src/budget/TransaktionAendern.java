package budget;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class TransaktionAendern extends javax.servlet.http.HttpServlet {

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
				
				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				HeaderFooter hf = new HeaderFooter();
				String loeschen = request.getParameter("loeschen");
				
				if (loeschen == null)
				{
					loeschen="nein";
					out.println("<html>");
					out.println("<head>");
					out.println("<title>Transaktion aendern</title>");
					
					
					out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
					out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
					out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
					out.println("</head>");
					out.println("<body>");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<p>");
					out.println("<h2>Sie haben keinen Datensatz ausgewählt</h2>");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
				Vector kat=new Vector();
				Vector konten = new Vector();
				Vector transaktionen = new Vector();
				
				kat=(Vector)session.getAttribute("kategorien");
				konten=(Vector)session.getAttribute("konten");
				transaktionen=(Vector)session.getAttribute("transaktionen");
				
				int element = new Integer(loeschen).intValue();
				//session.setAttribute("transaktionensatz", .elementAt(element));
				Hashtable trans =(Hashtable)transaktionen.elementAt(element);
				session.setAttribute("transaktionensatz",trans);
				
				out.println("<html>");
				out.println("<body  bgcolor=\"#EEFFBB\">");

				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				out.println("<table border=\"0\">");
				out.println("<tr><td>");
				
				out.println("<h1>Bitte Transaktion ändern</h1>");
				out.println("<form action=transaktionenUpdaten method=post>");
				out.println("<p>Name:<br><input name=\"Name\" type=\"text\" size=\"40\" value=\""+trans.get("name")+"\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("<p>Betrag:<br><input name=\"wert\" type=\"text\" size=\"40\"  value=\""+trans.get("wert")+"\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("<p>Beschreibung:<br><textarea name=\"beschreibung\" cols=\"50\" rows=\"5\" >"+trans.get("beschreibung")+"</textarea>");
				out.println("<p>");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				//out.println("<p>Datum: yyyy-mm-dd <br><input name=\"wert\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
				out.println("Datum:<br><input id=\"dob\" name=\"dob\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatter.format(trans.get("datum"))+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
				out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
				out.println("</div>");
				
				out.println("<p>");
				out.println("Kategorie: <select name=\"kategorie\" size=\"7\">");
				//out.println("<option>   </option>");
				String sel_kat=db.getKategorieName((Integer)trans.get("kategorie"));
				for (int i=0;i<kat.size();i++)
				{
					if (((Hashtable)kat.elementAt(i)).get("name").equals(sel_kat))
							{
						out.println("<option selected>"+((Hashtable)kat.elementAt(i)).get("name")+"</option>");
							}
						else
						{
							out.println("<option>"+((Hashtable)kat.elementAt(i)).get("name")+"</option>");
						}
				}
				out.println("</select>");

				out.println("<p>Partner:<br><input name=\"partner\" type=\"text\" size=\"40\" value=\""+trans.get("partner")+"\"  maxlength=\"50\"></p>");
				out.println("<p>");
				String kontoname=db.getKontoName((Integer)trans.get("konto"));
				//brauchen wir in der session
				session.setAttribute("akt_konto",kontoname);
				//System.err.println("Konto= "+kontoname);
				if (((Integer)trans.get("cycle")).intValue()==2 || (((Integer)trans.get("cycle")).intValue()== 0) && ((Integer)trans.get("kor_id")).intValue()!= 0)
				{
				out.println("<input type=\"checkbox\" name=\"korkonto\" value=\"ja\" checked> Transaktion auf anderes Konto <br>");
				out.println("<p>");
				out.println("Konten: <select name=\"kor_konto\" size=\"7\">");
				//out.println("<option>   </option>");
				//Hier muß das korespondierende konto gesucht werden
				//String kontoname=db.getKontoName((Integer)trans.get("konto"));
				//brauchen wir in der session
				//session.setAttribute("akt_konto",kontoname);
				//System.err.println("Konto= "+kontoname);
				String sel_konto=db.getKorKonto(kontoname,(Integer)trans.get("kor_id"));
				//String sel_konto=db.getKontoName((Integer)trans.get("konto"));
				for (int i=0;i<konten.size();i++)
				{
					if (((Hashtable)konten.elementAt(i)).get("name").equals(sel_konto))
					{
					out.println("<option selected>"+((Hashtable)konten.elementAt(i)).get("name")+"</option>");
					}
					else
					{
						out.println("<option>"+((Hashtable)konten.elementAt(i)).get("name")+"</option>");
					}
					}
				out.println("</select>");
				out.println("<p>");
				}
				out.println("<input type=\"checkbox\" name=\"loeschen\" value=\"ja\"> Transaktion komplett löschen <br>");
				if (((Integer)trans.get("cycle")).intValue()>0 )
				{
					out.println("<p>");
					out.println("<input type=\"radio\" name=\"anwenden\" value=\"einzeln\"checked > Nur auf diese Transaktionen anwenden<br>");
					out.println("<input type=\"radio\" name=\"anwenden\" value=\"alle\"> Auf alle anwenden <br>");
					out.println("<input type=\"radio\" name=\"anwenden\" value=\"folgend\"> Auf diese und folgende Transaktionen anwenden<br>");
					out.println("<input type=\"radio\" name=\"anwenden\" value=\"zuvor\"> Auf diese und zurückliegende Transaktionen anwenden <br>");
					
				    out.println("<p>");
				}
				if (((String)trans.get("planed")).equals("j"))
				{
				out.println("<input type=\"checkbox\" name=\"planung\" value=\"j\" checked> Transaktion nur zur Planung <br>");
				}
				else
				{
					out.println("<input type=\"checkbox\" name=\"planung\" value=\"j\"> Transaktion nur zur Planung <br>");
				}
				
				out.println("<input type=\"submit\" value=\" Absenden \">");
				out.println("</form>");

	
				//out.println("loesche ... ");
				out.println("<p>");
				out.println("</td><td valign=\"top\">");
				Vector hist = db.getTransaktionHistorie(((Integer)trans.get("id")).toString());
				Calendar cal= Calendar.getInstance();
				SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
				//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				out.println("<h2>Historie:</h2>");
				out.println("<table border=1  bgcolor=\"#CCEECC\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th></th>");
				out.println("<th>Datum</th>");
				out.println("<th>Uhrzeit</th>");
				out.println("<th>User</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				for (int i=0; i<hist.size();i++)
				{
					out.println("<tr>");
					out.println("<td>");
					if ( ((Integer)((Hashtable)hist.elementAt(i)).get("type")).intValue()==0 )
					{
						out.println("Angelegt am");
					}
					else
					{
						out.println("Verändert am");
					}
					out.println("</td><td>");
				cal.setTime((Date)((Hashtable)hist.elementAt(i)).get("datum"));
				String datum=formatter.format(cal.getTime());
				out.println(formatter.format(cal.getTime()));
				out.println("</td><td>");
				cal.setTime((Date)((Hashtable)hist.elementAt(i)).get("zeit"));
				out.println(formatTime.format(cal.getTime()));
				out.println("</td><td>");
				out.println((String)((Hashtable)hist.elementAt(i)).get("user"));
				out.println("</td></tr>");
				}
				out.println("</table>");
				//Ende rechte Tabelle
				
				out.println("</td></tr>");
				out.println("</table>"); 
				//Ende Haupttabelle
				out.println("</body>");
				out.println("</html>");
				out.close();
			}catch (Throwable theException) {
					theException.printStackTrace();
				}

		}
}
