package budget.anlagearten;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;


    //Aufruf http://localhost:8080/filme/MainFrame

    public class AnlageAendern extends javax.servlet.http.HttpServlet {

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
                    out.println("<h2>Sie haben keinen Datensatz ausgewählt</h2>");
                    out.println("</body>");
                    out.println("</html>");
                    out.close();
                    return;
                }
                Vector vec=new Vector();
                vec=(Vector)session.getAttribute("anlagen"); 
                int element = new Integer(loeschen).intValue();
                session.setAttribute("anlagensatz", vec.elementAt(element));
                out.println("<html>");
                out.println("<body>");

                hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
                out.println("<p>");
                
                
                out.println("<h1>Bitte Anlagendaten ändern</h1>");
                out.println("<form action=anlageupdaten method=post>");
                out.println("<p>Name:<br><input name=\"Name\" type=\"text\"  value=\""+ ((Hashtable)vec.elementAt(element)).get("name") +"\"size=\"40\" maxlength=\"50\"></p>");
                out.println("<p>");
                out.println("<p>Beschreibung:<br><input name=\"Beschreibung\" type=\"textarea\"  value=\""+ ((Hashtable)vec.elementAt(element)).get("description") +"\"size=\"40\" maxlength=\"50\"></p>");
                out.println("<p>");
               
                out.println("<p>Für die Anlage soll Rendite berechnet werden: <br>");
                if ((((Hashtable)vec.elementAt(element)).get("rendite")).equals("Y"))
                {
                 
                out.println("<input type=\"radio\" name=\"rendite\" value=\"Y\" \" checked> J <br>");
                }
                else
                {
                out.println("<input type=\"radio\" name=\"rendite\" value=\"Y\"> J <br>"); 
                }
                if ((((Hashtable)vec.elementAt(element)).get("rendite")).equals("N"))
                {
                out.println("<input type=\"radio\" name=\"rendite\" value=\"N\" checked> N <br>");
                }
                else
                {
                out.println("<input type=\"radio\" name=\"rendite\" value=\"N\"> N <br>"); 
                }
                out.println("<p>");
                out.println("<p>Welche Regel soll für den Ertrag verwendet werden? <br>");
                out.println("Regel: <select name=\"rule_id\" size=\"1\">");
                //out.println("<option>   </option>");
                Vector rules=db.onlyValidRules(db.getAllRules());
                String select="";
                String rule_id= Integer.toString((Integer)((Hashtable)vec.elementAt(element)).get("rule_id"));
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
                out.println("<p>Anlage löschen: <br>");
                out.println("<input type=\"checkbox\" name=\"loeschen\" value=\"ja\"> Anlage komplett löschen <br>");
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