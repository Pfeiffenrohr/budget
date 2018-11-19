	package budget;


	import java.io.PrintWriter;
	import java.util.Hashtable;
	import java.util.Vector;
	import cbudgetbase.DB;

	import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


		//Aufruf http://localhost:8080/filme/MainFrame

		public class NeueRegel extends javax.servlet.http.HttpServlet {

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
					String modus;
					HeaderFooter hf = new HeaderFooter();
					String rule_id = request.getParameter("aendern");
					String name="" ;
					String beschreibung ="";
					String mode="alle";
					Hashtable hash;
					Vector items =new Vector();
					String start="";
					if (rule_id==null)
					{
						//System.out.println("neu");
						modus="neu";
					}
					else
					{
						//System.out.println("aendern");
						modus="aendern";
						hash=db.getRule(new Integer(rule_id));
						//System.out.println(hash);
						name=(String)hash.get("name");
						beschreibung=(String)hash.get("beschreibung");
						mode=(String)hash.get("mode");
						items=db.getRulesItems(new Integer(rule_id));
						//System.out.println("Start = "+start);
						for (int i=0;i<items.size();i++)
						{
							start=start+"addFilterOnStart("+i+",'";
							start=start+((Hashtable)items.elementAt(i)).get("art")+"','";
							start=start+((Hashtable)items.elementAt(i)).get("operator")+"','";
							start=start+((Hashtable)items.elementAt(i)).get("value")+"'";
							start=start+");";
						}
					}
					System.out.println("Start = "+start);
					
					Vector kategorien=db.getAllKategorien();
					Vector konten=db.getAllKonto();
					Vector regeln=db.getAllRules();
					
					out.println("<html>");
					out.println("<head>");
					out.println("<script type=\"text/javascript\" src=\"js/filter1.js\"></script>");
					out.println("</head>");
					out.println("<body bgcolor=\"#EEFFBB\" onload=\""+start+"\">");
					//out.println("<body bgcolor=\"#EEFFBB\" onload=\"addFilterOnStart(0,'title','eq','testname');addFilterOnStart(1,'category','le','28');addFilterOnStart(2,'rule','eq','10');addFilterOnStart(3,'betrag','eq','14');\">");

					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<p>");
					
				
					out.println("<h1>Bitte Regel angeben</h1>");
					out.println("<div id='filterLineEmpty' style='display:none;'><div style=\"float: left;\"><select name='filterSelect__FILTER_ID__' id='filterSelect__FILTER_ID__' onchange=\"setFilterContent('__FILTER_ID__');\" >");
					out.println("<option  value='unselected'>Bitte wählen Sie einen Filter</option>");
					out.println("<option  value='title'>Name</option>");
					out.println("<option  value='category'>Kategorie</option>");
					out.println("<option  value='rule'>Regel</option>");
					out.println("<option  value='konto'>Konto</option>");
					out.println("<option  value='betrag'>Betrag</option>");
					out.println("<option  value='delete'>&lt;Filter löschen&gt;</option>");

					out.println("</select>");
					out.println("</div><div id=\"filterContent__FILTER_ID__\"></div></div>"); 
					
					//name
					out.println("<div id='unselectedEmpty' style='display:none;'></div><div id='titleEmpty' style='display:none;'>Name ist<select name='titleOperator__FILTER_ID__' id='titleOperator__FILTER_ID__' style='width: 95px;' >");
					out.println("<option  value='eq'>gleich</option>");
					out.println("<option  value='ne'>ungleich</option>");
					out.println("<option  value='bw'>fängt an mit</option>");
					out.println("<option  value='ew'>hört auf mit</option>");
					out.println("<option  value='ct'>enthält</option>");
					out.println("</select>");
					out.println("&nbsp;<input type='text' id='title__FILTER_ID__' name='title__FILTER_ID__' size='30' class='inputString' value=''   /></div>");
					//Kategorie
					out.println("<div id='categoryEmpty' style='display:none;'>Kategorie<input type='radio' id='categoryOp__FILTER_ID__' name='categoryOp__FILTER_ID__' size='' class='inputString' value='eq'   /><label for='categoryOp__FILTER_ID__' id='labelcategoryOp__FILTER_ID__'>ist</label>&nbsp;<input type='radio' id='categoryOp__FILTER_ID___0' name='categoryOp__FILTER_ID__' size='' class='inputString' value='ne'   /><label for='categoryOp__FILTER_ID___0' id='labelcategoryOp__FILTER_ID___0'>ist nicht</label>&nbsp;<select name='categoryId__FILTER_ID__' id='categoryId__FILTER_ID__' style='width: 210px;' >");
					for (int i=0;i<kategorien.size();i++)
					{					
						out.println("<option value="+((Hashtable)kategorien.elementAt(i)).get("id") +">"+((Hashtable)kategorien.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("</div>");
					//Regel
					out.println("<div id='ruleEmpty' style='display:none;'>Regel<input type='radio' id='ruleOp__FILTER_ID__' name='ruleOp__FILTER_ID__' size='' class='inputString' value='eq'   /><label for='ruleOp__FILTER_ID__' id='labelruleOp__FILTER_ID__'>beinhaltet</label>&nbsp;<input type='radio' id='ruleOp__FILTER_ID___0' name='ruleOp__FILTER_ID__' size='' class='inputString' value='ne'   /><label for='ruleOp__FILTER_ID___0' id='labelruleOp__FILTER_ID___0'>beinhaltet nicht</label>&nbsp;<select name='ruleId__FILTER_ID__' id='ruleId__FILTER_ID__' style='width: 210px;' >");
					
					for (int i=0;i<regeln.size();i++)
					{
						
						out.println("<option value="+((Hashtable)regeln.elementAt(i)).get("rule_id") +">"+((Hashtable)regeln.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("</div>");
					//Konto
					out.println("<div id='kontoEmpty' style='display:none;'>Konto<input type='radio' id='kontoOp__FILTER_ID__' name='kontoOp__FILTER_ID__' size='' class='inputString' value='eq'   /><label for='kontoOp__FILTER_ID__' id='labelkontoOp__FILTER_ID__'>ist</label>&nbsp;<input type='radio' id='kontoOp__FILTER_ID___0' name='kontoOp__FILTER_ID__' size='' class='inputString' value='ne'   /><label for='kontoOp__FILTER_ID___0' id='labelkontoOp__FILTER_ID___0'>ist nicht</label>&nbsp;<select name='kontoId__FILTER_ID__' id='kontoId__FILTER_ID__' style='width: 210px;' >");
					for (int i=0;i<konten.size();i++)
					{
						
						out.println("<option value="+((Hashtable)konten.elementAt(i)).get("id") +">"+((Hashtable)konten.elementAt(i)).get("name")+"</option>");
					}
					out.println("</select>");
					out.println("</div>");
					//Betrag
					out.println("<div id='betragEmpty' style='display:none;'>Betrag ist<select name='betragOperator__FILTER_ID__' id='betragOperator__FILTER_ID__' style='width: 95px;' >");
					out.println("<option  value='eq'>=</option>");
					out.println("<option  value='lt'>&lt;</option>");
					out.println("<option  value='le'>&lt;=</option>");
					out.println("<option  value='gt'>&gt;</option>");
					out.println("<option  value='ge'>&gt;=</option>");
					out.println("<option  value='ne'>&lt;&gt;</option>");
					out.println("</select>");
					out.println("&nbsp;<input type='integer' id='betrag__FILTER_ID__' name='betrag__FILTER_ID__' size='3' class='inputString' value=''   /></div>");
					
					
					out.println("<form action=regeleinfuegen method=post>");
					out.println("<p>Name:<br><input name=\"Name\" type=\"text\"  value=\""+ name +"\"size=\"40\" maxlength=\"50\"></p>");
					out.println("<p>");
					out.println("<p>Beschreibung:<br><textarea name=\"beschreibung\" cols=\"50\" rows=\"10\" >"+beschreibung+"</textarea>");
					out.println("<p>");
					out.println("Kriterien auf  <select name=\"mode\" size=\"1\">");
					if (mode.equals("alle")) 
					{
					out.println("<option selected>alle</option>");
					}
					else
					{
						out.println("<option>alle</option>");
					}	
					if (mode.equals("eine")) 
					{
					out.println("<option selected>eine</option>");
					}
					else
					{
						out.println("<option>eine</option>");
					}	
					out.println("<option>eine</option>");
					out.println("</select>");
					out.println(" Bedingungen anwenden");
					
					out.println("<p>");
					out.println("<div>");
					out.println("Filter");
					out.println("<button  name='addFilter' id='addFilter' onclick=\"addFilterLineX();\" type=\"button\">");

					out.println("<table cellspacing='0' cellpadding='0'>");
					out.println("<tr>");
					out.println("<td nowrap='nowrap'>&nbsp;Filter hinzufügen</td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("</button>");
					out.println("</div>");
					out.println("<form name=\"mainform\" id=\"mainform\">");
					out.println("<div id=\"filterContent\" style=\"overflow: auto; height: 10em; border: 1px solid blue; width: 50em;\">");
					out.println("</div>");
					out.println("<input type=\"hidden\" name=\"rule_id\" value=\""+rule_id+"\">");
					out.println("<input type=\"hidden\" name=\"filter_anzahl\" id=\"filter_anzahl\" value=\"0\">");
					if (modus.equals("aendern"))
					{
					out.println("<input type=\"checkbox\" name=\"loeschen\" value=\"ja\"> Regel komplett löschen <br>");
					out.println("<p>");
					}
					out.println("<input type=\"submit\" value=\" Absenden\" ;>");
					out.println("</form>");
					out.println("</form>");
					out.println("</body>");
					out.println("</html>");
					out.close();
				}catch (Throwable theException) {
						theException.printStackTrace();
					}

			}
	}
