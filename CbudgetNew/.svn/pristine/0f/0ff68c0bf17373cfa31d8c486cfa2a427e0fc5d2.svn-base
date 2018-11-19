	package budget;

	import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
	import java.util.Hashtable;
	import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class NeueZyklischeTransaktion extends javax.servlet.http.HttpServlet {

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
					String id = request.getParameter("trans");
					String vec_id = request.getParameter("vec_id");
					String kor_id = request.getParameter("kor_id");
					String akt_konto=(String)session.getAttribute("akt_konto");
					session.setAttribute("transid",id);
					Vector trans=(Vector)session.getAttribute("transaktionen");
					Integer int_vec_id= new Integer(vec_id);
					session.setAttribute("int_vec_id",int_vec_id);
					Calendar cal= Calendar.getInstance();
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat formatterDB = new SimpleDateFormat("yyyyMMdd");
					String akt_datum=formatter.format(cal.getTime());
					DB db = (DB)session.getAttribute("db"); 
					String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
					{
						RequestDispatcher rd;
						rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
						rd.forward(request, response);
						return;
					}
					Hashtable hash = db.getCycleTransaktionen(new Integer(kor_id));
					int anz_eintrag=0;
					int anz_geplant=0;
					if (hash==null)
					{
						System.out.println("hash = null");
						hash=new Hashtable();
						hash.put("name", "");
						hash.put("korid","0");
						hash.put("end_datum",akt_datum);
						hash.put("wiederholung","");
						hash.put("noend","nein");
						hash.put("delta",1);
						hash.put("begin_datum",akt_datum);
						
						
					}
					else
					{
						hash.put("begin_datum",formatter.format((Date)((Hashtable)db.getFirstCycleTransaktion(new Integer(kor_id))).get("datum")));
						anz_eintrag=db.getAnzCycleEintrag(kor_id,formatterDB.format(cal.getTime()),true);
						anz_geplant=db.getAnzCycleEintrag(kor_id,formatterDB.format(cal.getTime()),false);
					}
					
					out.println("<html>");
					out.println("<head>");
					out.println(" <title>Date Chooser</title>");
					out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
					out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
					out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
					out.println("</head>");
					out.println("<body>");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Bitte Neue zyklische Transaktionsdaten für "+akt_konto+ " eingeben</h1>");
					out.println("<form action=zyklischeTransaktionEinfuegen method=post>");
					out.println("<p>Name:<br><input name=\"Name\" type=\"text\" size=\"40\" maxlength=\"50\" value=\""+hash.get("name")+"\"></p>");
					out.println("<p>");
					out.println("Erste Transaktion: "+hash.get("begin_datum"));
					out.println("<p>");
					out.println("Schon eingetragene Transaktionen: "+anz_eintrag);
					out.println("<p>");
					out.println("Geplante Transaktionen: "+anz_geplant);
					out.println("<p>");
					//out.println("<p>Datum: yyyy-mm-dd <br><input name=\"wert\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("End Datum:<br><input id=\"dob\" name=\"dob\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+hash.get("end_datum")+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob', 'chooserSpan', 2005, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					out.println("<p>");
					String select ="";
					if ( ((String) hash.get("noend")).equals("ja") )
					{
						select=" checked ";
					}
					out.println("<input type=\"checkbox\" name=\"endlos\" value=\"ja\" "+select+"> Kein Enddatum <br>");
					out.println("<p>");
					out.println("Wiederhole: <select name=\"repeat\" size=\"7\">");
					select ="";
					if (((String)hash.get("wiederholung")).equals("taeglich"))
					{
						select="selected";
					}
					out.println("<option "+select+">taeglich</option>");
					select ="";
					if (((String)hash.get("wiederholung")).equals("woechentlich"))
					{
						select="selected";
					}
					out.println("<option "+select+">woechentlich</option>");
					select ="";
					if (((String)hash.get("wiederholung")).equals("monatlich"))
					{
						select="selected";
					}
					out.println("<option "+select+">monatlich</option>");
					select ="";
					if (((String)hash.get("wiederholung")).equals("jaehrlich"))
					{
						select="selected";
					}
					out.println("<option "+select+">jaehrlich</option>");
					out.println("</select>");
					out.println("<p>");
					out.println("Abstand Tage/Monat/Jahr: <select name=\"delta\" size=\"7\">");
					for (int i=1; i<7; i++)
					{
					   if (((Integer)hash.get("delta")).intValue()==i)
					   {
						   out.println("<option selected>"+i+"</option>");
					   }
					   else
					   {
						   out.println("<option>"+i+"</option>");
					   }   
						
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<input type=\"hidden\" name=\"kor_id\" value=\""+hash.get("korid")+"\">");
					out.println("<input type=\"submit\" value=\" Absenden \">");
					out.println("</form>");
					out.println("</body>");
					out.println("</html>");
					out.close();
				}catch (Throwable theException) {
						theException.printStackTrace();
					}

			}
			private String formatDatum(Date datum)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal= Calendar.getInstance();
				cal.setTime(datum);
				System.out.println(" Cal = " + formatter.format(cal.getTime()));
				return formatter.format(cal.getTime());
			}
	}
