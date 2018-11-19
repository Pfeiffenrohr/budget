package budget;

import java.io.PrintWriter;
import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class TransaktionEinfuegen extends javax.servlet.http.HttpServlet {

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
				String user=(String)((Hashtable)session.getAttribute("settings")).get("user");
				
				DB db = (DB)session.getAttribute("db"); 
				String auth=(String)session.getAttribute("auth"); if (db==null || ! auth.equals("ok") )
				{
					RequestDispatcher rd;
					rd = getServletContext().getRequestDispatcher("/startseite?info=Zeit abgelaufen");
					rd.forward(request, response);
					return;
				}
				String akt_konto = (String)session.getAttribute("akt_konto");
				String name = request.getParameter("Name");
				String wert = request.getParameter("wert");
				String beschreibung = request.getParameter("beschreibung");
				String datum = request.getParameter("dob");
				String kategorie =request.getParameter("kategorie");
				if (kategorie==null)
				{
					kategorie="";
				}
				String partner = request.getParameter("partner");
				String korkonto =request.getParameter("korkonto");
				if (korkonto==null)
				{
					korkonto="nein";
				}
				String kor_konto = request.getParameter("kor_konto");
				if (kor_konto==null)
				{
					kor_konto="Sparkonto";
				}
				String planung = request.getParameter("planung");
				if (planung==null)
				{
					planung="n";
				}
				Hashtable hash= new Hashtable();
				hash.put("name",name);
				hash.put("wert",wert);
				hash.put("beschreibung",beschreibung);
				hash.put("partner",partner);
				hash.put("kategorie",kategorie);
				hash.put("konto",akt_konto);
				hash.put("kor_konto",kor_konto);
				hash.put("datum",datum);
				hash.put("cycle",0);
				hash.put("planed",planung);
				hash.put("user", user);
				
				
				HeaderFooter hf = new HeaderFooter();
				out.println("<html>");
				out.println("<body>");

				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				out.println("Transaktion wird erstellt...");
				out.println("<p>");
				if (!wertOK(wert))
				{
					out.println("<font color=\"red\">!!!Der Betrag hat nich da richtige Format!!!</font>");
					out.println("</body>");
					out.println("</html>");
					out.close();
					return;
				}
				if (! korkonto.equals("ja"))
				{
					insertSimpleTransaction(db,hash,out,akt_konto);
				}
				else
				{
					insertKorTransaction(db,hash,out,akt_konto);
				}
				out.println("</body>");
				out.println("</html>");
				out.close();
			}catch (Throwable theException) {
					theException.printStackTrace();
				}

		}
		
		private void insertSimpleTransaction(DB  db, Hashtable hash,PrintWriter out,String akt_konto)
		{
		if (db.insertTransaktion(hash,0))
		{
		out.println("Transaktion erfolgreich erstellt");
		out.println("<p><a href='transaktionsverwaltung?konto="+akt_konto+"'>Zurück zur Transaktionsverwaltung</a>");
		}
		else
		{
			out.println("<font color=\"red\">!!!Transaktion konnte nicht getätigt werden!!!</font>");
			out.println("<p><a href='transaktionsverwaltung?konto="+akt_konto+"'>Zurück zur Transaktionsverwaltung</a>");
		}
		}
		
		private void insertKorTransaction(DB  db, Hashtable hash,PrintWriter out,String akt_konto)
		{
			boolean fehler=false;
			Integer aktId=db.getHighestId("transaktionen","kor_id")+1;
			System.out.println("HighestId =" +aktId);
			if (db.insertTransaktion(hash,aktId))
			{
				fehler=true;
			}
			hash.put("konto", (String)hash.get("kor_konto"));
			hash.put("wert",invert((String)hash.get("wert")));
			
			if (db.insertTransaktion(hash,aktId))
			{
				fehler=true;
			}
		if (fehler)
		{
		out.println("MultiTransaktion erfolgreich erstellt");
		out.println("<p><a href='transaktionsverwaltung?konto="+akt_konto+"'>Zurück zur Transaktionsverwaltung</a>");
		}
		else
		{
			out.println("<font color=\"red\">!!!Transaktionen konnte nicht getätigt werden!!!</font>");
			out.println("<p><a href='transaktionsverwaltung?konto="+akt_konto+"'>Zurück zur Transaktionsverwaltung</a>");
		}
		}
		private String invert(String wert)
		{
			try{
			Double db = new Double(wert);
			db=db*-1;
			return db.toString();
			}
			catch (NumberFormatException nfe )
			{
				System.err.println (nfe);
				return "0.0";
			}
		}
		public Double makeDouble(String str)
		{
			Double fl=0.0; 
			try{
				fl = new Double(str);
			}
			catch (NumberFormatException nfe){
				System.err.println (nfe);
				return 0.0;
			}
			return fl;
		}
		
		public boolean wertOK(String wert)
		{
			try{
			Double db = new Double(wert);
			}
			catch (NumberFormatException nfe){
				return false;
			}
			return true;
		}
}

				