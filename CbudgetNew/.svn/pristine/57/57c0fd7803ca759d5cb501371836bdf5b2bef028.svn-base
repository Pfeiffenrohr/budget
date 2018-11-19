package budget;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Startseite extends javax.servlet.http.HttpServlet {

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
				String info = request.getParameter("info");
				
				//HeaderFooter hf = new HeaderFooter();
				out.println("<html>");
				out.println("<body>");
				out.println("<h1>Bitte melden Sie sich an</h1>");
				if (info != null)
				{
					out.println("<font color=\"red\">!!!!!"+info+"!!!!!!!! </font>");
				}
				out.println("<form action=authentifizierung method=post>");
				out.println("<p>Passwort:<br><input name=\"passwort\" type=\"password\" size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("<input type=\"submit\" value=\" Absenden \">");
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
		
	