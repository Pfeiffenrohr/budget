package budget.anlagearten;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;

    //Aufruf http://localhost:8080/filme/MainFrame

    public class Anlageverwaltung extends javax.servlet.http.HttpServlet {

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
                
                //Konten einlesen

                DB db = (DB)session.getAttribute("db"); 
                String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
                {
                    RequestDispatcher rd;
                    rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
                    rd.forward(request, response);
                    return;
                }
                Vector vec= new Vector();
                vec=db.getAllAnlagen();
                session.setAttribute("anlagen", vec);

                out.println("<html>");
                out.println("<head>");
                
                out.println("</head>");

                
                
                out.println("<body bgcolor=\"#EEFFBB\">");
                hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
                out.println("<h1>Anlageverwaltung</h1>");
                out.println("<table cellpadding=\"0\" cellspacing=\"4\" width=\"114\" border=\"0\">");
                out.println("<tr><td>");
                out.println("<form action=\"neueanlageart\">");
                out.println("<input type=\"submit\" name=\"Text 1\" value=\"Neue Anlageart anlegen\">");
                out.println("</form>");
                out.println("</td>");
                out.println("<td>");
                out.println("<form action=\"anlageaendern\">");
                out.println("<input type=\"submit\" name=\"Text 2\" value=\"Anlage ändern/löschen\">");
                out.println("</td></tr>");
                out.println("</table>");
                out.println("</p>");
                out.println("<table border=0>");//Tabelle für Eingabefeld und Baum
                out.println("<td>");
                
//Übersicht über die Kategorien
                out.println("<table border=\"1\" rules=\"groups\">");
                //out.println("<table border=\"1\">");
                out.println("<thead>");
                out.println("<tr>");
                out.println("<th></th>");
                out.println("<th>Name</th>");
                out.println("</tr>");
                out.println("</thead>");
                out.println("<tbody>");
                out.println("<tr>");
                //Allle Anlagen durchgehen und eintragen
                for (int i=0; i<vec.size();i++)
                {
                //out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
                 out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+new Integer(i).toString()+"\"></td>");
                out.println("<td>"+((Hashtable)vec.elementAt(i)).get("name")+"</td>");
                //out.println("<td><font color=\"green\">1896.56</font></td>");
                out.println("</tr>");
                }
                out.println("</tbody>");
                out.println("</table>");
                out.println("</td>");
                out.println("<td>");
                out.println("<div style=\"position:absolute; top:0; left:0; \"><table border=\"0\"><tr><td><font size=\"-2\"><a style=\"font-size:7pt;text-decoration:none;color:silver;\" href=\"http://www.treemenu.net/\" target=\"_blank\">JavaScript Tree Menu</a></font></td></tr></table></div>");
                out.println("<SCRIPT>initializeDocument()</SCRIPT>");
                out.println("</td>");
                out.println("</table>");
                out.println("</form>");
                                
                out.println("</body>");
                out.println("</html>");
                out.close();
            }

            catch (Throwable theException) {
                theException.printStackTrace();
            }
        }
        
      
        
        
       
      
}