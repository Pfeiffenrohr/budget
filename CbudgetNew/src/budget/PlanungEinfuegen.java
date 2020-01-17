package budget;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

//Aufruf http://localhost:8080/filme/MainFrame

public class PlanungEinfuegen extends javax.servlet.http.HttpServlet {

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
			
			DB db = (DB)session.getAttribute("db"); 
			String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
			{
				RequestDispatcher rd;
				rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
				rd.forward(request, response);
				return;
			}
			Vector allAusgaben = (Vector) session.getAttribute("allAusgaben");
			Vector allEinnahmen = (Vector) session.getAttribute("allEinnahmen");

			String name = request.getParameter("Name");
			String beschreibung = request.getParameter("beschreibung");
			String startdatum = request.getParameter("startdatum");
			String enddatum = request.getParameter("enddatum");
			String batch = request.getParameter("batch");
			String rule_id = request.getParameter("rule_id");
			if (batch==null)
			{
				batch="nein";
			}
			// String wert=request.getParameter("wert");

			Hashtable hash = new Hashtable();
			hash.put("name", name);
			hash.put("beschreibung", beschreibung);
			hash.put("startdatum", startdatum);
			hash.put("enddatum", enddatum);
			hash.put("plan_id", db.getHighestId("planung", "plan_id") + 1);
			hash.put("batch",batch);
			hash.put("rule_id",new Integer(rule_id));

			for (int i = 0; i < allAusgaben.size(); i++) {

				hash.put((String) ((Hashtable) allAusgaben.elementAt(i))
						.get("name"), request
						.getParameter((String) ((Hashtable) allAusgaben
								.elementAt(i)).get("name")));

				hash.put((String) ((Hashtable) allAusgaben.elementAt(i))
						.get("name")
						+ "_radio", request
						.getParameter((String) ((Hashtable) allAusgaben
								.elementAt(i)).get("name")
								+ "_radio"));

				if (hash.get(
						(String) ((Hashtable) allAusgaben.elementAt(i))
								.get("name")).equals("")) {
					hash.put((String) ((Hashtable) allAusgaben.elementAt(i))
							.get("name"), "0.0");
				}
			}
			for (int i = 0; i < allEinnahmen.size(); i++) {
				hash.put(((Hashtable) allEinnahmen.elementAt(i)).get("name"),
						request.getParameter((String) ((Hashtable) allEinnahmen
								.elementAt(i)).get("name")));
				hash.put(((Hashtable) allEinnahmen.elementAt(i)).get("name")+"_radio",
						request.getParameter((String) ((Hashtable) allEinnahmen
								.elementAt(i)).get("name")+"_radio"));
				if (hash.get((String) ((Hashtable) allEinnahmen.elementAt(i))
						.get("name")).equals(""))
				{
					hash.put((String) ((Hashtable) allEinnahmen.elementAt(i))
							.get("name"),"0.0");
				}
			}
			System.out.println(hash);
			HeaderFooter hf = new HeaderFooter();
			out.println("<html>");
			out.println("<body>");

			hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
			out.println("<p>");
			out.println("Planung wird erstellt...");
			out.println("<p>");

			for (int i = 0; i < allAusgaben.size(); i++) {
				if (!checkfloat((String) hash.get(((Hashtable) allAusgaben
						.elementAt(i)).get("name")))) {
					out
							.println("<font color=\"red\">!!!Planung konte nicht erstellt werden!!!</font>");
					out.println("<p>");
					out.println("Wert hat falsches format!!");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
			}
			for (int i = 0; i < allEinnahmen.size(); i++) {
				if (!checkfloat((String) hash.get(((Hashtable) allEinnahmen
						.elementAt(i)).get("name")))) {
					out
							.println("<font color=\"red\">!!!Planung konte nicht erstellt werden!!!</font>");
					out.println("<p>");
					out.println("Wert hat falsches format!!");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
			}
			if (db.insertPlanung(hash)) {
				out.println("Planung erfolgreich erstellt");
			} else {
				out
						.println("<font color=\"red\">!!!Planung konte nicht erstellt werden!!!</font>");
			}
			Double summe = 0.0;
			for (int i = 0; i < allAusgaben.size(); i++) {
				if (db.insertPlanung_daten((Integer) hash.get("plan_id"),
						(Integer) ((Hashtable) allAusgaben.elementAt(i))
								.get("id"), (String) hash
								.get(((Hashtable) allAusgaben.elementAt(i))
										.get("name")),(String) hash
										.get(((Hashtable) allAusgaben.elementAt(i))
												.get("name")+"_radio"))) {
					out.println("Planung erfolgreich erstellt");
					out.println("<p>");
				} else {
					out
							.println("<font color=\"red\">!!!Planung konte nicht erstellt werden!!!</font>");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
				summe=summe+new Double((String) hash
								.get(((Hashtable) allAusgaben.elementAt(i))
										.get("name")));

			}
			//Alle Ausgben eintragen
			if (db.insertPlanung_daten((Integer) hash.get("plan_id"),99998,summe.toString(),"relativ" )) {
				out.println("Alle Ausgben erfolgreich erstellt");
				out.println("<p>");
			} else {
				out
						.println("<font color=\"red\">!!!Alle Ausgaben konte nicht erstellt werden!!!</font>");
				out.println("</body>");
				out.println("</html>");
				out.close();
				return;
			}
			summe=0.0;
			for (int i = 0; i < allEinnahmen.size(); i++) {
				if (db.insertPlanung_daten((Integer) hash.get("plan_id"),
						(Integer) ((Hashtable) allEinnahmen.elementAt(i))
								.get("id"), (String) hash
								.get(((Hashtable) allEinnahmen.elementAt(i))
										.get("name")),(String) hash
										.get(((Hashtable) allEinnahmen.elementAt(i))
												.get("name")+"_radio"))) {
					out.println("Planung erfolgreich erstellt");
				} else {
					out
							.println("<font color=\"red\">!!!Planung konte nicht erstellt werden!!!</font>");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
				summe=summe+new Double((String) hash
						.get(((Hashtable) allEinnahmen.elementAt(i))
								.get("name")));
			}
			//Alle Einnahmen eintragen
			if (db.insertPlanung_daten((Integer) hash.get("plan_id"),99999,summe.toString() ,"relativ")) {
				out.println("Alle Ausgben erfolgreich erstellt");
				out.println("<p>");
			} else {
				out
						.println("<font color=\"red\">!!!Alle Ausgaben konte nicht erstellt werden!!!</font>");
				out.println("</body>");
				out.println("</html>");
				out.close();
				return;
			}
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (Throwable theException) {
			theException.printStackTrace();
		}

	}

	public boolean checkfloat(String str) {
		try {
			Float fl = new Float(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}

				