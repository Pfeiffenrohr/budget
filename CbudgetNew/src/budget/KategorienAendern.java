package budget;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class KategorienAendern extends javax.servlet.http.HttpServlet {

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
					out.println("<body>");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<p>");
					out.println("<h2>Sie haben keinen Datensatz ausgewählt</h2>");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
				Vector vec=new Vector();
				vec=(Vector)session.getAttribute("kategorien"); 
				int element = new Integer(loeschen).intValue();
				session.setAttribute("kategoriensatz", vec.elementAt(element));
				out.println("<html>");
				out.println("<body>");

				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				
				
				out.println("<h1>Bitte Kategoriedaten ändern</h1>");
				out.println("<form action=kategorienUpdaten method=post>");
				out.println("<p>Name:<br><input name=\"Name\" type=\"text\"  value=\""+ ((Hashtable)vec.elementAt(element)).get("name") +"\"size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("Mutterkategorie: <select name=\"mutterkategorie\" size=\"3\">");
				out.println("<p>");
				out.println("<option>   </option>");
				String parent=(String)((Hashtable)vec.elementAt(element)).get("parent");
				for (int i=0;i<vec.size();i++)
				{
					if (((Hashtable)vec.elementAt(i)).get("name").equals(parent))
					{
						out.println("<option selected>"+((Hashtable)vec.elementAt(i)).get("name")+"</option>");
					}
					else
					{
					out.println("<option>"+((Hashtable)vec.elementAt(i)).get("name")+"</option>");
					}
				}
				out.println("</select>");

				out.println("<p>Beschreibung:<br><input name=\"Beschreibung\" type=\"textarea\"  value=\""+ ((Hashtable)vec.elementAt(element)).get("description") +"\"size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("<p>Monatliche Obergrenze:<br><input name=\"monthlimit\" type=\"text\"  value=\""+ ((Hashtable)vec.elementAt(element)).get("monthlimit") +"\"size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("<p>Järliche Obergrenze:<br><input name=\"yearlimit\" type=\"text\"  value=\""+ ((Hashtable)vec.elementAt(element)).get("yearlimit") + "\"size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("<input type=\"radio\" name=\"art\" value=\"ausgabe\" checked> Ausgabe <br>");
				out.println("<input type=\"radio\" name=\"art\" value=\"einnahme\"> Einnahme <br>");
				out.println("<input type=\"radio\" name=\"art\" value=\"buchung\"> Buchung <br>");
				
				out.println("<p>Kategotrie ist Aktiv: <br>");
				out.println("<input type=\"radio\" name=\"active\" value=\"1\"checked> Aktiv <br>");
				out.println("<input type=\"radio\" name=\"active\" value=\"0\"> Inactive <br>");
				
				out.println("<input type=\"checkbox\" name=\"loeschen\" value=\"ja\"> Kategorie komplett löschen <br>");
				out.println("<input type=\"submit\" value=\" Absenden \">");
				out.println("</form>");

	
				//out.println("loesche ... ");
				out.println("<p>");
				
				out.println("</body>");
				out.println("</html>");
				out.close();
			}catch (Throwable theException) {
					theException.printStackTrace();
				}

		}
}
