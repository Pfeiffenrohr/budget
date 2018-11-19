package budget;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

//Aufruf http://localhost:8080/filme/MainFrame

public class KlonePlanungEinfuegen extends javax.servlet.http.HttpServlet {

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

			DB db = (DB) session.getAttribute("db");
			if (db == null) {
				RequestDispatcher rd;
				rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
				rd.forward(request, response);
				return;
			}
			HeaderFooter hf = new HeaderFooter();
			String name = request.getParameter("Name");
			String planung = request.getParameter("planung");
			
			Vector vec= new Vector();
			vec=db.getAllPlanungen();
			Integer old_plan_id;
			Hashtable oldhash= new Hashtable();
			out.println("<html>");
			out.println("<head>");
			out.println(" <title>Planung Klonen</title>");
			
			out.println("</head>");
			out.println("<body>");		
			hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
			out.println("<p>");
			if (planung==null)
			{
				out
				.println("<font color=\"red\">!!!Bitte eine Planung zum Klonen selektieren!!!</font>");
		out.println("</body>");
		out.println("</html>");
		out.close();
			}
			for (int i=0; i<vec.size(); i++)
			{
				//System.out.println("Planung = "+planung);
				//System.out.println("Wert = "+ ((String)((Hashtable)vec.elementAt(i)).get("name")));
				if (((String)((Hashtable)vec.elementAt(i)).get("name")).equals(planung))
				{
					//System.out.println("gefunden");
					oldhash=(Hashtable)vec.elementAt(i);
				}
			}
			old_plan_id=(Integer)oldhash.get("plan_id");
			Integer new_plan_id= db.getHighestId("planung", "plan_id")+1;
			Hashtable hash= new Hashtable();
			hash.put("name", name);
			hash.put("beschreibung", oldhash.get("beschreibung"));
			hash.put("startdatum", oldhash.get("startdatum"));
			hash.put("enddatum", oldhash.get("enddatum"));
			hash.put("rule_id", oldhash.get("rule_id"));
			hash.put("plan_id", new_plan_id);
			//System.out.println("Hash = "+hash);
			db.insertPlanung(hash);
			
			//Planungsdaten für neuen Plan einfügen
			Vector plan_daten=db.getAllPlanungsDaten(old_plan_id);
			boolean fehler=false;
			for(int i=0;i<plan_daten.size();i++)
			{
				Hashtable oldValues =(Hashtable) plan_daten.elementAt(i);
				System.out.println("Hash = "+oldValues);
				if (! db.insertPlanung_daten(new_plan_id, (Integer)oldValues.get("kategorie"),oldValues.get("wert").toString() , (String)oldValues.get("absolut")))
				{
					fehler=true;
				}
			}
			
			if (! fehler)
			{
			 out.println("Planung erfolgreich geklont");
		  	out.println("<p>");
		} else {
			out
					.println("<font color=\"red\">!!!Planung konte nicht geklont werden!!!</font>");
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

				