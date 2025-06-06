	package budget.kategorie;

	import java.io.PrintWriter;
	import java.util.Hashtable;
	import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class NeueKategorie extends javax.servlet.http.HttpServlet {

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
					
					out.println("<html>");
					out.println("<body>");
					Vector vec=(Vector)session.getAttribute("kategorien"); 
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Bitte Kategoriedaten eingeben</h1>");
					out.println("<form action=kategorieEinfuegen method=post>");
					out.println("<p>Name:<br><input name=\"Name\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("Eltern Kategorie: <select name=\"mutterkategorie\" size=\"4\">");
					out.println("<option>   </option>");
					for (int i=0;i<vec.size();i++)
					{
						
						out.println("<option>"+((Hashtable)vec.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");

					out.println("<p>Beschreibung:<br><input name=\"Beschreibung\" type=\"textarea\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<p>Monatliche Obergrenze:<br><input name=\"monthlimit\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<p>J�rliche Obergrenze:<br><input name=\"yearlimit\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<input type=\"radio\" name=\"art\" value=\"ausgabe\" checked> Ausgabe <br>");
					out.println("<input type=\"radio\" name=\"art\" value=\"einnahme\"> Einnahme <br>");
					out.println("<input type=\"radio\" name=\"art\" value=\"buchung\"> Buchung <br>");
					out.println("<p>Kategotrie ist Aktiv: <br>");
					out.println("<input type=\"radio\" name=\"active\" value=\"1\" checked > Aktiv <br>");
					out.println("<input type=\"radio\" name=\"active\" value=\"0\"> Inaktiv <br>");
					out.println("<p>F�r die Kategorie soll es einen Forecast geben: <br>");
					out.println("<input type=\"radio\" name=\"forecast\" value=\"1\" checked > Ja <br>");
					out.println("<input type=\"radio\" name=\"forecast\" value=\"0\"> Nein <br>");
					out.println("<p>F�r die Kategorie soll die Inflation mitberechnet werden: <br>");
                    out.println("<input type=\"radio\" name=\"inflation\" value=\"1\" checked > Ja <br>");
                    out.println("<input type=\"radio\" name=\"inflation\" value=\"0\"> Nein <br>");

					out.println("<input type=\"submit\" value=\" Absenden \">");
					out.println("</form>");
					out.println("</body>");
					out.println("</html>");
					out.close();
				}catch (Throwable theException) {
						theException.printStackTrace();
					}

			}
	}
