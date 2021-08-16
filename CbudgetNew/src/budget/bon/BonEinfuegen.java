package budget.bon;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;
import cbudgetbase.DB;

public class BonEinfuegen extends javax.servlet.http.HttpServlet {
	
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
			//String loeschen = request.getParameter("loeschen");
			
			//Vector vec=new Vector();
			//vec=(Vector)session.getAttribute("kategorien"); 
			Hashtable hash = db.getBon();
			session.setAttribute("bonsatz", hash);
			out.println("<html>");
			out.println("<body>");

			hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
			out.println("<p>");
			
			if (hash==null)
			{
			out.println("Alles aktuell :)");	
		    out.println("</body>");
			out.println("</html>");
			}
			else
			{
			out.println("<h1>Bitte Bon ändern</h1>");
			out.println("<form action=bonupdate method=post>");
			
			out.println("<p>"+hash.get("rawname")+":<br><input name=\"internalname\" type=\"text\" \"size=\"40\" maxlength=\"50\"></p>");
			
			out.println("<input type=\"submit\" value=\" Absenden \">");
			out.println("</form>");


			//out.println("loesche ... ");
			out.println("<p>");
			
			out.println("</body>");
			out.println("</html>");
			}
			out.close();
		}catch (Throwable theException) {
				theException.printStackTrace();
			}

	}

}
