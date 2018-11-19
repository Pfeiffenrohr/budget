package budget;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Abmelden extends javax.servlet.http.HttpServlet {

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
				//Vector vec=(Vector)session.getAttribute("planungen"); 
				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); 
				if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite");
					rd.forward(request, response);
					return;
				}
			  db.closeConnection();
				
				//System.out.println(settings);
			
				RequestDispatcher rd;
				session.setAttribute("auth", "notok");
				rd = getServletContext().getRequestDispatcher("/startseite?info=Abgemeldet");
				rd.forward(request, response);
				return;
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
