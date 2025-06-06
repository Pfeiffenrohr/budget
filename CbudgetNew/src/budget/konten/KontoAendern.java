package budget.konten;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;
 

	//Aufruf http://localhost:8080/filme/MainFrame

	public class KontoAendern extends javax.servlet.http.HttpServlet {

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
					out.println("<h2>Sie haben keinen Datensatz ausgew�hlt</h2>");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
				//Vector vec=new Vector();
				//vec=(Vector)session.getAttribute("konten"); 
				int element = new Integer(loeschen).intValue();
				Hashtable kontosatz= db.getKontoID(loeschen);
				session.setAttribute("kontensatz", kontosatz);
				out.println("<html>");
				out.println("<body>");

				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				
				
				out.println("<h1>Bitte Kontodaten �ndern</h1>");
				out.println("<form action=kontoUpdaten method=post>");
				out.println("<p>Name:<br><input name=\"Name\" type=\"text\" value=\""+ kontosatz.get("name") +"\" size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				out.println("<p>Beschreibung:<br><input name=\"Beschreibung\" type=\"textarea\"value=\""+ kontosatz.get("description") +"\" size=\"40\" maxlength=\"50\"></p>");
				out.println("<p>");
				//System.out.println("Versteckt ="+((String)kontosatz.get("versteckt"))+"<");
				if (((String)kontosatz.get("versteckt")).trim().equals("ja"))
				{
					//System.out.println(" ver = ja");
				out.println("<input type=\"checkbox\" name=\"versteckt\" value=\"ja\" checked> Verstecktes Konto <br>");
				}
				else
				{
					//System.out.println(" ver = nein");
					out.println("<input type=\"checkbox\" name=\"versteckt\" value=\"ja\"> Verstecktes Konto <br>");
				}
				out.println("<fieldset>");
				String mode;
				Vector allAnlagen = db.getAllAnlagen();
                
                for (int i=0; i< allAnlagen.size();i++)
                    
                {
                    Hashtable hash = (Hashtable)allAnlagen.get(i);
               
                    if (((String)kontosatz.get("mode")).equals(hash.get("name"))) mode="checked";else mode="";
                    out.println("<input type=\"radio\" id=\""+i+"\" name=\"mode\" value=\""+hash.get("name")+"\" "+mode+"><label for=\"mc\">"+hash.get("name")+"</label><br>");
                }
				/*
				if (((String)kontosatz.get("mode")).equals("Geldkonto")) mode="checked";else mode=""; 
				out.println("<input type=\"radio\" id=\"0\" name=\"mode\" value=\"Geldkonto\" "+mode+"><label for=\"mc\"> Geldkonto</label><br>"); 
				if (((String)kontosatz.get("mode")).equals("Geldanlage")) mode="checked";else mode=""; 
				out.println("<input type=\"radio\" id=\"1\" name=\"mode\" value=\"Geldanlage\" "+mode+"><label for=\"mc\"> Geldanlage</label><br>");
				if (((String)kontosatz.get("mode")).equals("Sachanlage")) mode="checked";else mode=""; 
				out.println("<input type=\"radio\" id=\"2\" name=\"mode\" value=\"Sachanlage\" "+mode+"><label for=\"mc\"> Sachanlage</label><br>");
				if (((String)kontosatz.get("mode")).equals("Verbindlichkeit")) mode="checked";else mode=""; 
				out.println("<input type=\"radio\" id=\"2\" name=\"mode\" value=\"Verbindlichkeit\" "+mode+"><label for=\"mc\"> Verbindlichkeit</label><br>");
				*/
				
				out.println("</fieldset>");
				out.println("<p>");
				out.println("<p>Welche Regel soll f�r den Ertrag verwendet werden? (Wenn keine, dann bitte leer lassen <br>");
                out.println("Regel: <select name=\"rule_id\" size=\"1\">");
                //out.println("<option>   </option>");
                Vector rules=db.onlyValidRules(db.getAllRules());
                String select="";
                String rule_id= Integer.toString((Integer)(kontosatz.get("rule_id")));
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
				
				out.println("<input type=\"checkbox\" name=\"loeschen\" value=\"ja\"> Konto komplett l�schen <br>");
				out.println("<p>Aktueller Stand:<br><input name=\"newValue\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
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
