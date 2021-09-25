	package budget.konten;

	import java.io.PrintWriter;
	import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;
import cbudgetbase.DB;

		//Aufruf http://localhost:8080/filme/MainFrame

		public class NeuesKonto extends javax.servlet.http.HttpServlet {

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
				    HttpSession session = request.getSession(true);
				    DB db = (DB)session.getAttribute("db"); 
				    String rule_id=request.getParameter("rule_id");
				    if (rule_id==null)
				    {
				        rule_id="-1";
				    }
	                //Hashtable settings = (Hashtable) session.getAttribute("settings");
	                String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
	                {
	                    RequestDispatcher rd;
	                    rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
	                    rd.forward(request, response);
	                    return;
	                }
	                
					//FileHandling fh = new FileHandling();
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					HeaderFooter hf = new HeaderFooter();
					out.println("<html>");
					out.println("<body>");

					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<h1>Bitte Kontodaten eingeben</h1>");
					out.println("<form action=kontoEinfuegen method=post>");
					out.println("<p>Name:<br><input name=\"Name\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<p>Beschreibung:<br><input name=\"Beschreibung\" type=\"textarea\" size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<p>Geben Sie die Art des Kontos an:</p>");
					out.println("<fieldset>");
					Vector allAnlagen = db.getAllAnlagen();
					
					for (int i=0; i< allAnlagen.size();i++)
					    
					{
					    Hashtable hash = (Hashtable)allAnlagen.get(i);
					    out.println("<input type=\"radio\" id=\""+i+"\" name=\"mode\" value=\""+hash.get("name")+"\"><label for=\"mc\">"+hash.get("name")+"</label><br>"); 
					}
					/*
					 
					out.println("<input type=\"radio\" id=\"0\" name=\"mode\" value=\"Geldkonto\"><label for=\"mc\"> Geldkonto</label><br>"); 
					out.println("<input type=\"radio\" id=\"1\" name=\"mode\" value=\"Geldanlage\"><label for=\"mc\"> Geldanlage</label><br>");
					out.println("<input type=\"radio\" id=\"2\" name=\"mode\" value=\"Sachanlage\"><label for=\"mc\"> Sachanlage</label><br>");
					out.println("<input type=\"radio\" id=\"3\" name=\"mode\" value=\"Verbindlichkeit\"><label for=\"mc\"> Verbindlichkeit</label><br>");
					*/
					out.println("</fieldset>");
					out.println("<p>");
					out.println("<p>Welche Regel soll für den Ertrag verwendet werden?  (Wenn keien, dann bitte leer lassen) <br>");
		            out.println("Regel: <select name=\"rule_id\" size=\"1\">");
					Vector rules=db.onlyValidRules(db.getAllRules());
		            String select="";
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
					out.println("<input type=\"checkbox\" name=\"versteckt\" value=\"ja\"> Verstecktes Konto <br>");
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
