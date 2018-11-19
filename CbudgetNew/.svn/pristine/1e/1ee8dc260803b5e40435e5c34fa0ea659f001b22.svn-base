package budget;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Kontouebersicht extends javax.servlet.http.HttpServlet {

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
				//out.println("<html>");
				//out.println("<body>");

				
				DB db = (DB)session.getAttribute("db"); 
				//Hashtable settings = (Hashtable) session.getAttribute("settings");
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				
				
				Vector vec= new Vector();
				vec=db.getAllKonto();
				// SystemExecute se = new SystemExecute();
				// SystemExecute.syscall_win("notepad");

				// out.println(" Rootpfad = " +rootPath);
				out.println("<html>");
				out.println("<body bgcolor=\"#EEFFBB\">");
				out.println("<h1>Wilkommen zur Kontoübersicht</h1>");
				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
			//Übersicht über die Konten
			out.println("<table border=\"1\" rules=\"groups\" bgcolor=\"#CCEECC\">");
			//out.println("<table border=\"1\">");
			out.println("<thead>");
			out.println("<tr>");
			out.println("<th>Kontoname</th>");
			out.println("<th>Kontostand</th>");
			out.println("</tr>");
			out.println("</thead>");
			out.println("<tbody>");
			out.println("<tr>");
			//Allle Konten durchgehen und eintragen
			Double summe=0.0;
			for (int i=0; i<vec.size();i++)
			{
				if (((String)((Hashtable)vec.elementAt(i)).get("versteckt")).equals("nein"))
				{
					//out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
					out.println("<td><a href=\"transaktionsverwaltung?konto="+((Hashtable)vec.elementAt(i)).get("name")+"\">"+((Hashtable)vec.elementAt(i)).get("name")+"</a></td>");
					out.println("<td>"+aktuellerKontostand(db,(Hashtable)vec.elementAt(i))+"</td>");
					out.println("</tr>");
					summe=summe+aktuellerKontostandDouble(db,(Hashtable)vec.elementAt(i));
				}
			}
			out.println("</tbody>");
			out.println("</table>");
			out.println("</form>");
			out.println("<p>");
			out.println("Summe  "+formater(summe));			
			
			out.println("</body>");
			out.println("</html>");
			out.close();
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
