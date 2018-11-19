package budget;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Authentifizierung extends javax.servlet.http.HttpServlet {

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
				//HeaderFooter hf = new HeaderFooter();
				String passwort = request.getParameter("passwort");
				ServletContext context = getServletContext();
				String DBusername = context.getInitParameter("DBusername");
				String DBuserpassword = context.getInitParameter("DBuserpassword");
				String DBconnectString = context.getInitParameter("DBconnectstring");
				
			DB db = (DB) session.getAttribute("db");
			Hashtable settings = (Hashtable) session.getAttribute("settings");
			if (db == null || db.con.isClosed()) {
				db = new DB();
				
				//db.dataBaseConnect(DBusername);
				if (!db.dataBaseConnect(DBusername,DBuserpassword,DBconnectString)) {
					System.err.println("Error, can not connest to database");
					out.println("can not connect to database");
					return;

				}
				session.setAttribute("db", db);
				settings = db.getSettings();
				//System.out.println(settings);
				session.setAttribute("settings", settings);

			}
			
			if (db.authenticate(passwort))
					{
				RequestDispatcher rd;
				session.setAttribute("auth", "ok");
				UpdateZyklischeTransaktion uz = new UpdateZyklischeTransaktion();
				out.println("<html>");
				out.println("<body>");
				if (uz.update(db,out,settings))
				{
					
					out.println("<form action=kontouebersicht method=post>");
					
					out.println("<input type=\"submit\" value=\" Weiter.. \">");
					out.println("</form>");
				}
				else
				{
				rd = getServletContext().getRequestDispatcher("/kontouebersicht");
				rd.forward(request, response);
				return;
				}
				out.println("</body>");
				out.println("</html>");
				}
			else
			{
				RequestDispatcher rd;
				rd = getServletContext().getRequestDispatcher("/startseite?info=Falsches Passwort");
				rd.forward(request, response);
				return;
			}
				
		//out.close();
		}
			
			catch (Throwable theException) {
				theException.printStackTrace();
			}
		}
		
		private String aktuellerKontostand(DB db,Hashtable hash){
			
			Calendar cal= Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String datum=formatter.format(cal.getTime());
			return formater(db.getAktuellerKontostand((String)hash.get("name"),datum,""));
		}
		private double aktuellerKontostandDouble(DB db,Hashtable hash){
			
			Calendar cal= Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String datum=formatter.format(cal.getTime());
			return db.getAktuellerKontostand((String)hash.get("name"),datum,"");
		}
		
		private String formater(Double d)
		{
			String str="";
			DecimalFormat f = new DecimalFormat("#0.00");
			if (d.doubleValue()<0)
			{
				str="<font color=\"red\">";
			}
			else
			{
				str="<font color=\"green\">";
			}
			
			str=str+f.format(d);
			str=str+"</font>";
			return str;
		}
		

}
