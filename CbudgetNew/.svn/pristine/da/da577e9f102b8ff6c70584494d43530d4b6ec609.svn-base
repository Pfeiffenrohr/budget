package budget;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class PlanungAendern extends javax.servlet.http.HttpServlet {

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
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");			
				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				HeaderFooter hf = new HeaderFooter();
				String loeschen = request.getParameter("loeschen");
				
				if (loeschen == null)
				{
					loeschen="nein";
					out.println("<html>");
					out.println("<body>");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<p>");
					out.println("<h2>Sie haben keinen Datensatz ausgewählt</h2>");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
				Vector allAusgaben=db.getAllKategorien("ausgabe");
				Vector allEinnahmen=db.getAllKategorien("einnahme");
				session.setAttribute("allAusgaben",allAusgaben);
				session.setAttribute("allEinnahmen",allEinnahmen);
				Vector vec=new Vector();
				vec=(Vector)session.getAttribute("planungen"); 
				Vector rules=db.getAllRules();
				int element = new Integer(loeschen).intValue();
				Hashtable hash = (Hashtable)vec.elementAt(element);
				session.setAttribute("planungssatz",hash);
				out.println("<html>");
				out.println("<head>");
				out.println(" <title>Planungen</title>");
				out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
				out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
				out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
				out.println("</head>");
				out.println("<body>");

				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				out.println("<h1>Bitte Planungsdaten eingeben</h1>");
				out.println("<form action=planungUpdaten method=post>");
				out.println("<p>Name:<br><input name=\"Name\" type=\"text\" value=\""+hash.get("name")+"\" size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				System.out.println(hash);
				out.println("<p>Beschreibung:<br><textarea name=\"beschreibung\" cols=\"50\" rows=\"10\" >"+hash.get("beschreibung")+"</textarea>");
				out.println("<p>");
				out.println("Start Datum:<br><input id=\"dob1\" name=\"startdatum\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatter.format(hash.get("startdatum"))+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2000, 2050, 'Y-m-d', false);\"/>");
				out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
				out.println("</div>");
				out.println("<p>");
				out.println("End Datum:<br><input id=\"dob2\" name=\"enddatum\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatter.format(hash.get("enddatum"))+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2000, 2050, 'Y-m-d', false);\"/>");
				out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
				out.println("</div>");
				out.println("Regel: <select name=\"rule_id\" size=\"1\">");
				//out.println("<option>   </option>");
				String select="";
				String rule_id=((Integer)hash.get("rule_id")).toString();
				if (rule_id.equals("-1"))
				{
					select=" selected";
				}
				else
				{
					select="";
				}
				out.println("<option"+select+" value=\"-1\"> </option>");
				
				for (int i=0;i<rules.size();i++)
				{
					//System.out.println("RULE_ID: "+((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString());
					//System.out.println("RULE_ID_: "+rule_id);
					if (((Integer)((Hashtable)rules.elementAt(i)).get("rule_id")).toString().equals(rule_id))
					{
						select=" selected";
					}
					else
					{
						select="";
					}
					out.println("<option"+select+" value=\""+ ((Hashtable)rules.elementAt(i)).get("rule_id") +"\">"+((Hashtable)rules.elementAt(i)).get("name")+ "</option>");
				}
				out.println("</select>");
				out.println("<p>");
				out.println("<p><h2>Ausgaben:</h2>");
				out.println("<p>");
				Double value;
				String absolute;
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
					
				value=db.getPlanung_daten_wert((Integer)hash.get("plan_id"),(Integer)((Hashtable)allAusgaben.elementAt(i)).get("id"));
				absolute=db.getPlanung_daten_absolut((Integer)hash.get("plan_id"),(Integer)((Hashtable)allAusgaben.elementAt(i)).get("id"));
				out.println("<p>"+((Hashtable)allAusgaben.elementAt(i)).get("name")+ ": "+subs+ "<br><input name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+ "\" type=\"text\" size=\"40\" value=\""+value+"\" maxlength=\"50\">");
				if (absolute.equals("relativ"))
				{
					out.println("<input type=\"radio\" name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+"_radio\" value=\"relativ\" checked> Relativ ");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+"_radio\" value=\"absolut\"> Absolut <br></p>");
				}
				else
				{
					out.println("<input type=\"radio\" name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+"_radio\" value=\"relativ\"> Relativ ");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allAusgaben.elementAt(i)).get("name")+"_radio\" value=\"absolut\"checked > Absolut <br></p>");
				}
				
				
				
				
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
					
					value=db.getPlanung_daten_wert((Integer)hash.get("plan_id"),(Integer)((Hashtable)allEinnahmen.elementAt(i)).get("id"));
					absolute=db.getPlanung_daten_absolut((Integer)hash.get("plan_id"),(Integer)((Hashtable)allEinnahmen.elementAt(i)).get("id"));
				out.println("<p>"+((Hashtable)allEinnahmen.elementAt(i)).get("name")+ ": "+subs+ "<br><input name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+ "\" type=\"text\" size=\"40\" value=\""+value+"\"  maxlength=\"50\">");
				if (absolute.equals("relativ"))
				{
					out.println("<input type=\"radio\" name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+"_radio\" value=\"relativ\" checked> Relativ ");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+"_radio\" value=\"absolut\"> Absolut <br></p>");
				}
				else
				{
					out.println("<input type=\"radio\" name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+"_radio\" value=\"relativ\"> Relativ ");
					out.println("<input type=\"radio\" name=\""+((Hashtable)allEinnahmen.elementAt(i)).get("name")+"_radio\" value=\"absolut\" checked > Absolut <br></p>");
				}
				}
				
				out.println("<p>");
				out.println("<input type=\"checkbox\" name=\"loeschen\" value=\"ja\">Planung komplett löschen <br>");
				out.println("<p>");
				if (((String)hash.get("batch")).equals("ja"))
				{
					out.println("<input type=\"checkbox\" name=\"batch\" value=\"ja\" checked>Planung im Batch berechnen <br>");
				}
				else
				{
				out.println("<input type=\"checkbox\" name=\"batch\" value=\"ja\">Planung im Batch berechnen <br>");
				}
				out.println("<input type=\"submit\" value=\" Absenden \">");
				out.println("</form>");

	
				//out.println("loesche ... ");
				out.println("<p>");
				
				out.println("</body>");
				out.println("</html>");
				out.close();
			}catch (Throwable theException) {
					theException.printStackTrace();
				}

		}
}
