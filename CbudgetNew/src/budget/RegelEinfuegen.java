package budget;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import cbudgetbase.DB;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;


	//Aufruf http://localhost:8080/filme/MainFrame

	public class RegelEinfuegen extends javax.servlet.http.HttpServlet {

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
				String loeschen = request.getParameter("loeschen");
				String name = request.getParameter("Name");
				if (name==null)
				{
					name="";
				}
				String beschreibung = request.getParameter("beschreibung");
				if (beschreibung==null)
				{
					beschreibung="";
				}
				String mode = request.getParameter("mode");
				String strRuleId = request.getParameter("rule_id");
				Hashtable rule=new Hashtable();
				rule.put("name", name);
				rule.put("beschreibung", beschreibung);
				rule.put("mode", mode);
				rule.put("command", "dummy");
				if (loeschen != null)
				{
					//String title1 = request.getParameter("title1");
					HeaderFooter hf = new HeaderFooter();
					out.println("<html>");
					out.println("<body>");
					hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
					out.println("<p>");
					out.println("Regel wird gelöscht...");
					out.println("<p>");
					if (db.deleteRule(new Integer(strRuleId)))
					{
					db.deleteRuleItem(new Integer(strRuleId));
					out.println("Regel erfolgreich gelöscht");
					
					}
					else
					{
						out.println("<font color=\"red\">!!!Regel konte nicht gelöscht werden!!!</font>");
					}
					out.println("</body>");
					out.println("</html>");
					return;
				}
				if (strRuleId==null || strRuleId.equals("null"))
				{
					//Regel existiert noch nicht => neu
					Integer rule_id =db.getHighestId("rules", "rule_id")+1;
					rule.put("rule_id", rule_id);
					db.insertRule(rule);					
				}
				else
				{
					//Regel existiert 
					System.out.println("rule_id "+rule);
					db.deleteRuleItem(new Integer(strRuleId));
					rule.put("rule_id", new Integer(strRuleId));
					db.updateRule(rule);
				}
				
				//Hier werden die ganzen Filter eingelesen
				Integer count=0;
				boolean done=false;
				String conjunc;
				boolean first=true;
				if (mode.equals("alle"))
				{
					conjunc=" AND ";
				}
				else
				{
					conjunc=" OR ";
				}
				String command="";
				Integer anz=new Integer (request.getParameter("filter_anzahl"));
				System.out.println("anz = "+anz);
				while (! done)
				{
					String filter = request.getParameter("filterSelect"+count.toString());
					System.out.println("Filter "+filter);
					System.out.println("count "+count);
					if (filter==null||filter.equals("null"))
					{
						count++;
						if (count>=anz)
						{
							done=true;
						}
						continue;
					}
					if (filter.equals("title"))
					{
						String operator=request.getParameter("titleOperator"+count.toString());
						if (operator==null)
						{
							operator="eq";
						}
						String title = request.getParameter("title"+count.toString());
						System.out.println("Regel "+count+" Name "+operator+" "+title);
						rule.put("art","title");
						rule.put("operator",operator);
						rule.put("value",title);
						db.insertRuleItem(rule);
						if (!first)
						{
							command=command+conjunc;
						}
						else
						{
							first=false;
						}
						command=command+" name ";
						if (operator.equals("eq"))
						{
							command=command+" = ''"+title+"''";
						}
						if (operator.equals("ne"))
						{
							command=command+" != ''"+title+"''";
						}
						if (operator.equals("bw"))
						{
							command=command+" like ''"+title+"%''";
						}
						if (operator.equals("ew"))
						{
							command=command+" like ''%"+title+"''";
						}
						if (operator.equals("ct"))
						{
							command=command+" like ''%"+title+"%''";
						}
						count++;
					}
					if (filter.equals("category"))
					{
						String operator=request.getParameter("categoryOp"+count.toString());
						if (operator==null)
						{
							operator="eq";
						}
						String title = request.getParameter("categoryId"+count.toString());
						System.out.println("Regel "+count+" Kategorie "+operator+" "+title);
						rule.put("art","category");
						rule.put("operator",operator);
						rule.put("value",title);
						db.insertRuleItem(rule);
						if (!first)
						{
							command=command+conjunc;
						}
						else
						{
							first=false;
						}
						command=command+" kategorie ";
						if (operator.equals("eq"))
						{
							command=command+" = "+title;
						}
						if (operator.equals("ne"))
						{
							command=command+" != "+title;
						}
						count++;
					}
					if (filter.equals("rule"))
					{
						String operator=request.getParameter("ruleOp"+count.toString());
						if (operator==null)
						{
							operator="eq";
						}
						String title = request.getParameter("ruleId"+count.toString());
						System.out.println("Regel "+count+" Regel "+operator+" "+title);
						rule.put("art","rule");
						rule.put("operator",operator);
						rule.put("value",title);
						db.insertRuleItem(rule);
						if (!first)
						{
							command=command+conjunc;
						}
						else
						{
							first=false;
						}
						if(operator.equals("eq"))
						{
						command=command+"( "+db.getRuleCommand(new Integer(title)).replaceAll("\\'", "\\\\'")+" ) ";
						}
						else
						{
							command=command+" NOT( "+db.getRuleCommand(new Integer(title)).replaceAll("\\'", "\\\\'")+" ) ";
						}
						count++;
					}
					if (filter.equals("konto"))
					{
						String operator=request.getParameter("kontoOp"+count.toString());
						if (operator==null)
						{
							operator="eq";
						}
						String title = request.getParameter("kontoId"+count.toString());
						System.out.println("Regel "+count+" Konto "+operator+" "+title);
						rule.put("art","konto");
						rule.put("operator",operator);
						rule.put("value",title);
						db.insertRuleItem(rule);
						if (!first)
						{
							command=command+conjunc;
						}
						else
						{
							first=false;
						}
						command=command+" konto_id ";
						if (operator.equals("eq"))
						{
							command=command+" = "+title;
						}
						if (operator.equals("ne"))
						{
							command=command+" != "+title;
						}
						count++;
					}
					if (filter.equals("betrag"))
					{
						String operator=request.getParameter("betragOperator"+count.toString());
						if (operator==null)
						{
							operator="eq";
						}
						String title = request.getParameter("betrag"+count.toString());
						System.out.println("Regel "+count+" Betrag "+operator+" "+title);
						rule.put("art","betrag");
						rule.put("operator",operator);
						rule.put("value",title);
						db.insertRuleItem(rule);
						if (!first)
						{
							command=command+conjunc;
						}
						else
						{
							first=false;
						}
						command=command+" wert ";
						if (operator.equals("eq"))
						{
							command=command+" = "+title;
						}
						if (operator.equals("lt"))
						{
							command=command+" < "+title;
						}
						if (operator.equals("le"))
						{
							command=command+" <= "+title;
						}
						if (operator.equals("gt"))
						{
							command=command+" > "+title;
						}
						if (operator.equals("ge"))
						{
							command=command+" >= "+title;
						}
						if (operator.equals("ne"))
						{
							command=command+" != "+title;
						}
						count++;
					}
					if (count>=anz)
					{
						done=true;
					}
				}
				command = "("+ command +")";
				System.out.println("Command = "+command);
				db.updateRuleCommand((Integer)rule.get("rule_id"), command);
				//String filter = request.getParameter("filterSelect1");
				//String title = request.getParameter("titleOperator1");
				//String title1 = request.getParameter("title1");
				HeaderFooter hf = new HeaderFooter();
				out.println("<html>");
				out.println("<body>");
				
				hf.writeHeader(out,(String)((Hashtable)session.getAttribute("settings")).get("instance"));
				out.println("<p>");
				out.println("Regel wird updatet...");
				out.println(name);
				out.println("<p>");
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

				