	package budget;

	import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
	import java.util.Hashtable;
	import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class NeueTransaktion extends javax.servlet.http.HttpServlet {

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
					String akt_konto=(String)session.getAttribute("akt_konto");
					
					Calendar cal= Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					String akt_datum=formatter.format(cal.getTime());
					
					
					out.println("<html>");
					out.println("<head>");
					out.println(" <title>Date Chooser</title>");
					out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
					out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
					out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
					out.println("</head>");

					out.println("<body  bgcolor=\"#EEFFBB\">");
					Vector vec=(Vector)session.getAttribute("kategorien"); 
					Vector konten=(Vector)session.getAttribute("konten");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Bitte Transaktionsdaten für "+akt_konto+ " eingeben</h1>");
					out.println("<form action=transaktionEinfuegen method=post>");
					out.println("<p>Name:<br><input name=\"Name\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<p>Betrag:<br><input name=\"wert\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<p>Beschreibung:<br><textarea name=\"beschreibung\" cols=\"50\" rows=\"5\"></textarea>");
					out.println("<p>");
					//out.println("<p>Datum: yyyy-mm-dd <br><input name=\"wert\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("Datum:<br><input id=\"dob\" name=\"dob\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+akt_datum+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					
					out.println("<p>");
					out.println("Kategorie: <select name=\"kategorie\" size=\"7\">");
					//out.println("<option>   </option>");
					for (int i=0;i<vec.size();i++)
					{
						
						out.println("<option>"+((Hashtable)vec.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");

					out.println("<p>Partner:<br><input name=\"partner\" type=\"textarea\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<input type=\"checkbox\" name=\"korkonto\" value=\"ja\"> Transaktion auf anderes Konto <br>");
					out.println("<p>");
					out.println("Konten: <select name=\"kor_konto\" size=\"7\">");
					//out.println("<option>   </option>");
					for (int i=0;i<konten.size();i++)
					{
						
						out.println("<option>"+((Hashtable)konten.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<input type=\"checkbox\" name=\"planung\" value=\"j\"> Transaktion nur zur Planung <br>");
					out.println("<input type=\"submit\" value=\" Absenden \"onClick=\"ists_zahl(Betrag.value)\";>");
					out.println("</form>");
					out.println("</body>");
					out.println("</html>");
					out.close();
				}catch (Throwable theException) {
						theException.printStackTrace();
					}

			}
	}
