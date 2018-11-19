package budget;

import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Kategorieverwaltung extends javax.servlet.http.HttpServlet {

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
				vec=db.getAllKategorien();
				session.setAttribute("kategorien", vec);

				out.println("<html>");
				out.println("<head>");
				out.println("<script src=\"js/ua.js\"></script>");
				out.println("<script src=\"js/ftiens4.js\"></script>");
				out.println("<script>");
				out.println("STARTALLOPEN = 0 ");
				out.println("HIGHLIGHT = 1");
				out.println("foldersTree = gFld(\"<i>Kategorien</i>\", \"test\")");
				out.println("foldersTree.treeID = \"Funcs\"");
				out.println("aux1 = insFld(foldersTree, gFld(\"Ausgaben\", \"test\"))");
				getItems("ausgabe",vec,out);
				//out.println("insDoc(aux1, gLnk(\"R\", \"Fixkosten\", \"\"))");
				out.println("aux1 = insFld(foldersTree, gFld(\"Einnahmen\", \"\"))");
				getItems("einnahme",vec,out);
				out.println("aux1 = insFld(foldersTree, gFld(\"Buchungen\", \"\"))");
				getItems("buchung",vec,out);
				out.println("</script>");
				out.println("</head>");

				
				
				out.println("<body bgcolor=\"#EEFFBB\">");
				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<h1>Kategorieverwaltung</h1>");
				out.println("<table cellpadding=\"0\" cellspacing=\"4\" width=\"114\" border=\"0\">");
				out.println("<tr><td>");
				out.println("<form action=\"neueKategorie\">");
				out.println("<input type=\"submit\" name=\"Text 1\" value=\"Neue Kategorie anlegen\">");
				out.println("</form>");
				out.println("</td>");
				out.println("<td>");
				out.println("<form action=\"kategorienAendern\">");
				out.println("<input type=\"submit\" name=\"Text 2\" value=\"Kategorie ändern/löschen\">");
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
				out.println("<th>Mutterkategorie</th>");
				out.println("</tr>");
				out.println("</thead>");
				out.println("<tbody>");
				out.println("<tr>");
				//Allle Konten durchgehen und eintragen
				for (int i=0; i<vec.size();i++)
				{
				//out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+((Integer)((Hashtable)vec.elementAt(i)).get("id")).toString()+"\"></td>");
			     out.println("<td><input type=\"checkbox\" name=\"loeschen\" value=\""+new Integer(i).toString()+"\"></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("name")+"</td>");
				//out.println("<td><font color=\"green\">1896.56</font></td>");
				out.println("<td>"+((Hashtable)vec.elementAt(i)).get("parent")+"</td>");
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
		
		void getItems(String mode ,Vector kat,PrintWriter out)
		{
			Vector all = getVecMode(kat,mode);
			Hashtable allFound = new Hashtable();
			Hashtable tree = getTree(all,"",out,1,allFound);
		}
		
		
		Hashtable getTree(Vector all,String parent,PrintWriter out,int tiefe,Hashtable allFound)
		{
			//Erstellt einen Baum zu jeweilgen mode
			//Suche alle, die als parent "parent haben und ordne sie in eine Hashtable
			Hashtable neu = new Hashtable();
			int tiefeinc = tiefe-1;
			boolean found=false;
			for (int i=0; i<all.size();i++)
			{
				if (   ((String)((Hashtable)all.elementAt(i)).get("parent")).equals(parent)     )
				{
					found=true;
					if (! parent.equals("")&& ! allFound.containsKey(parent))
					{
					out.println("aux"+tiefe+" = insFld(aux"+tiefeinc+", gFld(\""+parent+"\", \"\"))");
					allFound.put(parent,found);
					//out.println("aux"+tiefe+" = insFld(foldersTree, gFld(\""+ parent+"\", \"\"))");
					}
					neu.put(((String)((Hashtable)all.elementAt(i)).get("name")), getTree(all, ((String)((Hashtable)all.elementAt(i)).get("name")),out,tiefe+1,allFound));
					}
				}
			if ( ! found && ! allFound.containsKey(parent))
			{
				tiefe--;
			out.println("insDoc(aux"+tiefe+", gLnk(\"R\", \"" +parent +"\", \"\"))");
			allFound.put(parent,found);
			}
			return neu;
		}
		
		Vector getVecMode(Vector kat, String mode)
		{
		//Holt alle Werte aus dem Vector kat, der mode entspricht
			Vector neu = new Vector();
			for (int i=0; i<kat.size();i++)
			{
				if ( ((String)((Hashtable)kat.elementAt(i)).get("mode")).equals(mode))
				{
				neu.add((Hashtable)kat.elementAt(i));	
				}
			}
			return neu;
		}
}