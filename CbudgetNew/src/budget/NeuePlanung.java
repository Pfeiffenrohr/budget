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

		public class NeuePlanung extends javax.servlet.http.HttpServlet {

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
					String auth=(String)session.getAttribute("auth"); 
					if (db==null || ! auth.equals("ok") )
					{
						RequestDispatcher rd;
						rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
						rd.forward(request, response);
						return;
					}
					Vector allAusgaben=db.getAllKategorien("ausgabe");
					Vector allEinnahmen=db.getAllKategorien("einnahme");
					Vector rules=db.onlyValidRules(db.getAllRules());
					session.setAttribute("allAusgaben",allAusgaben);
					session.setAttribute("allEinnahmen",allEinnahmen);
					out.println("<html>");
					out.println("<head>");
					out.println(" <title>Planungen</title>");
					out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
					out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
					out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
					out.println("</head>");
					out.println("<body>");
					
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Bitte Planungsdaten eingeben</h1>");
					out.println("<form action=planungEinfuegen method=post>");
					out.println("<p>Name:<br><input name=\"Name\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					
					out.println("<p>Beschreibung:<br><textarea name=\"beschreibung\" cols=\"50\" rows=\"10\"></textarea>");
					out.println("<p>");
					out.println("Start Datum:<br><input id=\"dob1\" name=\"startdatum\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+akt_datum+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2000, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					out.println("<p>");
					out.println("Start Datum:<br><input id=\"dob2\" name=\"enddatum\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+akt_datum+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2000, 2050, 'Y-m-d', false);\"/>");
					out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
					out.println("</div>");
					//out.println("<option>   </option>");
					String select="";
					out.println("Regel: <select name=\"rule_id\" size=\"1\">");
					out.println("<option"+select+" value=\"-1\"> </option>");
					
					for (int i=0;i<rules.size();i++)
					{
						//System.out.println("RULE_ID: "+((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString());
						out.println("<option"+select+" value=\""+ ((Hashtable)rules.elementAt(i)).get("rule_id") +"\">"+((Hashtable)rules.elementAt(i)).get("name")+ "</option>");
					}
					out.println("</select>");
					out.println("<p>");
					out.println("<p><h2>Ausgaben:</h2>");
					out.println("<p>");
					for (int i=0;i<allAusgaben.size();i++)
					{
						Vector vec_subs= new Vector();
						hf.searchSub(vec_subs, (String)((Hashtable)allAusgaben.elementAt(i)).get("name"),allAusgaben, 0);
						String subs="";
						boolean found=false;
						for (int j=0;j<vec_subs.size();j++)
						{
							subs=subs+ (String)vec_subs.elementAt(j)+" ";
							found=true;
						}
						if (found)
						{
							subs="(außer "+subs+")";
						}
						
						
						out.println("<p>"+((Hashtable)allAusgaben.elementAt(i)).get("name")+": "+subs+ "<br><input name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+ "\" type=\"text\" size=\"40\" maxlength=\"50\">");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+"_radio\" value=\"relativ\" checked> Relativ ");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+"_radio\" value=\"absolut\"> Absolut <br></p>");
					}
					out.println("<p>");
					out.println("<p><h2>Einnahmen:</h2>");
					out.println("<p>");
					for (int i=0;i<allEinnahmen.size();i++)
					{
						Vector vec_subs= new Vector();
						hf.searchSub(vec_subs, (String)((Hashtable)allEinnahmen.elementAt(i)).get("name"),allEinnahmen, 0);
						String subs="";
						boolean found=false;
						for (int j=0;j<vec_subs.size();j++)
						{
							subs=subs+ (String)vec_subs.elementAt(j)+" ";
							found=true;
						}
						if (found)
						{
							subs="(außer "+subs+")";
						}
						
					out.println("<p>"+((Hashtable)allEinnahmen.elementAt(i)).get("name")+ ": "+subs+ "<br><input name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+ "\" type=\"text\" size=\"40\" maxlength=\"50\">");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+"_radio\" value=\"relativ\" checked> Relativ ");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+"_radio\" value=\"absolut\"> Absolut <br></p>");
					}
					out.println("<p>");
					out.println("<input type=\"checkbox\" name=\"batch\" value=\"ja\">Planung im Batch berechnen <br>");
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
