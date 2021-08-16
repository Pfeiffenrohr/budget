package budget.kategorie;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;


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
					out.println("<h2>Sie haben keinen Datensatz ausgew�hlt</h2>");
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
				
				
				out.println("<h1>Bitte Kategoriedaten �ndern</h1>");
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
				out.println("<p>J�rliche Obergrenze:<br><input name=\"yearlimit\" type=\"text\"  value=\""+ ((Hashtable)vec.elementAt(element)).get("yearlimit") + "\"size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				if (((Hashtable)vec.elementAt(element)).get("mode").equals("ausgabe"))
				{
				out.println("<input type=\"radio\" name=\"art\" value=\"ausgabe\" checked> Ausgabe <br>");
				}
				else
				{	
				out.println("<input type=\"radio\" name=\"art\" value=\"ausgabe\"> Ausgabe <br>");	
				}
				if (((Hashtable)vec.elementAt(element)).get("mode").equals("einnahme"))
				{
				out.println("<input type=\"radio\" name=\"art\" value=\"einnahme\" checked> Einnahme <br>");
				}
				else
				{
				out.println("<input type=\"radio\" name=\"art\" value=\"einnahme\"> Einnahme <br>");
				}
				if (((Hashtable)vec.elementAt(element)).get("mode").equals("buchung"))
				{
				out.println("<input type=\"radio\" name=\"art\" value=\"buchung\" checked> Buchung <br>");
				}
				else
				{
					out.println("<input type=\"radio\" name=\"art\" value=\"buchung\"> Buchung <br>");
				}
				
				out.println("<p>Kategotrie ist Aktiv: <br>");
				if (((Integer)((Hashtable)vec.elementAt(element)).get("active")).intValue()==1)
				{
				out.println("<input type=\"radio\" name=\"active\" value=\"1\"checked> Aktiv <br>");
				}
				else
				{
					out.println("<input type=\"radio\" name=\"active\" value=\"1\"> Aktiv <br>");
				}
				if (((Integer)((Hashtable)vec.elementAt(element)).get("active")).intValue()==0)
				{
				out.println("<input type=\"radio\" name=\"active\" value=\"0\"checked> Inaktiv <br>");
				}
				else
				{
				out.println("<input type=\"radio\" name=\"active\" value=\"0\"> Inaktiv <br>");	
				}
				
				
				out.println("<p>F�r die Kategorie soll es einen Forecast geben: <br>");
				
				if (((Integer)((Hashtable)vec.elementAt(element)).get("forecast")).intValue()==1)
				{
				out.println("<input type=\"radio\" name=\"forecast\" value=\"1\"checked> Ja <br>");
				}
				else
				{
				out.println("<input type=\"radio\" name=\"forecast\" value=\"1\"> Ja <br>");	
				}
				if (((Integer)((Hashtable)vec.elementAt(element)).get("forecast")).intValue()==0)
				{
				out.println("<input type=\"radio\" name=\"forecast\" value=\"0\"checked> Nein <br>");
				}
				else
				{
				out.println("<input type=\"radio\" name=\"forecast\" value=\"0\"> Nein <br>");
				}
				
                out.println("<p>Soll die Infaltionsrate im Forecast berechnet werden?: <br>");
                
                if (((Integer)((Hashtable)vec.elementAt(element)).get("inflation")).intValue()==1)
                {
                out.println("<input type=\"radio\" name=\"inflation\" value=\"1\"checked> Ja <br>");
                }
                else
                {
                out.println("<input type=\"radio\" name=\"inflation\" value=\"1\"> Ja <br>");    
                }
                if (((Integer)((Hashtable)vec.elementAt(element)).get("inflation")).intValue()==0)
                {
                out.println("<input type=\"radio\" name=\"inflation\" value=\"0\"checked> Nein <br>");
                }
                else
                {
                out.println("<input type=\"radio\" name=\"inflation\" value=\"0\"> Nein <br>");
                }
				out.println("<p>Kategorie l�schen: <br>");
				out.println("<input type=\"checkbox\" name=\"loeschen\" value=\"ja\"> Kategorie komplett l�schen <br>");
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
