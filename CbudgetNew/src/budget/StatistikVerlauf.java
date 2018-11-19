package budget;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

//Aufruf http://localhost:8080/filme/MainFrame

public class StatistikVerlauf extends javax.servlet.http.HttpServlet {

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
			// FileHandling fh = new FileHandling();
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			HttpSession session = request.getSession(true);
			HeaderFooter hf = new HeaderFooter();
			// String akt_konto=(String)session.getAttribute("akt_konto");

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String akt_datum = formatter.format(cal.getTime());
			cal.set(Calendar.DAY_OF_YEAR, 1);
			String start_datum = formatter.format(cal.getTime());
			DB db = (DB)session.getAttribute("db"); 
			String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
			{
				RequestDispatcher rd;
				rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
				rd.forward(request, response);
				return;
			}	
			Hashtable settings = (Hashtable) session.getAttribute("settings");
			String mode = request.getParameter("mode");
			if (mode == null) {
				mode = "";
			}

			String startdatum = request.getParameter("startdatum");
			if (startdatum==null)
				if (settings.containsKey("VerlaufStartdatum"))
				{
					startdatum=(String)settings.get("VerlaufStartdatum");
					//System.err.println("Stardatum = "+startdatum);
				}
				else
				{							
				startdatum=start_datum;
				
				}
			settings.put("VerlaufStartdatum",startdatum);
			db.updatesetting("VerlaufStartdatum",startdatum);
			String enddatum = request.getParameter("enddatum");
			if (enddatum==null)
				if (settings.containsKey("VerlaufEnddatum"))
				{
					enddatum=(String)settings.get("VerlaufEnddatum");
				}
				else
				{
					enddatum=akt_datum;
					
				}
			settings.put("VerlaufEnddatum",enddatum);
			db.updatesetting("VerlaufEnddatum",enddatum);
			String str_konten = request.getParameter("konten");
			if (str_konten==null)
			{
				if (settings.containsKey("VerlaufKonten"))
				{
					str_konten=(String)settings.get("VerlaufKonten");
				}
				else
				{
					str_konten="";
					
				}
				
			}
			settings.put("VerlaufKonten",str_konten);
			db.updatesetting("VerlaufKonten",str_konten);
			String rule_id=request.getParameter("rule_id");
			if (rule_id==null)
			{
				if (settings.containsKey("VerlaufRuleId"))
				{
					rule_id=(String)settings.get("VerlaufRuleId");
				}
				else
				{
					rule_id="";
					
				}					
			}
			settings.put("VerlaufRuleId",rule_id);
			db.updatesetting("VerlaufRuleId",rule_id);

			Vector konten = db.getAllKonto();
			Vector rules=db.onlyValidRules(db.getAllRules());

			out.println("<html>");
			out.println("<head>");
			out.println(" <title>Statistik</title>");
			out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
			out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
			out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
			out.println("</head>");

			out.println("<body  bgcolor=\"#EEFFBB\">");
			// Vector vec=(Vector)session.getAttribute("kategorien");
			hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
			hf.writeStatisticHeader(out);
			out.println("<h1>Kontoverlauf: Bitte Auswertekriterien eingeben</h1>");
			out.println("<table>");
			out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
			out.println("<form action=auswertungverlauf?mode=verlauf method=post>");
			out.println("Regel: <select name=\"rule_id\" size=\"1\">");
			//out.println("<option>   </option>");
			String select="";
			if (rule_id.equals("-1"))
			{
				select=" selected";
			}
			else
			{
				select="";
			}
			out.println("<option"+select+" value=\"-1\"> </option>");
			
