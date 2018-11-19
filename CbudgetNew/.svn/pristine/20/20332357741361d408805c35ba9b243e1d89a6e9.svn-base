package budget;

import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class KategorieEinfuegen extends javax.servlet.http.HttpServlet {

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
				String parent =request.getParameter("mutterkategorie");
				if (parent==null)
				{
					parent="";
				}
				String beschreibung = request.getParameter("Beschreibung");
				String monthlimit =request.getParameter("monthlimit");
				String yearlimit = request.getParameter("yearlimit");
				String mode=request.getParameter("art");
				
				Hashtable hash= new Hashtable();
				hash.put("name",name);
				hash.put("parent",parent);
				hash.put("description",beschreibung);
				hash.put("mode",mode);
				
				if (!checkfloat (monthlimit))
				{
					monthlimit="-1";
				}
				System.out.println("monthlimit="+monthlimit);
				hash.put("monthlimit",monthlimit);
				if (!checkfloat (yearlimit))
				{
					yearlimit="-1";
				}
				hash.put("yearlimit",yearlimit);
				
				HeaderFooter hf = new HeaderFooter();
				out.println("<html>");
				out.println("<body>");

				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				out.println("Kategorie wird erstellt...");
				out.println("<p>");
				if (db.insertKategorie(hash))
				{
				out.println("Kategorie erfolgreich erstellt");
				}
				else
				{
					out.println("<font color=\"green\">!!!Kategorie konte nicht erstellt werden!!!</font>");
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

				