	package budget;

	import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
	import java.util.Hashtable;
	import java.util.Vector;
	import java.util.Enumeration;
	import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class KlonePlanung extends javax.servlet.http.HttpServlet {

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
					Calendar cal= Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String akt_datum=formatter.format(cal.getTime());
					//Vector vec=(Vector)session.getAttribute("planungen"); 
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
					
					
					out.println("<html>");
					out.println("<head>");
					out.println(" <title>Planung Klonen</title>");
					
					out.println("</head>");
					out.println("<body>");		
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Bitte Planungsdaten für Klone  eingeben</h1>");
					out.println("<form action=klonePlanungEinfuegen method=post>");
					out.println("<p>Name der neuen Planung:<br><input name=\"Name\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("Zu klonende Planung: <select name=\"planung\" size=\"3\">");
					for (int i=0;i<vec.size();i++)
					{
						out.println("<option>"+((Hashtable)vec.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<p><input type=\"submit\" value=\" Absenden \">");
					out.println("</form>");
					out.println("</body>");
					out.println("</html>");
					out.close();
				}catch (Throwable theException) {
						theException.printStackTrace();
					}

			}
	}