			for (int i=0;i<rules.size();i++)
			{
				//System.out.println("RULE_ID: "+((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString());
				//System.out.println("RULE_ID_: "+rule_id);
				if (((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString().equals(rule_id))
				{
					select=" selected";
				}
				else
				{
					select="";
				}
				out.println("<option"+select+" value=\""+ ((Hashtable)rules.elementAt(i)).get("rule_id") +"\">"+((Hashtable)rules.elementAt(i)).get("name")+ "</option>");
			}
			out.println("</select>");
			out.println("<p>");
			out.println("Start Datum:<br><input id=\"dob1\" name=\"startdatum\" value=\""
					+ startdatum
					+ "\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""
					+ akt_datum
					+ "\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
			out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
			out.println("</div>");
			out.println("<p>");
			out.println("End Datum:<br><input id=\"dob2\" name=\"enddatum\"  value=\""
					+ enddatum
					+ "\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""
					+ akt_datum
					+ "\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
			out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
			out.println("</div>");
			out.println("<p>");
			out.println("Konto: <select name=\"konten\" size=\"7\">");
			if (str_konten.equals("alleKonten")) {
				out.println("<option selected>alleKonten</option>");
			} else {
				out.println("<option>alleKonten</option>");
			}

			select = "";
			for (int i = 0; i < konten.size(); i++) {
				// System.out.println("Select: "+((Hashtable)kat.elementAt(i)).get("name"));
				if (((Hashtable) konten.elementAt(i)).get("name").equals(
						str_konten)) {
					select = " selected";
				} else {
					select = "";
				}
				out.println("<option" + select + ">"
						+ ((Hashtable) konten.elementAt(i)).get("name")
						+ "</option>");
			}
			out.println("</select>");
			out.println("<p>");
			out.println("<input type=\"hidden\" name=\"mode\" value=\"verlauf\">");
			out.println("<input type=\"submit\" value=\"Absenden\";>");
			out.println("</form>");
			out.println("</td><td>");

			if (mode.equals("verlauf")) {
				String wherestring = "";
				boolean first = true;
				String rule;
				if (rule_id.equals("-1"))
				{
					//dummy
					
				rule="";
				}
				else
				{
				rule=" AND "+db.getRuleCommand(new Integer(rule_id));
				}
				if (!str_konten.equals("alleKonten")) {
					wherestring = wherestring + " konto_id="
							+ db.getKontoId(str_konten);
					first = false;
				}

				if (!first) {
					wherestring = wherestring + " and ";
				}
				wherestring = wherestring + " datum >  to_date('"
						+ startdatum  + "','YYY-MM-DD') and datum <= to_date('"
						+ enddatum+"','YYY-MM-DD' )" +rule+ " order by datum";
				wherestring = " where " + wherestring;

				Double aktwert = db.getAktuellerKontostand(str_konten,
						startdatum.replaceAll("-", ""),rule);
				Vector trans = db.getAllTransaktionenWithWhere(wherestring);
				Vector chartvec = new Vector();
				for (int i = 0; i < trans.size(); i++) {

					Hashtable hash_chart = new Hashtable();
					hash_chart.put("datum", (Date) ((Hashtable) trans
							.elementAt(i)).get("datum"));
					aktwert = aktwert
							+ (Double) ((Hashtable) trans.elementAt(i))
									.get("wert");
					hash_chart.put("wert", aktwert);
					// System.out.println("Aktwert = "+aktwert
					// +" "+(String)((Hashtable)trans.elementAt(i)).get("name")+(Double)((Hashtable)trans.elementAt(i)).get("wert"));
					chartvec.addElement(hash_chart);
				}
				// System.out.println(chartvec);
				session.setAttribute("chart_vec", chartvec);

				out.println("<img src=chart?mode=verlauf width'600' height='400'>");

			}
			out.println("</td>");
			out.println("</tr>");
			out.println("</tbody>");
			out.println("</table>");
			out.println("</td></tr>");

			out.println("</table>");
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (Throwable theException) {
			theException.printStackTrace();
		}

	}

	private String formater(Double d) {
		String str = "";
		DecimalFormat f = new DecimalFormat("#0.00");
		if (d.doubleValue() < 0) {
			str = "<font color=\"red\">";
		} else {
			str = "<font color=\"green\">";
		}

		str = str + f.format(d);
		str = str + "</font>";
		return str;
	}

	private Vector replaceIdWithName(Vector katGroup, DB db) {
		for (int i = 0; i < katGroup.size(); i++) {
			Integer kat_id = (Integer) ((Hashtable) katGroup.elementAt(i))
					.get("kategorie");
			String name = db.getKategorieName(kat_id);

			((Hashtable) katGroup.elementAt(i)).put("name", name);
		}
		return katGroup;
	}

}
