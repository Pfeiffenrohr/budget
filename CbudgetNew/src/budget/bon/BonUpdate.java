package budget.bon;

import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

import budget.HeaderFooter;
import cbudgetbase.DB;

public class BonUpdate  extends javax.servlet.http.HttpServlet  {
	
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
			
			Hashtable hash = (Hashtable) session.getAttribute("bonsatz");
			String id = request.getParameter("id");
			

			
			String internalname = request.getParameter("internalname");
			
			System.out.println("Internalname "+internalname);
			
			hash.put("internalname",internalname);
			
			
			HeaderFooter hf = new HeaderFooter();
			out.println("<html>");
			out.println("<body>");
			hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
			

			{
			out.println("<p>");
			out.println("Bon wird updatet...");
			out.println("<p>");
			if (db.updateBon(hash))
			{
			out.println("Bon erfolgreich update");
			}
			else
			{
				out.println("<font color=\"red\">!!!Bon konte nicht update werden!!!</font>");
			}
			out.println("</body>");
			out.println("</html>");
			out.close();
			}
		}catch (Throwable theException) {
				theException.printStackTrace();
			}

	}

}
