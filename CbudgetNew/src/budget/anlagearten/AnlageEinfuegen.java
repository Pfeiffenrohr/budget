package budget.anlagearten;

import java.io.PrintWriter;
import java.util.Hashtable;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;



    //Aufruf http://localhost:8080/filme/MainFrame 

    public class AnlageEinfuegen extends javax.servlet.http.HttpServlet {

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
                
                String name = request.getParameter("Name");
                String beschreibung = request.getParameter("Beschreibung");
                String rendite=request.getParameter("rendite");
                Integer rule_id= new Integer (request.getParameter("rule_id"));
               
                
                Hashtable hash= new Hashtable();
               
                hash.put("name",name);
                hash.put("description",beschreibung);
                hash.put("rendite",rendite);
                hash.put("rule_id",rule_id);
                
                HeaderFooter hf = new HeaderFooter();
                out.println("<html>");
                out.println("<body>");

                hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
                out.println("<p>");
                out.println("Anlage wird erstellt...");
                out.println("<p>");
                if (db.insertAnlage(hash))
                {
                out.println("Anlage erfolgreich erstellt");
                }
                else
                {
                    out.println("<font color=\"red\">!!!Anlage konte nicht erstellt werden!!!</font>");
                }
                out.println("</body>");
                out.println("</html>");
                out.close();
            }catch (Throwable theException) {
                    theException.printStackTrace();
                }

        }
        public boolean checkfloat(String str)
        {
            try{
                Float fl = new Float(str);
            }
            catch (NumberFormatException nfe){
                return false;
            }
            return true;
        }
}
