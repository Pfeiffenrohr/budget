package budget.konten;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class KontoUpdaten extends javax.servlet.http.HttpServlet {

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
				
				Hashtable hash = (Hashtable) session.getAttribute("kontensatz");
				//Hashtable hash = new Hashtable();
				String loeschen = request.getParameter("loeschen");
				String name = request.getParameter("Name");
				String beschreibung = request.getParameter("Beschreibung");
				String versteckt = request.getParameter("versteckt");
				Integer rule_id = new Integer (request.getParameter("rule_id"));
				String aktStand = request.getParameter("newValue");
				if (aktStand.contains(",") && aktStand.contains("."))
				{
					aktStand=aktStand.replace(".", "");
				}
				aktStand=aktStand.replace(',', '.');
				if (! aktStand.equals(""))
				{
					try {
					//Hier sie Differenz ausrechnen und als Ertrag eintragen
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal= Calendar.getInstance();
					Double akt = db.getAktuellerKontostand(name, formatter.format(cal.getTime()), "");
					Double neu = new Double(aktStand);
					Double diff = neu - akt;
					DecimalFormat f = new DecimalFormat("#0.00");
					Hashtable trans = new Hashtable();
					hash.put("name","Ertrag");
					hash.put("konto",name);
					hash.put("wert",new Double(f.format(diff).replace(',','.')));
					hash.put("datum",formatter.format(cal.getTime()));
					hash.put("partner","");
					hash.put("beschreibung","");
					hash.put("partner","");
					hash.put("kategorie","Zinsen Dividenden");
					hash.put("kor_id",0);
					hash.put("cycle",0);
					hash.put("planed","n");
				
					db.insertTransaktion(hash, 0);
					}
					catch (NumberFormatException e) {
						out.println("<font color=\"red\">!!!Betrag hat nicht das richtige Format!!!</font>");
						out.println("<html>");
						out.println("<body>");
						return;
					}
				}
				
				if (versteckt==null)
				{
					versteckt="nein";
				}
				String mode = request.getParameter("mode");
				//hash.put("id",loeschen);
				hash.put("name",name);
				hash.put("description",beschreibung);
				hash.put("versteckt",versteckt.trim());
				hash.put("mode",mode);
				hash.put("rule_id",rule_id);
				HeaderFooter hf = new HeaderFooter();
				out.println("<html>");
				out.println("<body>");
				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				if (loeschen != null)
				{
					out.println("<p>");
					out.println("Konto wird gelöscht...");
					out.println("<p>");
					if (db.deleteKonto(hash))
					{
					out.println("Konto erfolgreich gelöscht");
					}
					else
					{
						out.println("<font color=\"red\">!!!Konto konte nicht gelöscht werden!!! Es gibt noch Transaktionen mit disem Konto</font>");
					}
				}
				else

				{
				out.println("<p>");
				out.println("Konto wird updatet...");
				out.println("<p>");
				if (db.updateKonto(hash))
				{
				out.println("Konto erfolgreich update");
				}
				else
				{
					out.println("<font color=\"red\">!!!Konto konte nicht update werden!!!</font>");
				}
				out.println("</body>");
				out.println("</html>");
				out.close();
				}
			}catch (Throwable theException) {
					theException.printStackTrace();
				}

		}
}

