	package budget;

	import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
	import java.util.Hashtable;
	import java.util.Vector;
	import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class Einstellungen extends javax.servlet.http.HttpServlet {

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
					//String akt_konto=(String)session.getAttribute("akt_konto");
					
				
					DB db = (DB)session.getAttribute("db"); 
					String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
					{
						RequestDispatcher rd;
						rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
						rd.forward(request, response);
						return;
					}	
					Hashtable settings = (Hashtable) session.getAttribute("settings");
					
					
					out.println("<html>");
					out.println("<head>");
					out.println(" <title>Einstellungen</title>");
					out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
					out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
					out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
					out.println("</head>");

					out.println("<body  bgcolor=\"#EEFFBB\">");
					//Vector vec=(Vector)session.getAttribute("kategorien"); 
					
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Einstellungen:</h1>");
					out.println("<table>");
					out.println("<tr><td border=\"1\" bgcolor=\"#E0FFFF\">");
					out.println("<form action=transaktionen method=post>");
					//anzMonatFuture
					out.println("Ansicht Monat Vor: <select name=\"anzMonatFuture\" size=\"1\">");
					
					String selectedString="";	
					if (settings.containsKey("anzMonatFuture"));
							{
								selectedString=(String)settings.get("anzMonatFuture");
							}
							String select="";
					for (int i=1;i<12;i++)
					{
						if (new Integer(i).toString().equals(selectedString))
						{
							select="selected";
						}
						else
						{
							select="";
						}
						out.println("<option"+select+" value=\""+i+"\">"+i+" </option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<font size=\"-2\">Dieser Wert gibt an, wieviele Monate bei der Transaktionsverwaltung im vorraus angezeigt werden.</font>");
					out.println("<p>");
					out.println("----------------------------------------------------------------");
					out.println("<p>");
					//anzMonatPast
					out.println("Ansicht Monat zurück: <select name=\"anzMonatPast\" size=\"1\">");
					if (settings.containsKey("anzMonatPast"));
							{
								selectedString=(String)settings.get("anzMonatPast");
							}
					for (int i=1;i<12;i++)
					{
						if (new Integer(i).toString().equals(selectedString))
						{
							select="selected";
						}
						else
						{
							select="";
						}
						out.println("<option"+select+" value=\""+i+"\">"+i+" </option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<font size=\"-2\">Dieser Wert gibt an, wieviele Monate bei der Transaktionsverwaltung in der Vergangenheit angezeigt werden.</font>");
					out.println("<p>");
					out.println("----------------------------------------------------------------");
					out.println("<p>");
					//anzYears
					out.println("Eintrag in Jahren im Vorraus: <select name=\"anzYearFuture\" size=\"1\">");
					if (settings.containsKey("anzYearFuture"));
							{
								selectedString=(String)settings.get("anzYearFuture");
							}
					for (int i=1;i<20;i++)
					{
						if (new Integer(i).toString().equals(selectedString))
						{
							select="selected";
						}
						else
						{
							select="";
						}
						out.println("<option"+select+" value=\""+i+"\">"+i+" </option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<font size=\"-2\">Dieser Wert gibt an, wieviele Jahre im vorraus die Eintagungen gemacht werden. </font>");
					out.println("<p>");
					out.println("----------------------------------------------------------------");
					out.println("<p>");
					
					out.println("</form>");
					out.println("</td>");
					out.println("</table>");
					out.println("</body>");
					out.println("</html>");
					out.close();
				}catch (Throwable theException) {
						theException.printStackTrace();
					}

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
			private Vector replaceIdWithName(Vector katGroup,DB db)
			{
				for (int i=0;i<katGroup.size();i++)
				{
					Integer kat_id= (Integer)((Hashtable)katGroup.elementAt(i)).get("kategorie");
					String name=db.getKategorieName(kat_id);
					
					((Hashtable)katGroup.elementAt(i)).put("name",name);
				}
				return katGroup;
			}
			private boolean ispositiv(Double wert)
			{
				if (wert>0)
				{
					return true;
				}
				else
					return false;
			}
			private String calcKategorie(Integer id ,Vector allKat)
			{
			  for (int i=0;i<allKat.size();i++)
			  {
				  if (id.intValue()==((Integer)((Hashtable)allKat.elementAt(i)).get("id")).intValue())
				  {
					  return (String)((Hashtable)allKat.elementAt(i)).get("name");
				  }
			  }
			  return "keine Kategorie";
			}
			private String calcKonto(Integer id ,Vector allKat)
			{
			  for (int i=0;i<allKat.size();i++)
			  {
				  if (id.intValue()==((Integer)((Hashtable)allKat.elementAt(i)).get("id")).intValue())
				  {
					  return (String)((Hashtable)allKat.elementAt(i)).get("name");
				  }
			  }
			  return "kein Konto";
			}
			private String chooseIcon(Integer id,Integer cycle)
			{
				if (id.intValue()==0)
				{
					return "<img src=\"icons/finished_transaction.png\" width'20' height='20'>";
				}
				else
				{
					if (cycle.intValue()==0)
					{
					return "<img src=\"icons/finished_transferal_target_transaction.png\" width'20' height='20'>";
					}
					else
					{
						if (cycle.intValue()==1)
						{
							return "<img src=\"icons/planned_transferal_source_transaction.png\" width'20' height='20'>";
						}
						else
						{
							return "<img src=\"icons/planned_transferal_target_transaction.png\" width'20' height='20'>";
						}
					}
					}
			}
			private String formatDatum(Date datum)
			{
				SimpleDateFormat formatter = new SimpleDateFormat("E dd.MM.yyyy");
				Calendar cal= Calendar.getInstance();
				cal.setTime(datum);
				System.out.println(" Cal = " + formatter.format(cal.getTime()));
				return formatter.format(cal.getTime());
			}
	}
