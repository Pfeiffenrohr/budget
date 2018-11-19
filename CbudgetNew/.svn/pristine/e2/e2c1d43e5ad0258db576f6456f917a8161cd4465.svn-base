package budget;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Planungsverwaltung extends javax.servlet.http.HttpServlet {

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
				
				//Konten einlesen

				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				Vector vec= new Vector();
				vec=db.getAllPlanungen();
				session.setAttribute("planungen", vec);

				out.println("<html>");
				out.println("<body bgcolor=\"#EEFFBB\">");
				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<h1>Planungsverwaltung</h1>");
				out.println("<table cellpadding=\"0\" cellspacing=\"4\" width=\"114\" border=\"0\">");
				out.println("<tr><td>");
				out.println("<form action=\"neuePlanung\">");
				out.println("<input type=\"submit\" name=\"Text 1\" value=\"Neue Planung anlegen\">");
				out.println("</form>");
				out.println("</td><td>");
				out.println("<form action=\"klonePlanung\">");
				out.println("<input type=\"submit\" name=\"action\" value=\"Planung klonen\">");
				out.println("</td></form>");
				out.println("<td>");
				out.println("<form action=\"planungAendern\">");
				out.println("<input type=\"submit\" name=\"Text 2\" value=\"Planung ändern/löschen\">");
				out.println("</td></tr>");
				out.println("</table>");
				out.println("</p>");
				
//Übersicht über die Kategorien
				out.println("<table border=\"1\" bgcolor=\"#CCEECC\">");
				//out.println("<table border=\"1\">");
				out.println("<thead>");
				out.println("<tr>");
				out.println("<th></th>");
				out.println("<th>Name</th>");
				out.println("<th>Ausgaben</th>");
				out.println("<th>Einnahmen</th>");
				out.println("<th>Planung Stand heute</th>");
				out.println("<th>Planung Stand Montsende</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				out.println("<tr>");
				//Allle Konten durchgehen und eintragen
				for (int i=0; i<vec.size();i++)
				{
				//out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
			     out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+new Integer(i).toString()+"\"></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("name")+"</td>");
				
				out.println("<td>"+db.getPlanung_daten_wert((Integer)((Hashtable)vec.elementAt(i)).get("plan_id"),99998)+"</td>");
				out.println("<td>"+db.getPlanung_daten_wert((Integer)((Hashtable)vec.elementAt(i)).get("plan_id"),99999)+"</td>");
				out.println("<td><a href='zeigePlanung?zeit=tag&plan="+((Integer)((Hashtable)vec.elementAt(i)).get("plan_id"))+"'>anzeigen</a></td>");
				out.println("<td><a href='zeigePlanung?zeit=monat&plan="+((Integer)((Hashtable)vec.elementAt(i)).get("plan_id"))+"'>anzeigen</a></td>");
				//out.println("<td><font color=\"green\">1896.56</font></td>");
				out.println("</tr>");
				}
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

}