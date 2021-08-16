package budget.anlagearten;

import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.http.HttpSession;

import budget.HeaderFooter;

//Aufruf http://localhost:8080/filme/MainFrame

public class NeueAnlageart extends javax.servlet.http.HttpServlet {

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
            out.println("<html>");
            out.println("<body>");

            hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
            out.println("<h1>Bitte Analgeart eingeben</h1>");
            out.println("<form action=anlageeinfuegen method=post>");
            out.println("<p>Name:<br><input name=\"Name\" type=\"text\" size=\"40\" maxlength=\"50\"></p>");
            out.println("<p>");
            out.println("<p>Beschreibung:<br><input name=\"Beschreibung\" type=\"textarea\" size=\"40\" maxlength=\"50\"></p>");
            out.println("<p>");
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
